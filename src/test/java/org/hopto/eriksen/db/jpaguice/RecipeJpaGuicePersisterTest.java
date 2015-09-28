package org.hopto.eriksen.db.jpaguice;

import java.util.Set;

import org.hopto.eriksen.api.Ingredient;
import org.hopto.eriksen.api.Part;
import org.hopto.eriksen.api.Recipe;
import org.hopto.eriksen.db.RecipePersister;
import org.testng.Assert;
import org.testng.annotations.Guice;
import org.testng.annotations.Test;

import com.google.inject.Inject;

@Guice(modules = GuiceInjectorModuleForTest.class)
public class RecipeJpaGuicePersisterTest {

	//	private final Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());

	@Inject
	private RecipePersister recipePersister;

	private Long recipieId;

	@Test
	public void testSaveRecipie() {
		final String title = "Ceasar sallad";
		final String comment = "Tar ca 40 min att laga...";

		Recipe recipe = new Recipe();
		recipe.setTitle(title);
		recipe.setComment(comment);
		recipePersister.save(recipe);

		recipieId = recipe.getId();
		Recipe persistedRecipe = recipePersister.findById(recipieId);

		Assert.assertNotNull(recipieId);
		Assert.assertEquals(title, persistedRecipe.getTitle());
		Assert.assertEquals(comment, persistedRecipe.getComment());

	}

	@Test(dependsOnMethods = "testSaveRecipie")
	public void testAddCourse() {
		final String name = "Dressing";
		final String instruction1 = "Put the oil into the ...";
		final String instruction2 = "foo bar";

		Recipe recipe = recipePersister.findById(recipieId);
		Part part = new Part();
		part.setName(name);
		part.addInstruction(instruction1);
		part.addInstruction(instruction2);
		recipe.addCPart(part);
		recipePersister.save(recipe);
		Recipe persistedRecipe = recipePersister.findById(recipieId);
		Assert.assertEquals(persistedRecipe.getParts().get(0).getName(), name);
		Assert.assertEquals(persistedRecipe.getParts().get(0).getInstructions().get(0), instruction1);
		Assert.assertEquals(persistedRecipe.getParts().get(0).getInstructions().get(1), instruction2);

		System.out.println("The persisted recipe looks like: " + persistedRecipe.toString());
	}

	@Test(dependsOnMethods = "testAddCourse")
	public void testAddIngredient() {

		final Ingredient oilIngredient = new Ingredient("oil");
		final Ingredient lemonIngredient = new Ingredient("lemon");

		Recipe recipe = recipePersister.findById(recipieId);
		Part part = recipe.getParts().get(0);
		part.addIngredient(oilIngredient);
		part.addIngredient(lemonIngredient);

		recipePersister.save(recipe);
		Recipe persistedRecipe = recipePersister.findById(recipieId);
		Set<Ingredient> ingredients = persistedRecipe.getParts().get(0).getIngredients();

		Assert.assertTrue(ingredients.contains(oilIngredient));
		Assert.assertTrue(ingredients.contains(lemonIngredient));
	}

}
