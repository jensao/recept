package org.hopto.eriksen.db.jpaguice;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.hopto.eriksen.api.Recipe;
import org.hopto.eriksen.db.RecipePersister;
import org.hopto.eriksen.resources.RecipeResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.persist.Transactional;

/**
 * This class first used a DAO but it was removed since the DAO only changed the
 * name of the methods used for the actions.
 *
 */

public class RecipeJpaGuicePersister implements RecipePersister {

	private static final Logger LOGGER = LoggerFactory.getLogger(RecipeJpaGuicePersister.class.getSimpleName());
	
	@Inject
	private EntityManager entityManager;

	@Override
	@Transactional
	public void save(Recipe recipe) {
		entityManager.persist(recipe);
	}

	@Override
	@Transactional
	public Recipe findById(Long id) {
		return (Recipe) entityManager.find(Recipe.class, id);
	}

	@Override
	@Transactional
	// Pagination solution taken from: http://www.baeldung.com/jpa-pagination 
	public List<Recipe> getAll(int pageNumber, int pageSize) {
		
		// TODO dessa rader ska nog flyttas till en egen funktion?!
		Query queryTotal = entityManager.createQuery("Select count(r.id) from Recipe r");
		long countResult = (long)queryTotal.getSingleResult();
		
//		int maxPageNumber = (int) ((countResult / pageSize) + 1);
		
		LOGGER.debug("It exist " + countResult + " rows in the recipe table");
		
		Query query = entityManager.createQuery("From Recipe");

		query.setFirstResult((pageNumber - 1) * pageSize);
		query.setMaxResults(pageSize);
		return query.getResultList();
	}

	@Override
	@Transactional
	public void delete(Recipe recipe) {
		entityManager.remove(recipe);
	}

}
