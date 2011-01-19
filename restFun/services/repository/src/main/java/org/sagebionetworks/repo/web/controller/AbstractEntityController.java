package org.sagebionetworks.repo.web.controller;

import javax.servlet.http.HttpServletRequest;

import org.sagebionetworks.repo.view.PaginatedResults;
import org.sagebionetworks.repo.web.ConflictingUpdateException;
import org.sagebionetworks.repo.web.NotFoundException;
import org.sagebionetworks.repo.web.ServiceConstants;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author deflaux
 *
 * @param <T>
 */
public interface AbstractEntityController<T> {

    /**
     * Get entities<p>
     * <ul>
     * <li>TODO filter by date
     * <li>TODO more response bread crumb urls when we have proper DTOs
     * </ul>
     * 
     * @param offset 1-based pagination offset
     * @param limit maximum number of results to return
     * @param request used to form return URLs in the body of the response
     * @return list of all entities stored in the repository 
     */
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "", method = RequestMethod.GET)
    public @ResponseBody
    abstract PaginatedResults<T> getEntities(
            @RequestParam(value = ServiceConstants.PAGINATION_OFFSET_PARAM, required = false, defaultValue = ServiceConstants.DEFAULT_PAGINATION_OFFSET_PARAM) Integer offset,
            @RequestParam(value = ServiceConstants.PAGINATION_LIMIT_PARAM, required = false, defaultValue = ServiceConstants.DEFAULT_PAGINATION_LIMIT_PARAM) Integer limit,
            HttpServletRequest request);

    /**
     * Get a specific entity<p>
     * <ul>
     * <li>TODO response bread crumb urls when we have proper DTOs
     * </ul>
     *   
     * @param id the unique identifier for the entity to be returned 
     * @return the entity or exception if not found
     * @throws NotFoundException 
     */
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public @ResponseBody
    abstract T getEntity(@PathVariable Long id) throws NotFoundException;

    /**
     * Create a new entity<p>
     * <ul>
     * <li>TODO validate minimum requirements for new entity object
     * <li>TODO response bread crumb urls when we have proper DTOs
     * </ul>
     *
     * @param newEntity 
     * @return the newly created entity 
     */
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "", method = RequestMethod.POST)
    public @ResponseBody
    abstract T createEntity(@RequestBody T newEntity);

    /**
     * Update an existing entity<p>
     * <ul>
     * <li>TODO validate updated entity
     * <li>TODO response bread crumb urls when we have proper DTOs
     * </ul>
     * 
     * @param id the unique identifier for the entity to be updated
     * @param etag service-generated value used to detect conflicting updates
     * @param updatedEntity the object with which to overwrite the currently stored entity
     * @return the updated entity
     * @throws NotFoundException 
     * @throws ConflictingUpdateException 
     */
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public @ResponseBody
    abstract T updateEntity(@PathVariable Long id,
            @RequestHeader(ServiceConstants.ETAG_HEADER) Integer etag,
            @RequestBody T updatedEntity) throws NotFoundException,
            ConflictingUpdateException;

    /**
     * Delete a specific entity<p>
     * 
     * @param id the unique identifier for the entity to be deleted 
     * @throws NotFoundException 
     */
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public abstract void deleteEntity(@PathVariable Long id)
            throws NotFoundException;

}