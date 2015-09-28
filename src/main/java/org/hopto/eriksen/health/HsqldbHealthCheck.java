package org.hopto.eriksen.health;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.codahale.metrics.health.HealthCheck;
import com.google.inject.Inject;
import com.google.inject.persist.Transactional;

public class HsqldbHealthCheck extends HealthCheck {

	@Inject
	private EntityManager entityManager;
	
	// See: http://stackoverflow.com/questions/3668506/efficient-sql-test-query-or-validation-query-that-will-work-across-all-or-most
	private static final String healthCheckString = "SELECT 1 FROM INFORMATION_SCHEMA.SYSTEM_USERS";
	
	@Override
	@Transactional
	// Inspired by: https://github.com/dropwizard/dropwizard/blob/master/dropwizard-hibernate/src/main/java/io/dropwizard/hibernate/SessionFactoryHealthCheck.java
	protected Result check() throws Exception {
		Query query = entityManager.createNativeQuery(healthCheckString);  

		query.getSingleResult();
		
		// TODO fix me
		return Result.healthy();
	}

}
