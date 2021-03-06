package org.sagebionetworks.repo.model;

import java.util.Collection;

import org.sagebionetworks.repo.web.NotFoundException;

public interface AccessControlListDAO  {
	
	/**
	 * Find the access control list for the given resource
	 * @throws DatastoreException 
	 * @throws NotFoundException 
	 */
	public AccessControlList getForResource(String rid) throws DatastoreException, NotFoundException;

	/**
	 * @return true iff some group in 'groups' has explicit permission to access 'resourceId' using access type 'accessType'
	 * @throws DatastoreException 
	 */
	public boolean canAccess(Collection<UserGroup> groups, String resourceId, ACCESS_TYPE accessType) throws DatastoreException;

	/**
	 * @return the SQL to find the root-accessible nodes that a specified user-group list can access
	 * using a specified access type
	 */
	public String authorizationSQL(int n);

	/**
	 * Create a new ACL
	 * @param dto
	 * @return
	 * @throws DatastoreException
	 * @throws InvalidModelException
	 * @throws NotFoundException 
	 */
	public String create(AccessControlList dto) throws DatastoreException,	InvalidModelException, NotFoundException;

	/**
	 * Get an ACL using the Node's ID
	 * @param id
	 * @return
	 * @throws DatastoreException
	 * @throws NotFoundException
	 */
	public AccessControlList get(String id) throws DatastoreException,	NotFoundException;

	/**
	 * Update the JDO
	 * @param dto
	 * @throws DatastoreException
	 * @throws InvalidModelException
	 * @throws NotFoundException
	 * @throws ConflictingUpdateException
	 */
	public void update(AccessControlList dto) throws DatastoreException, InvalidModelException, NotFoundException,ConflictingUpdateException;

	/**
	 * Delete a ACL using the Node's Id.
	 * @param id
	 * @throws DatastoreException
	 * @throws NotFoundException
	 */
	public void delete(String id) throws DatastoreException, NotFoundException;

}
