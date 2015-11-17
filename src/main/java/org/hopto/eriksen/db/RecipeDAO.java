package org.hopto.eriksen.db;

import org.hibernate.SessionFactory;
import org.hopto.eriksen.core.Recipe;

import io.dropwizard.hibernate.AbstractDAO;

public class RecipeDAO  extends AbstractDAO<Recipe> {

	public RecipeDAO(SessionFactory sessionFactory) {
		super(sessionFactory);
	}

    public Recipe findById(int id) {
        return get(id);
    }

	public void merge(Recipe recipe) {
		currentSession().merge(recipe);
	}
    
	public void delete(Recipe recipe) {
		currentSession().delete(recipe);
	}
	
	public boolean delete(int recipeId) {
	    Recipe recipe = (Recipe) currentSession().load(Recipe.class, recipeId);
	    if (recipe != null) {
	    	currentSession().delete(recipe);
	        return true;
	    }
	    return false;
	}
	
}
