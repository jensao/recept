package org.hopto.eriksen;

import org.hopto.eriksen.core.Course;
import org.hopto.eriksen.core.Ingredient;
import org.hopto.eriksen.core.Recipe;
import org.hopto.eriksen.core.RecipeHasIngredient;
import org.hopto.eriksen.core.RecipeHasIngredientId;
import org.hopto.eriksen.core.RecipeInstruction;
import org.hopto.eriksen.db.CourseDAO;
import org.hopto.eriksen.db.RecipeDAO;
import org.hopto.eriksen.resources.CourseResource;

import io.dropwizard.Application;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class ReceptApplication extends Application<ReceptConfiguration> {
	
	public static void main(final String[] args) throws Exception {
		new ReceptApplication().run(args);
	}

	// TODO change to ScanningHibernateBundle
	private final HibernateBundle<ReceptConfiguration> hibernateBundle = new HibernateBundle<ReceptConfiguration>(
			Course.class, 
			Ingredient.class, 
			Recipe.class, 
			RecipeHasIngredient.class, 
			RecipeHasIngredientId.class, 
			RecipeInstruction.class) {
		
	    @Override
	    public DataSourceFactory getDataSourceFactory(ReceptConfiguration configuration) {
	        return configuration.getDataSourceFactory();
	    }
	};
	
	@Override
	public String getName() {
		return "recept";
	}

	@Override
	public void initialize(final Bootstrap<ReceptConfiguration> bootstrap) {
		bootstrap.addBundle(hibernateBundle);
		
		// Serializes java.util.Date into e.g. 2015-10-24T11:40:27.649+0000 instead of epoch timestamp.
		bootstrap.getObjectMapper().disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
	}

	@Override
	public void run(final ReceptConfiguration configuration, final Environment environment) {
		final CourseDAO courseDao = new CourseDAO(hibernateBundle.getSessionFactory());
		final RecipeDAO recipeDAO = new RecipeDAO(hibernateBundle.getSessionFactory());
		
	    environment.jersey().register(new CourseResource(courseDao, recipeDAO));
	}


	
}
