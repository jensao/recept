package org.hopto.eriksen.resources;

import static com.jayway.restassured.RestAssured.get;
import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.RestAssured.when;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.isEmptyString;
import static org.hamcrest.Matchers.not;

import java.util.Arrays;
import java.util.List;

import org.hopto.eriksen.core.Course;
import org.hopto.eriksen.core.Recipe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.jayway.restassured.response.Response;

public class CourseResourceBasicCrudIT {

	private static final Logger LOGGER = LoggerFactory.getLogger(CourseResourceBasicCrudIT.class.getName());
	static final String JSON = "application/json";
	private Course course;
	private String courseUrl;
	private Recipe recipe1;
	private String recipe1Url;
	private Recipe recipe2;
	private String recipe2Url;
	
//	RestAssured.baseURI = "http://myhost.org";
//	RestAssured.port = 80;
	
	@BeforeClass
	public void initilize() {
		course = new Course();
		course.setTitle("testTitle");
		course.setComment("testComment");
		
		recipe1 = new Recipe();
		recipe1.setName("testRecipe1Title");
		recipe1.addInstruction("1 ONE");
		recipe1.addInstruction("1 TWO");
		
		recipe2 = new Recipe();
		recipe2.setName("testRecipe2Title");
		recipe2.addInstruction("2 ONE");
		recipe2.addInstruction("2 TWO");
		recipe2.addInstruction("2 TREE");
	}
	
//	@Test(dependsOnMethods = "testPostCourse")
	@Test
	public void testGetCourses() {
		LOGGER.info("Running test testGetCourses");
		// TODO Test that the query param "page" and "size" works, test test that 400 is received if faulty values is supplied.
		get("http://localhost:8080/courses").then().assertThat().statusCode(200).and().contentType(JSON);
	}
	
	@Test
	public void testPostCourse()  {
		LOGGER.info("Running test testPostCourse");
		
		courseUrl =
		given().
			contentType(JSON).
//			body("{\"title\":\"test\",\"comment\":\"comment1\"}").
			body(course).
		when().
			post("http://localhost:8080/courses").
		then().
			statusCode(201).
			header("Location",  containsString("http")).
		extract().
			header("Location"); 
	}
	
	// Updates a course. in this TC will a new course be created but depends on the post TC anyway.
	@Test(dependsOnMethods = "testPostCourse") 
	public void testPutCourse() {
		LOGGER.info("Running test testPutCourse");
		
		Course orgCourse = new Course();
		orgCourse.setTitle("AAAAA");
		orgCourse.setComment("aaaaa");
		// TODO add a recipe here and test the behavior
		
		// Create a original
		LOGGER.debug("Will create a new course with following data: " + orgCourse.toString() );
		String tmpUrl =
		given().
			contentType(JSON).
			body(orgCourse).
		when().
			post("http://localhost:8080/courses").
		then().
			statusCode(201).
			header("Location",  containsString("http")).
		extract().
			header("Location");
		
		// Modify the original
		Course modCourse = new Course();
		modCourse.setTitle("BBBBB");
		modCourse.setComment("bbbbb");
		LOGGER.debug("Will modify course on resource " + tmpUrl +  " with follonging data: " + modCourse.toString());
		given().
			contentType(JSON).
			body(modCourse).
			log().body().
		when().
			put(tmpUrl).
		then().
			statusCode(204);
		
		// Read the modified version 
		LOGGER.debug("Will read the course resourse " + tmpUrl);
		given().
			header("Content-Type", JSON).
		when().
			get(tmpUrl).
		then().
			log().ifValidationFails().
			statusCode(200).
			contentType(JSON).
			body("title", equalTo(modCourse.getTitle() )).
			body("comment",  equalTo(modCourse.getComment() ));
		
		LOGGER.info("End of test testPutCourse");
	}
	
	@Test(dependsOnMethods = "testPostCourse")
	public void testGetCourse() {
		LOGGER.info("Running test testGetCourse");
		
		given().
			header("Content-Type", JSON).
		when().
			get(courseUrl).
		then().
			statusCode(200).
			contentType(JSON).
			body("title", equalTo(course.getTitle())).
			body("comment",  equalTo(course.getComment())).
			body("lastUpdated", not(isEmptyString())).		// Change to date matcher
			header("Cache-Control", "no-transform, max-age=3600");
	}
	
