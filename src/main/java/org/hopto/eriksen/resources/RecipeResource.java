package org.hopto.eriksen.resources;

import io.dropwizard.jersey.caching.CacheControl;

import java.net.URI;
import java.util.List;
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

import org.hopto.eriksen.api.Part;
import org.hopto.eriksen.api.Recipe;
import org.hopto.eriksen.db.RecipePersister;
import org.hopto.eriksen.resources.util.CustomNotFoundException;
import org.hopto.eriksen.resources.util.CustomUnprocessableEntityException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.annotation.Timed;
import com.google.inject.Inject;

// TO REMEMBER: Use plural nouns (seems to be the recommended way)
@Path("/recipes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RecipeResource {

	private static final Logger LOGGER = LoggerFactory.getLogger(RecipeResource.class.getSimpleName());

	private RecipePersister recipePersister;

	@Inject
	public RecipeResource(RecipePersister recipePersister) {
		this.recipePersister = recipePersister;
	}

	// curl -v -H "Accept: application/json" http://localhost:8080/recipes
	// curl -v -H "Accept: application/json" "http://localhost:8080/recipes?page=1&size=1"
	@GET
	@Timed
	public List<Recipe> listAll(
			@DefaultValue("1") @QueryParam("page") int pageNumber,
			@DefaultValue("50") @QueryParam("size") int pageSize) {
		
		LOGGER.info("method list, produces: " + MediaType.APPLICATION_JSON + ", query params: page=" + pageNumber + ", size=" + pageSize);
		
		if(pageNumber < 1) {
			throw new CustomUnprocessableEntityException("Query parameter \"page\" must be bigger than zero");
		}
		if(pageSize < 1) {
			throw new CustomUnprocessableEntityException("Query parameter \"size\" must be bigger than zero");
		}
		
		List<Recipe> recipes = recipePersister.getAll(pageNumber, pageSize);
		
		return recipes;
	}	
	
	// curl -v -H "Content-Type: application/json" -X POST -d '{"title":"test","comment":"comment1"}' http://localhost:8080/recipes
	@POST
	@Timed
	public Response saveItem(@Valid Recipe recipe) {
		recipePersister.save(recipe);
		URI tmpUri = UriBuilder
				.fromResource(RecipeResource.class)
				.path("/{id}")
				.build(recipe.getId());
			
		LOGGER.debug("The produced uri: " + tmpUri.toString() );
		return Response.created(tmpUri).build();
	}

	// curl -v -H "Accept: application/json" http://localhost:8080/recipes/1	
	@GET
	@Path("/{id}")
	@CacheControl(maxAge = 1, maxAgeUnit = TimeUnit.HOURS)
	public Recipe getIdAsJson(@PathParam("id") Long id) {
		LOGGER.debug("method getIdAsJson with id as " + id + ", produces " + MediaType.APPLICATION_JSON);

		Recipe recipe = recipePersister.findById(id);
		if (recipe == null) {
			LOGGER.error("A recipe with database id " + id + " was not found");
			throw new CustomNotFoundException("A recipe with database id " + id + " was not found");
		}
		// This response shall in line with HATEOAS also contain a "parts: {rel: recipes/parts, href=http://.../recipes/{id}/parts} "
		// It is the "rel: recipes/parts" that forever needs to the same, the href part can be changed 

		// It shall also contain a "self" url with href attribute that points to the current resource itself.  
		return recipe;
	}

	// curl -v -X DELETE  http://localhost:8080/recipes/1
	@DELETE
	@Path("/{id}")
	public void delete(@PathParam("id") Long id) {
		LOGGER.debug("Will try to delete item with id " + id);
		Recipe recipe = recipePersister.findById(id);
		if (recipe == null) {
			LOGGER.error("A recipe with database id " + id + " was not found");
			throw new CustomNotFoundException("A recipe with database id " + id + " was not found");
		}
		recipePersister.delete(recipe);	
	}
	
	/**
	 * 
	 * @param id
	 * @return
	 */
	// TO REMEMBER: The relation type between recipes and pars is of type composition, use a sub-resource url
	@GET
	@Path("/{id}/parts")
	public List<Part> getPartsAsJson(@PathParam("id") Long id) {
		LOGGER.debug("method getPartsAsJson with id as " + id + ", produces " + MediaType.APPLICATION_JSON);

		Recipe recipe = recipePersister.findById(id);
		if (recipe == null) {
			throw new CustomNotFoundException("A recipe with database id " + id + " was not found");
		}
		return recipe.getParts();
	}
	
	// curl -v -H "Content-Type: application/json"  -X POST -d '{"name":"part 1","instructions": ["instruction1", "instruction2"]}' http://localhost:8080/recipes/1/parts
	@POST
	@Timed
	@Path("/{id}/parts")
	public Response postpart(@PathParam("id") Long id, @Valid Part part) {
		
		Recipe recipe = recipePersister.findById(id);
		if (recipe == null) {
			LOGGER.error("A recipe with database id " + id + " was not found");
			throw new CustomNotFoundException("A recipe with database id " + id + " was not found");
		}
		
		// TODO Check if the part is new or shall be updated
		recipe.addCPart(part);
		recipePersister.save(recipe);
		
		return null;
	}
	
	@DELETE
	@Path("/{id}/parts/{partId}")
	public Part getPartById(@PathParam("id") Long id, @PathParam("partId") Long partId) {
		Recipe recipe = recipePersister.findById(id);
		if (recipe == null) {
			LOGGER.error("A recipe with database id " + id + " was not found");
			throw new CustomNotFoundException("A recipe with database id " + id + " was not found");
		}
		return null;
	}
}
