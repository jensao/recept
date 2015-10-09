package org.hopto.eriksen.db;

import java.util.List;

import io.dropwizard.hibernate.AbstractDAO;

import org.hibernate.SessionFactory;
import org.hopto.eriksen.core.Course;


public class CourseDAO extends AbstractDAO<Course> {

	public CourseDAO(SessionFactory sessionFactory) {
		super(sessionFactory);
		// TODO Auto-generated constructor stub
	}

    public Course findById(int id) {
        return get(id);
    }

    /**
     * Either save or update the given instance, depending upon resolution of the unsaved-value checks (see the manual for discussion of unsaved-value checking).
     */
    // Om man byter namn på denna till "persist" så får man en stack-overflow, why?
    public Course create(Course course) {
        return persist(course);
    }
    
    public List<Course> findAll() {
    	return criteria().list();
//        return list(namedQuery("com.example.helloworld.core.Person.findAll"));
    }
    
	public List<Course> getAllPaged(int pageNumber, int pageSize) {
		
//		// TODO dessa rader ska nog flyttas till en egen funktion?!
//		Query queryTotal = entityManager.createQuery("Select count(r.id) from Recipe r");
//		long countResult = (long)queryTotal.getSingleResult();
//		
////		int maxPageNumber = (int) ((countResult / pageSize) + 1);
//		
//		LOGGER.debug("It exist " + countResult + " rows in the recipe table");
//		
//		Query query = entityManager.createQuery("From Recipe");
//
//		query.setFirstResult((pageNumber - 1) * pageSize);
//		query.setMaxResults(pageSize);
//		return query.getResultList();
		
		return null;
	}
	
	public void delete(Course course) {
		 currentSession().delete(course);
	}
	
}

