package org.hopto.eriksen.resources;

import static com.jayway.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

import org.hopto.eriksen.core.Course;
import org.hopto.eriksen.core.Recipe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

// To remember about rest-assured 2.0 syntax:  "given / when / then"
@Test(groups = { "integration" })
public class CourseResourceExceptionsIT {

	private static final Logger LOGGER = LoggerFactory.getLogger(CourseResourceExceptionsIT.class.getName());
	static final String JSON = "application/json";
	private final String baseUrl = "http://localhost:8080/courses";
	
//	RestAssured.baseURI = "http://myhost.org";
//	RestAssured.port = 80;

	// TODO Test that a post with a null title gives a error response!
	
	 @Test
	 public void testGetNonExistingCourse() {
		LOGGER.info("Running test testGetNonExistingCourse");
		given().
			header("Content-Type", JSON).
		when().
			get(baseUrl + "/12345").
		then().
			statusCode(404).
			body("name", not(isEmptyString())).
			body("description", not(isEmptyString()));
		
		LOGGER.info("End of test testGetNonExistingCourse");
	 }
	


	@Test
	public void testGetNonExistingRecipe() {
		LOGGER.info("Running test testGetNonExistingRecipe");
		
		Course course = new Course("testGetNonExistingRecipe title", "Only used for test");
		
		// Create a course to get a valid course recourse.
		String courseUrl =
		given().
			contentType(JSON).
			body(course).
		when().
			post(baseUrl).
		then().
			statusCode(201).
			header("Location",  containsString("http")).
		extract().
			header("Location"); 
		
		given().
			contentType(JSON).
		when().
			get(courseUrl + "/recipes/1").	// valid but non existing url
		then().
			statusCode(404).
			body("name", not(isEmptyString())).
			body("description", not(isEmptyString()));
		
		LOGGER.info("End of test testGetNonExistingRecipe");
	}


	@Test
	public void testCreateTwoEqualRecipesShallFail() {
		LOGGER.info("Running test testCreateTwoEqualRecipesShallFail");
		Course course = new Course("testCreateTwoEqualRecipesShallFail title", "Only used for test");
		Recipe recipe = new Recipe();
		recipe.setName("testCreateTwoEqualRecipesShallFail name");
		
		// Create a course to get a valid course recourse.
		String courseUrl =
		given().
			contentType(JSON).
			body(course).
		when().
			post(baseUrl).
		then().
			statusCode(201).
			header("Location",  containsString("http")).
		extract().
			header("Location");
		
		// Create a recipe
		given().
			contentType(JSON).
			body(recipe).
			log().body().
		when().
			post(courseUrl + "/recipes").
		then().
			log().ifValidationFails().
			statusCode(201);
		
		// Try to create the same recipe again (shall fail).
		given().
			contentType(JSON).
			body(recipe).
		when().
			post(courseUrl + "/recipes").
		then().
			log().ifValidationFails().
			statusCode(409).
			contentType(JSON).
//			body("errorCode", equalTo("409")).
			body("name", equalTo("conflict")).
			body("description", not(isEmptyString()));
		LOGGER.info("End of testCreateTwoEqualRecipesShallFail");
	}

	@Test
	public void testCreateRecipeWithToShortName() {
		LOGGER.info("Running test testCreateRecipeWithToShortName");
		Course course = new Course("testCreateRecipeWithToShortName title", "Only used for test");
		Recipe recipe = new Recipe();
		// The min length is 3
		recipe.setName("AB");
		
		// Create a course to get a valid course recourse.
		String courseUrl =
		given().
			contentType(JSON).
			body(course).
		when().
			post(baseUrl).
		then().
			statusCode(201).
			header("Location",  containsString("http")).
		extract().
			header("Location");
		
		// Create a recipe
		LOGGER.debug("Will try and fail when posting " + recipe + " to " + courseUrl);
		given().
			contentType(JSON).
			body(recipe).
			log().body().
		when().
			post(courseUrl + "/recipes").
		then().
			log().ifValidationFails().
			statusCode(422);	// TODO This shall maybe be something else!!!
		
		LOGGER.info("End of test testCreateRecipeWithToShortName");
	}
	
	// TODO when posting a recipe with empty name shall a valid response be given  
	
}
