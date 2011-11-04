package org.sagebionetworks.repo.web.controller.metadata;

import org.sagebionetworks.repo.model.DatastoreException;
import org.sagebionetworks.repo.model.InvalidModelException;
import org.sagebionetworks.repo.model.Nodeable;
import org.sagebionetworks.repo.model.UnauthorizedException;
import org.sagebionetworks.repo.web.NotFoundException;

public interface EntityValidator<T extends Nodeable> {
	
	/**
	 * Validate an entity before it is created or updated.
	 * @param entity
	 * @param event
	 * @throws InvalidModelException
	 * @throws UnauthorizedException 
	 * @throws DatastoreException 
	 * @throws NotFoundException 
	 */
	public void validateEntity(T entity, EntityEvent event) throws InvalidModelException, NotFoundException, DatastoreException, UnauthorizedException;

}