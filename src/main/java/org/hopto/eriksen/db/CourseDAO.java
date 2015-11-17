package org.hopto.eriksen.db;

import java.util.List;

import io.dropwizard.hibernate.AbstractDAO;

import org.hibernate.SessionFactory;
import org.hopto.eriksen.core.Course;


public class CourseDAO extends AbstractDAO<Course> {

	public CourseDAO(SessionFactory sessionFactory) {
		super(sessionFactory);
	}

    public Course findById(int id) {
        return get(id);
    }

    /**
     * Either save or update the given instance, depending upon resolution of the unsaved-value checks (see the manual for discussion of unsaved-value checking).
     */
    public Course saveOrUpdate(Course course) {
        return persist(course);		// saveOrUpdate() will be used 
    }
    
    public List<Course> findAll() {
    	return criteria().list();
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
	
	/**
	 * Flushes the session, e.g. to get a DB id 
	 */
	public void flush() {
		currentSession().flush();
	}
	
	public void clear() {
		currentSession().clear();
	}
	
	public void merge(Course course) {
		currentSession().merge(course);
	}
	
}

