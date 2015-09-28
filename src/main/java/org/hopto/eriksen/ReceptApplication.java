package org.hopto.eriksen;

import org.hopto.eriksen.resources.RecipeResource;

import com.google.inject.persist.jpa.JpaPersistModule;
import com.hubspot.dropwizard.guice.GuiceBundle;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class ReceptApplication extends Application<ReceptConfiguration> {

	private GuiceBundle<ReceptConfiguration> guiceBundle;
	
	public static void main(final String[] args) throws Exception {
		new ReceptApplication().run(args);
	}

	@Override
	public String getName() {
		return "recept";
	}

	@Override
	public void initialize(final Bootstrap<ReceptConfiguration> bootstrap) {

		guiceBundle = GuiceBundle.<ReceptConfiguration>newBuilder()
				
				.addModule(new PersistenceGuiceModule())
				.enableAutoConfig("org.hopto.eriksen")
				.setConfigClass(ReceptConfiguration.class)
				.build();

		bootstrap.addBundle(guiceBundle);
	}

	@Override
	public void run(final ReceptConfiguration configuration, final Environment environment) {
		
//		environment.jersey().register(resource);
//		environment.lifecycle().manage(guiceBundle.getInjector().getInstance(PersistenceGuiceInitializer.class));

//		environment.jersey().register(guiceBundle.getInjector().getInstance(RecipeResource.class));

	}

}
