package org.hopto.eriksen.db.jpaguice;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.persist.PersistService;

/**
 * TO REMEMBER
 * 
 * This class initializes and starts the persistence. The start method was
 * forced to be in the constructor to get the TestNG works as intended (In
 * combination with "asEagerSingleton" in the module class). I think it is due
 * to that Guice usually shall initiate/create the classes that needs some kind
 * of dependency injection, but in this case is the test case classes initiated
 * by TestNG. See:
 * https://groups.google.com/forum/#!msg/testng-users/IEASESwcisg/zHVS_e2Dsu4J
 * 
 * I have also tried to move this initialization with "@BeforeGroups" method and
 * "dependsOnGroups" without success
 * 
 * @author jens
 *
 */
@Singleton
public class PersistenceInitializerForTest {

	private PersistService persistService;

	@Inject
	PersistenceInitializerForTest(PersistService service) {
		persistService = service;
		persistService.start();
	}

	/**
	 * To start the persistens service (this is done in the constructor
	 * nowadays)
	 */
	@Deprecated
	public void start() {
		persistService.start();
		// At this point JPA is started and ready.
	}

	public void stop() {
		persistService.stop();
	}

}
