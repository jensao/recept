package org.hopto.eriksen.resources;

import io.dropwizard.hibernate.UnitOfWork;
import io.dropwizard.jersey.caching.CacheControl;

import java.net.URI;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.hopto.eriksen.core.Course;
import org.hopto.eriksen.core.Recipe;
import org.hopto.eriksen.core.RecipeInstruction;
import org.hopto.eriksen.db.CourseDAO;
import org.hopto.eriksen.db.RecipeDAO;
import org.hopto.eriksen.resources.util.CustomBadRequestException;
import org.hopto.eriksen.resources.util.CustomNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.annotation.Timed;

@Path("/courses")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CourseResource {

	private final CourseDAO courseDAO; 
	private final RecipeDAO recipeDAO;
	private static final Logger LOGGER = LoggerFactory.getLogger(CourseResource.class.getName());
	
	public CourseResource(CourseDAO courseDAO, RecipeDAO recipeDAO) {
		this.courseDAO = courseDAO;
		this.recipeDAO = recipeDAO;
	}
	
	// curl -v -H "Accept: application/json" http://localhost:8080/courses
	// curl -v -H "Accept: application/json" "http://localhost:8080/courses?page=1&size=1"
	@GET
	@Timed
	@UnitOfWork
	public List<Course> getCourses(
			@DefaultValue("1") @QueryParam("page") int pageNumber,
			@DefaultValue("50") @QueryParam("size") int pageSize) {
		
		LOGGER.info("method list, produces: " + MediaType.APPLICATION_JSON + ", query params: page=" + pageNumber + ", size=" + pageSize);
		
		if(pageNumber < 1) {
			throw new CustomBadRequestException("Query parameter \"page\" must be bigger than zero");
		}
		if(pageSize < 1) {
			throw new CustomBadRequestException("Query parameter \"size\" must be bigger than zero");
		}
		
//		List<Recipe> recipes = courseDAO.getAllPaged(pageNumber, pageSize);
		List<Course> courses = courseDAO.findAll();
		
		return courses;
	}
	
	// curl -v -H "Content-Type: application/json" -X POST -d '{"title":"test","comment":"comment1"}' http://localhost:8080/courses
	@POST
	@Timed
	@UnitOfWork
	public Response saveCourse(@Valid Course course) {
		courseDAO.saveOrUpdate(course);
		URI tmpUri = UriBuilder
				.fromResource(CourseResource.class)
				.path("/{id}")
				.build(course.getCourseId());
			
		LOGGER.debug("A post request created a new course on: " + tmpUri.toString() );
		return Response.created(tmpUri).build();
	}
	
	@PUT
	@Timed
	@UnitOfWork
	@Path("/{courseId}")
	public void putCourse(
			@PathParam("courseId") int courseId, 
			@Valid Course updatedCourse) {
		
		Course existingCourse = courseDAO.findById(courseId);
		if(existingCourse == null) {
			LOGGER.info("A course with database id " + courseId + " was not found");
			throw new CustomNotFoundException("A coures with id " + courseId + " was not found");
		}
		else {
			// a merge here will not work due to that the @version on the "lastUpdated" field will create a StaleObjectStateException
			existingCourse.setTitle(updatedCourse.getTitle());
			existingCourse.setComment(updatedCourse.getComment());
			courseDAO.saveOrUpdate(existingCourse);
		}
		
	}
	
	// curl -v -H "Accept: application/json" http://localhost:8080/courses/1	
	@GET
	@Path("/{courseId}")
	@Timed
	@CacheControl(maxAge = 1, maxAgeUnit = TimeUnit.HOURS)
	@UnitOfWork
	public Course getCourse(@PathParam("courseId") int courseId) {
		LOGGER.debug("method getIdAsJson with id as " + courseId + ", produces " + MediaType.APPLICATION_JSON);

		Course course = courseDAO.findById(courseId);
		if (course == null) {
			LOGGER.info("A course with database id " + courseId + " was not found");
			throw new CustomNotFoundException("A course with database id " + courseId + " was not found");
		}
		// This response shall in line with HATEOAS also contain a "parts: {rel: course/recipe, href=http://.../courses/{id}/recipes} "
		// It is the "rel: course/recipe" that forever needs to the same, the href part can be changed 

		// It shall also contain a "self" url with href attribute that points to the current resource itself.  
		return course;
	}
	
	// curl -v -X DELETE  http://localhost:8080/courses/1
	@DELETE
	@Timed
	@Path("/{courseId}")
	@UnitOfWork
	public void deleteCourse(@PathParam("courseId") int courseId) {
		LOGGER.debug("Will try to delete course with id " + courseId);
		Course course = courseDAO.findById(courseId);
		if (course == null) {
			LOGGER.info("A course with database id " + courseId + " was not found");
			throw new CustomNotFoundException("A recipe with database id " + courseId + " was not found");
		}
		
		// TODO Create a response here
		courseDAO.delete(course);	
	}
	
	/**
	 * 
	 * @param courseId
	 * @return
	 */
	// TO REMEMBER: The relation type between course and recipe is of type composition, use a sub-resource url
	// curl -v -H "Accept: application/json" http://localhost:8080/courses/1/recipes
	@GET
	@Timed
	@UnitOfWork
	@Path("/{courseId}/recipes")
	public Set<Recipe> getRecipes(@PathParam("courseId") int courseId) {
		LOGGER.debug("method getRecipes with id as " + courseId + ", produces " + MediaType.APPLICATION_JSON);

		Course course = courseDAO.findById(courseId);
		if (course == null) {
			LOGGER.info("A course with database id " + courseId + " was not found");
			throw new CustomNotFoundException("A course with database id " + courseId + " was not found");
		}
		
//		To prevent lazy loading exception, see stackoverflow		
		course.getRecipes().size();
		return course.getRecipes();
	}
	
	// curl -v -H "Content-Type: application/json"  -X POST -d '{"name":"part 1","instructions": ["instruction1", "instruction2"]}' http://localhost:8080/courses/1/recipes
	@POST
	@Timed
	@UnitOfWork
	@Path("/{courseId}/recipes")
	public Response postRecipe(@PathParam("courseId") int courseId, @Valid Recipe recipe) {
		
		Course course = courseDAO.findById(courseId);
		if (course == null) {
			LOGGER.info("A course with database id " + courseId + " was not found");
			throw new CustomNotFoundException("A course with database id " + courseId + " was not found");
		}
		LOGGER.debug("Recived a post request with recipe data: " + recipe.toString());
		
		for (RecipeInstruction ri : recipe.getRecipeInstructions()) {
			ri.setRecipe(recipe);
		}
		
		// TODO Check if the part is new or shall be updated? If it is equal will a IllegalArgumentException be thrown and a 500 produced handle this!!!
		course.addRecipe(recipe);
		courseDAO.saveOrUpdate(course);
		courseDAO.flush();
		
		LOGGER.info("The inserted recipe looks like: " + recipe.toString());
		
		URI tmpUri = UriBuilder
				.fromResource(CourseResource.class)
				.path("/{courseId}/recipes/{recipeId}")
				.build(course.getCourseId(), recipe.getRecipeId());
		
		LOGGER.debug("The produced uri: " + tmpUri.toString() );
		return Response.created(tmpUri).build();
	}
	
	/**
	 * This intentionally breaks the HTTP put definition that says that a put shall "create if not exist"
	 * Only update to an existing DB id is allowed.
	 * 
	 * @param courseId
	 * @param recipeId
	 * @param updatedRecipe
	 * @return
	 */
	
	@PUT
	@Timed
	@UnitOfWork
	@Path("/{courseId}/recipes/{recipeId}")
	public Response putRecipe(
			@PathParam("courseId") int courseId, 
			@PathParam("recipeId") int recipeId, 
			@Valid Recipe updatedRecipe) {

		Recipe existingRecipe = recipeDAO.findById(recipeId);
		
		if (existingRecipe == null || courseId != existingRecipe.getCourse().getCourseId()) {
			LOGGER.info("A course with database id " + courseId + " was not found");
			throw new CustomNotFoundException("A coures with id " + courseId + " with a bellonging recipe with database id " + recipeId + " was not found");
		}
		else {
			updatedRecipe.setCourse(existingRecipe.getCourse());
			updatedRecipe.setRecipeId(recipeId);

			for(RecipeInstruction ri : updatedRecipe.getRecipeInstructions()) {
				ri.setRecipe(updatedRecipe);
			}
			
			LOGGER.debug("Will uppdate old recipe: " + existingRecipe.toString() + " with id " + recipeId + 
					" belonging to a course with id " + courseId + " with the new data: "  + updatedRecipe.toString());  
			
			recipeDAO.merge(updatedRecipe);
			// TODO Send a 204 here instead since the response doesn't include any content 
			return Response.ok().build();
		}
	}
	
	@GET
	@Timed
	@UnitOfWork
	@Path("/{courseId}/recipes/{recipeId}")
	public Recipe getRecipe( 
			@PathParam("courseId") int courseId, 
			@PathParam("recipeId") int recipeId) {

		Recipe recipe = recipeDAO.findById(recipeId);
		
		if (recipe == null || courseId != recipe.getCourse().getCourseId()) {
			LOGGER.info("A course with database id " + courseId + " was not found");
			throw new CustomNotFoundException("A coures with id " + courseId + " with a bellonging recipe with database id " + recipeId + " was not found");
		}
		else {
			return recipe;
		}
	}
	
	// curl -v -X DELETE http://localhost:8080/courses/1/recipes/2
	@DELETE
	@Timed
	@UnitOfWork
	@Path("/{courseId}/recipes/{recipeId}")
	public void deleteRecipe(
			@PathParam("courseId") int courseId, 
			@PathParam("recipeId") int recipeId){
		
		Recipe recipe = recipeDAO.findById(recipeId);
		
		if (recipe == null || courseId != recipe.getCourse().getCourseId()) {
			LOGGER.info("A course with database id " + courseId + " was not found");
			throw new CustomNotFoundException("A coures with id " + courseId + " with a bellonging recipe with database id " + recipeId + " was not found");
		}
		else {
			recipeDAO.delete(recipe);
		}
		
		// Jersey will create a 204 resposne here
	}
	
}