	@Test(dependsOnMethods = "testPostCourse")
	public void testPostRecipe() {
		LOGGER.info("Running test testPostRecipe");
		
		// Add the first recipe 
		recipe1Url = 
		given().
			contentType(JSON).
			body(recipe1).
		when().
			post(courseUrl + "/recipes").
		then().
			statusCode(201).
			header("Location",  containsString("http")).
		extract().
			header("Location"); 
		
		// Add the second recipe
		recipe2Url = 
		given().
			contentType(JSON).
			body(recipe2).
		when().
			post(courseUrl + "/recipes").
		then().
			statusCode(201).
			header("Location",  containsString("http")).
		extract().
			header("Location");
		
		LOGGER.info("After posting two recipes to " + courseUrl + " was the following Location urls recieved: " + recipe1Url + ", " + recipe2Url);
		Assert.assertNotEquals(recipe1Url, recipe2Url, "The location urls shall not be equal");
	}
	
	@Test(dependsOnMethods = "testPostRecipe")
	public void testGetRecipies() {
		LOGGER.info("Running test testGetRecipes");
		
		Response response = 
		given().
			contentType(JSON).
		when().
			get(courseUrl + "/recipes").
		then().
			statusCode(200).
		extract().
        	response(); 
		
		// http://stackoverflow.com/questions/21725093/rest-assured-deserialize-response-json-as-listpojo
		List<Recipe> resourceRecipes = Arrays.asList(response.as(Recipe[].class));

		Recipe resourceRecipe = findRecipeInSet(resourceRecipes, recipe1.getName());
		
		Assert.assertEquals(resourceRecipe.getName(), recipe1.getName());
		Assert.assertEquals(resourceRecipe.getRecipeInstructions().size() , 2);
		Assert.assertEquals(resourceRecipe.getRecipeInstructions().get(0).getInstruction() , recipe1.getRecipeInstructions().get(0).getInstruction() );
	}
	
	
	// Helper method
	public Recipe findRecipeInSet(List<Recipe> recipes, String name) {
		for (Recipe r : recipes) {
			if (r.getName().equals(name)) {
				return r;
			}
		}
		return null;
	}
	
	@Test(dependsOnMethods = "testPostRecipe")
	public void testGetRecipe() {
		LOGGER.info("Running test testGetRecipe");
		
		Recipe responseRecipe = 
		given().
			contentType(JSON).
			log().path().
			log().headers().
		when().
			get(recipe1Url).
		then().
			log().ifValidationFails().
			statusCode(200).
		extract().
			as(Recipe.class);
		
		Assert.assertEquals(responseRecipe.getName() , recipe1.getName());
		Assert.assertEquals(responseRecipe.getRecipeInstructions().get(0).getInstruction() , recipe1.getRecipeInstructions().get(0).getInstruction() );
		
		LOGGER.info("End of test testGetRecipe");
	}
	
	@Test(dependsOnMethods = "testGetRecipe") 
	public void testDeleteRecipe() {
		given().
			contentType(JSON).
		when().
			delete(recipe2Url).
		then().
			log().ifValidationFails().
			statusCode(204);
		
		// Test that only one recipe exist 
		Response response = 
		given().
			contentType(JSON).
		when().
			get(courseUrl + "/recipes").
		then().
			statusCode(200).
		extract().
        	response();
		
		List<Recipe> recipes = Arrays.asList(response.as(Recipe[].class));
		Assert.assertEquals(recipes.size(), 1);
		Recipe recipeOne = findRecipeInSet(recipes, recipe1.getName());		
		Assert.assertEquals(recipeOne.getName(), recipe1.getName());
	}
	
	@Test(dependsOnMethods = "testGetRecipe" )
	public void testPutRecipe() {
		LOGGER.info("Running test testPutRecipe");
		
		Recipe tmpRecipe = new Recipe();
		tmpRecipe.setName("aNewName");
		tmpRecipe.addInstruction("new 1");
		
		// Update a recipe 
		given().
			contentType(JSON).
			body(tmpRecipe).
			log().headers().
			log().body().
		when().
			put(recipe1Url).
		then().
			statusCode(204);
		
		// Get the recipe to check if the update was successfull
		given().
			contentType(JSON).
		when().
			get(recipe1Url).
		then().
			log().ifValidationFails().
			statusCode(200).
			contentType(JSON).
			body("name", equalTo(tmpRecipe.getName())).
			body("instructions.instruction", hasSize(1)).
			body("instructions.instruction", hasItems("new 1"));
		
		LOGGER.info("End of test testPutRecipe");
	}
	
	@Test(dependsOnMethods = "testPutRecipe" )
	public void testDeleteCourse() {
		LOGGER.info("Running test testDeleteCourse");
		when().
			delete(courseUrl).
		then().
			statusCode(204);
		
		LOGGER.debug("Will make a get request to " + courseUrl);
		when().
			get(courseUrl).
		then().
			statusCode(404);
		
		LOGGER.debug("Will make a get request to " + recipe1Url);
		when().
			get(recipe1Url).
		then().
			statusCode(404);
		
		LOGGER.info("End of test testDeleteCourse");
	}
	
	
	
}
