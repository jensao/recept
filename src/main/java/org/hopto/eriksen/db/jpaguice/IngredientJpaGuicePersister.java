package org.hopto.eriksen.db.jpaguice;

import javax.persistence.EntityManager;

import org.hopto.eriksen.api.Ingredient;
import org.hopto.eriksen.db.IngredientPersister;

import com.google.inject.Inject;
import com.google.inject.persist.Transactional;

public class IngredientJpaGuicePersister implements IngredientPersister {

	@Inject
	//	@PersistenceContext
	private EntityManager entityManager;

	@Override
	@Transactional
	public void save(Ingredient ingredient) {
		entityManager.persist(ingredient);
	}

	@Override
	@Transactional
	public Ingredient findById(Long id) {
		return (Ingredient) entityManager.find(Ingredient.class, id);
	}

}
