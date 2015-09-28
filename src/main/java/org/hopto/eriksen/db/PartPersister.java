package org.hopto.eriksen.db;

import org.hopto.eriksen.api.Part;

public interface PartPersister {

	void save(Part part);
	
	void delete(Part part);
	
}
