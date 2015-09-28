package org.hopto.eriksen;

import io.dropwizard.lifecycle.Managed;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.persist.PersistService;

@Singleton
public class PersistenceGuiceInitializer implements Managed {

	private PersistService persistService;

	@Inject
	PersistenceGuiceInitializer(PersistService service) {
		persistService = service;
	}

	/**
	 * To start the persistence service.
	 */
	@Override
	public void start() {
		persistService.start();
		// At this point JPA is started and ready.
	}

	/**
	 * To stop the persistence service 
	 */
	@Override
	public void stop() {
		persistService.stop();
	}

}
