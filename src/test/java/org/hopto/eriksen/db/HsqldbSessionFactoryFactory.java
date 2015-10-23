package org.hopto.eriksen.db;

import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HsqldbSessionFactoryFactory {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(HsqldbSessionFactoryFactory.class.getSimpleName());
	private static volatile SessionFactory sessionFactory;

	private HsqldbSessionFactoryFactory() {	}
	
	public static synchronized SessionFactory getSessionFactory() {
		if(sessionFactory == null) {
			LOGGER.info("Cerating a SessionFactory to a Hsqldb DB: ");
			sessionFactory = createHsqldbSessionFactory();         
		}
		return sessionFactory;
	}
	
	public static void closeSessionFactory() {
		sessionFactory.close();
		sessionFactory = null;
	}
	
	private static SessionFactory createHsqldbSessionFactory()  {
		LOGGER.info("Trying to create a HSQLDB sessionFactory");
		Configuration config = new Configuration();

		config.setProperty("hibernate.connection.url","jdbc:hsqldb:mem:recept;shutdown=true;hsqldb.tx=mvcc");
		config.setProperty("hibernate.connection.username","je");
		config.setProperty("hibernate.connection.driver_class","org.hsqldb.jdbcDriver");
		config.setProperty("hibernate.current_session_context_class", "thread");
		config.setProperty("hibernate.show_sql", "false");
		config.setProperty("hibernate.format_sql", "true");
		config.setProperty("hibernate.dialect", "org.hibernate.dialect.HSQLDialect");
		config.setProperty("hibernate.hbm2ddl.auto", "create-drop");
		
		// Incredibly bad way by HN, see: http://stackoverflow.com/questions/28097847/hibernate-4-3-x-load-all-entity-annotated-classes
		// Pkg scanning shall exist in HN5?
		config.addAnnotatedClass(org.hopto.eriksen.core.Course.class);
		config.addAnnotatedClass(org.hopto.eriksen.core.Ingredient.class);
		config.addAnnotatedClass(org.hopto.eriksen.core.Recipe.class);
		config.addAnnotatedClass(org.hopto.eriksen.core.RecipeHasIngredient.class);
		config.addAnnotatedClass(org.hopto.eriksen.core.RecipeHasIngredientId.class);
		config.addAnnotatedClass(org.hopto.eriksen.core.RecipeInstruction.class);
		
//		config.setProperty("connection.provider_class", "org.hibernate.connection.C3P0ConnectionProvider");
//		config.setProperty("c3p0.min_size", "5");
//		config.setProperty("c3p0.max_size", "20");
//		config.setProperty("c3p0.timeout", "1800");
//		config.setProperty("c3p0.max_statements", "100");
//		config.setProperty("hibernate.c3p0.testConnectionOnCheckout", "true");

		ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder().applySettings(config.getProperties()).build();
		return config.buildSessionFactory(serviceRegistry);
	}

	
}
