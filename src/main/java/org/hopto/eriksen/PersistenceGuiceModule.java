package org.hopto.eriksen;




import org.hopto.eriksen.db.IngredientPersister;
import org.hopto.eriksen.db.RecipePersister;
import org.hopto.eriksen.db.jpaguice.IngredientJpaGuicePersister;
import org.hopto.eriksen.db.jpaguice.RecipeJpaGuicePersister;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.persist.jpa.JpaPersistModule;

/**
 * This module handles / wiring the persistence package
 *
 */
public class PersistenceGuiceModule extends AbstractModule {

	@Override
	protected void configure() {
		install(new JpaPersistModule("jpaHsqldbMat"));
//		bind(PersistenceGuiceInitializer.class).in(Singleton.class);
		bind(RecipePersister.class).to(RecipeJpaGuicePersister.class);
		bind(IngredientPersister.class).to(IngredientJpaGuicePersister.class);
	}
}
