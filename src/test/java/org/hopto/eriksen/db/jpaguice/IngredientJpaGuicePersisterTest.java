package org.hopto.eriksen.db.jpaguice;


import org.hopto.eriksen.api.Ingredient;
import org.hopto.eriksen.db.IngredientPersister;
import org.testng.Assert;
import org.testng.annotations.Guice;
import org.testng.annotations.Test;

import com.google.inject.Inject;

@Guice(modules = GuiceInjectorModuleForTest.class)
public class IngredientJpaGuicePersisterTest {

	@Inject
	private IngredientPersister ingredientPersister;

	private Long ingredientId;
	private final String ingredientName = "Organo";

	@Test
	public void testSaveIngredient() {
		Ingredient ingredient = new Ingredient(ingredientName);
		ingredientPersister.save(ingredient);
		ingredientId = ingredient.getId();

	}

	@Test(dependsOnMethods = "testSaveIngredient")
	public void testReadIngredient() {
		Ingredient ingredient = ingredientPersister.findById(ingredientId);
		Assert.assertEquals(ingredient.getName(), ingredientName);
		Assert.assertNotNull(ingredient);
	}
}
