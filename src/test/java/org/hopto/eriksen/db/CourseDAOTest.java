package org.hopto.eriksen.db;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.hibernate.Session;
import org.hibernate.SessionException;
import org.hopto.eriksen.core.Course;
import org.hopto.eriksen.core.Recipe;
import org.hopto.eriksen.core.RecipeInstruction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * The intension is not to have full test coverage, 
 * more to have a quick way to test that all entity classes loads and also be able to test the tricky parts.
 * 
 * mvn  clean -Dtest=org.hopto.eriksen.db.CourseDAOTest test | tee kasta.txt
 * 
 * @author jens
 *
 */
public class CourseDAOTest {

	CourseDAO courseDAO = new CourseDAO(HsqldbSessionFactoryFactory.getSessionFactory());
	private static final Logger LOGGER = LoggerFactory.getLogger(CourseDAOTest.class.getSimpleName());

	private int courseId = -1;
	private static final String COURSE_TITLE = "Course title";
	private static final String COURSE_COMMENT = "course comment";
	private static final String COURSE_SECOND_COMMENT = "course second comment";
	
	@Test
	public void testCreateAFullCourse() {
		LOGGER.info("Starting test case testCreateAFullCourse");
		currentSession().beginTransaction();
		
		Course course = new Course();
		course.setTitle(COURSE_TITLE);
		course.setComment(COURSE_COMMENT);
		// Need to save the course so that the recipes below will be able to set its id
		courseDAO.saveOrUpdate(course);
		
		Recipe r1 = new Recipe();
		r1.setName("R1");
		r1.addInstruction("R1 I1");
		r1.addInstruction("R1 I2");
		r1.addInstruction("R1 I3");
		course.addRecipe(r1);
		
		Recipe r2 = new Recipe();
		r2.setName("R2");
		r2.addInstruction("R2 I1");
		r2.addInstruction("R2 I2");
		course.addRecipe(r2);
		
		courseId = course.getCourseId();
		Assert.assertNotNull(courseId, "The primary key shall not be null");
		
		currentSession().getTransaction().commit();	
		currentSession().close();
		LOGGER.info("End of test case testCreateAFullCourse");
	}
	
	@Test(dependsOnMethods = "testCreateAFullCourse")
	public void readAndTestCourse() {
		LOGGER.info("Starting test case readAndTestCourse");
		currentSession().beginTransaction();
		Assert.assertNull(courseDAO.findById(-1), "A non existing id shall not create havoc");
		
		Course course = courseDAO.findById(courseId);
		
		Assert.assertEquals(course.getTitle(), COURSE_TITLE);
		Assert.assertEquals(course.getComment(), COURSE_COMMENT);
		Assert.assertNotNull(course.getLastUpdated(), "The time shall have been set");
		
		Set<Recipe> recipes = course.getRecipes();
		Assert.assertEquals(recipes.size(), 2, "Two recipes shall have been found");
		
		Recipe r1 = course.getRecipeByName("R1");
		Assert.assertNotNull(r1, "The method getRecipeByName shall have found a recipe with name R1 for this course.");
		Assert.assertEquals(r1.getName(), "R1");
		
		List<RecipeInstruction> instructions = r1.getRecipeInstructions();
		Assert.assertEquals(instructions.size(), 3, "Tree instructions shall have been found");

		Assert.assertEquals("R1 I1", instructions.get(0).getInstruction());
		Assert.assertEquals("R1 I2", instructions.get(1).getInstruction());
		Assert.assertEquals("R1 I3", instructions.get(2).getInstruction());
		Assert.assertTrue(instructions.get(0).getInstructionOrder() < instructions.get(1).getInstructionOrder());
		Assert.assertTrue(instructions.get(1).getInstructionOrder() < instructions.get(2).getInstructionOrder());
		
		LOGGER.info("The Course with recipes looks like: " + course.toString() );
		
		currentSession().getTransaction().commit();	
		currentSession().close();
		LOGGER.info("End of test case readAndTestCourse");
	}
	
	// Tests that a update works for saveOrUpdate
	@Test(dependsOnMethods = "readAndTestCourse")
	public void testUpdateForMethodSaveOrUpdate() {
		LOGGER.info("Starting test case testUpdateForMethodSaveOrUpdate");
		currentSession().beginTransaction();

		Course course = courseDAO.findById(courseId);		
		course.setComment(COURSE_SECOND_COMMENT);
		
//		Recipe modifiedRecipe = new Recipe();
//		modifiedRecipe.setName("R1");
		
		Recipe r1 = course.getRecipeByName("R1");
		// Modify the instructions, final result shall be; ["R1 I3", "R1 I1"]
		List<RecipeInstruction> instructions = r1.getRecipeInstructions();
		instructions.remove(1);
		Collections.swap(instructions, 0, 1);
		LOGGER.info("The instruction list now looks like: " + instructions.toString() );
		r1.setRecipeInstructions(instructions);
		
		currentSession().getTransaction().commit();
		currentSession().close();
		LOGGER.info("End of test case testUpdateForMethodSaveOrUpdate");
	}
	
	@Test(dependsOnMethods = "testUpdateForMethodSaveOrUpdate")
	public void testReadUpdateCourse() {
		LOGGER.info("Starting test case testReadUpdateCourse");
		currentSession().beginTransaction();

		Course course = courseDAO.findById(courseId);		
		Assert.assertEquals(course.getComment(), COURSE_SECOND_COMMENT);
		
		Recipe r1 = course.getRecipeByName("R1");
		List<RecipeInstruction> instructions = r1.getRecipeInstructions();
		Assert.assertEquals(instructions.size(), 2);
		Assert.assertEquals("R1 I3", instructions.get(0).getInstruction());
		Assert.assertEquals("R1 I1", instructions.get(1).getInstruction());
		Assert.assertTrue(instructions.get(0).getInstructionOrder() < instructions.get(1).getInstructionOrder());
		
		LOGGER.info("The course in method testReadUpdateCourse looks like: " + course.toString());
		currentSession().getTransaction().commit();
		currentSession().close();
		LOGGER.info("End of test case testReadUpdateCourse");
	}
	
	// Needs to be last, since it deletes the ID 
	@Test(dependsOnMethods = "testReadUpdateCourse")
	public void testDelet() {
		LOGGER.info("Starting test case testDelet");
		currentSession().beginTransaction();
        
		Course course = courseDAO.findById(courseId);
		
		LOGGER.info("Will delete a course that looks like: " + course.toString());
		courseDAO.delete(course);
		currentSession().flush();
		currentSession().clear();
		
		Assert.assertNull(courseDAO.findById(courseId), "After a delete shall it not be possible to find the id");
		currentSession().getTransaction().commit();
		currentSession().close();
		LOGGER.info("End of test case testDelet");
	}
	
	public Session currentSession() {
		Session session;

		try { 
			session =  HsqldbSessionFactoryFactory.getSessionFactory().getCurrentSession();
		} catch (SessionException se) {
			session = HsqldbSessionFactoryFactory.getSessionFactory().openSession();
		}
		return session;
	}

}
