package org.hopto.eriksen.db.jpaguice;


import org.hopto.eriksen.db.IngredientPersister;
import org.hopto.eriksen.db.RecipePersister;

import com.google.inject.AbstractModule;
import com.google.inject.persist.jpa.JpaPersistModule;

public class GuiceInjectorModuleForTest extends AbstractModule {

	@Override
	protected void configure() {
		install(new JpaPersistModule("jpaHsqldbMat"));
		// The "asEagerSingleton" was critical to get this to work with TestNG, see: https://groups.google.com/forum/#!msg/testng-users/IEASESwcisg/zHVS_e2Dsu4J
		bind(PersistenceInitializerForTest.class).asEagerSingleton();

		bind(RecipePersister.class).to(RecipeJpaGuicePersister.class);
		bind(IngredientPersister.class).to(IngredientJpaGuicePersister.class);

	}
}
