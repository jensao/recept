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
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.hopto.eriksen.core.Course;
import org.hopto.eriksen.core.Recipe;
import org.hopto.eriksen.db.CourseDAO;
import org.hopto.eriksen.resources.util.CustomNotFoundException;
import org.hopto.eriksen.resources.util.CustomUnprocessableEntityException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.annotation.Timed;

@Path("/courses")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CourseResource {

	private final CourseDAO courseDAO; 
	private static final Logger LOGGER = LoggerFactory.getLogger(CourseResource.class.getSimpleName());
	
	public CourseResource(CourseDAO courseDAO) {
		this.courseDAO = courseDAO;
	}
	
	// curl -v -H "Accept: application/json" http://localhost:8080/courses
	// curl -v -H "Accept: application/json" "http://localhost:8080/courses?page=1&size=1"
	@GET
	@Timed
	@UnitOfWork
	public List<Course> listAll(
			@DefaultValue("1") @QueryParam("page") int pageNumber,
			@DefaultValue("50") @QueryParam("size") int pageSize) {
		
		LOGGER.info("method list, produces: " + MediaType.APPLICATION_JSON + ", query params: page=" + pageNumber + ", size=" + pageSize);
		
		if(pageNumber < 1) {
			throw new CustomUnprocessableEntityException("Query parameter \"page\" must be bigger than zero");
		}
		if(pageSize < 1) {
			throw new CustomUnprocessableEntityException("Query parameter \"size\" must be bigger than zero");
		}
		
//		List<Recipe> recipes = courseDAO.getAllPaged(pageNumber, pageSize);
		List<Course> courses = courseDAO.findAll();
		
		return courses;
	}
	
	// curl -v -H "Content-Type: application/json" -X POST -d '{"title":"test","comment":"comment1"}' http://localhost:8080/courses
	@POST
	@Timed
	@UnitOfWork
	public Response saveItem(@Valid Course course) {
		courseDAO.saveOrUpdate(course);
		URI tmpUri = UriBuilder
				.fromResource(CourseResource.class)
				.path("/{id}")
				.build(course.getCourseId());
			
		LOGGER.debug("The produced uri: " + tmpUri.toString() );
		return Response.created(tmpUri).build();
	}
	
	
	// curl -v -H "Accept: application/json" http://localhost:8080/courses/1	
	@GET
	@Path("/{id}")
	@CacheControl(maxAge = 1, maxAgeUnit = TimeUnit.HOURS)
	@UnitOfWork
	public Course getIdAsJson(@PathParam("id") int id) {
		LOGGER.debug("method getIdAsJson with id as " + id + ", produces " + MediaType.APPLICATION_JSON);

		Course course = courseDAO.findById(id);
		if (course == null) {
			LOGGER.error("A course with database id " + id + " was not found");
			throw new CustomNotFoundException("A course with database id " + id + " was not found");
		}
		// This response shall in line with HATEOAS also contain a "parts: {rel: course/recipe, href=http://.../courses/{id}/recipes} "
		// It is the "rel: course/recipe" that forever needs to the same, the href part can be changed 

		// It shall also contain a "self" url with href attribute that points to the current resource itself.  
		return course;
	}
	
	// curl -v -X DELETE  http://localhost:8080/courses/1
	@DELETE
	@Path("/{id}")
	@UnitOfWork
	public void delete(@PathParam("id") int id) {
		LOGGER.debug("Will try to delete item with id " + id);
		Course recipe = courseDAO.findById(id);
		if (recipe == null) {
			LOGGER.error("A recipe with database id " + id + " was not found");
			throw new CustomNotFoundException("A recipe with database id " + id + " was not found");
		}
		courseDAO.delete(recipe);	
	}
	
	/**
	 * 
	 * @param id
	 * @return
	 */
	// TO REMEMBER: The relation type between course and recipe is of type composition, use a sub-resource url
	@GET
	@UnitOfWork
	@Path("/{id}/recipes")
	public Set<Recipe> getRecipes(@PathParam("id") int id) {
		LOGGER.debug("method getRecipes with id as " + id + ", produces " + MediaType.APPLICATION_JSON);

		Course course = courseDAO.findById(id);
		if (course == null) {
			throw new CustomNotFoundException("A course with database id " + id + " was not found");
		}
		return course.getRecipes();
	}
	
	// curl -v -H "Content-Type: application/json"  -X POST -d '{"name":"part 1","instructions": ["instruction1", "instruction2"]}' http://localhost:8080/courses/1/recipes
	@POST
	@Timed
	@UnitOfWork
	@Path("/{id}/recipes")
	public Response postRecipe(@PathParam("id") int id, @Valid Recipe recipe) {
		
		Course course = courseDAO.findById(id);
		if (course == null) {
			LOGGER.error("A recipe with database id " + id + " was not found");
			throw new CustomNotFoundException("A course with database id " + id + " was not found");
		}
		
		// TODO Check if the part is new or shall be updated
		course.addRecipe(recipe);
		courseDAO.saveOrUpdate(course);
		
		return null;
	}
	
}
