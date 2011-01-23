package org.sagebionetworks.repo.web;

import java.util.List;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.sagebionetworks.repo.server.EntityRepository;
import org.sagebionetworks.repo.view.PaginatedResults;
import org.sagebionetworks.repo.web.controller.AbstractEntityController;

/**
 * Implementation for REST controller for CRUD operations on Entity objects
 * 
 * @author deflaux
 * @param <T> 
 */
public class EntityControllerImp<T> implements AbstractEntityController<T> {

    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(EntityControllerImp.class.getName());
        
    private Class<T> theModelClass;
    private EntityRepository<T> entityRepository;

    /**
     * @param theModelClass
     */
    public EntityControllerImp(Class<T> theModelClass) {
      this.theModelClass = theModelClass;
      this.entityRepository = new EntityRepository<T>(theModelClass);
    }
    
    /* (non-Javadoc)
     * @see org.sagebionetworks.repo.web.controller.AbstractEntityController#getEntities(java.lang.Integer, java.lang.Integer, javax.servlet.http.HttpServletRequest)
     */
    public PaginatedResults<T> getEntities(Integer offset,
            Integer limit,
            HttpServletRequest request) {
        
        ServiceConstants.validatePaginationParams(offset, limit);
        List<T> entities = entityRepository.getRange(offset, limit);
        Integer totalNumberOfEntities = entityRepository.getCount();
        return new PaginatedResults<T>(request.getServletPath() + UrlPrefixes.MODEL2URL.get(theModelClass),
                entities, totalNumberOfEntities, offset, limit);
    }

    /* (non-Javadoc)
     * @see org.sagebionetworks.repo.web.controller.AbstractEntityController#getEntity(java.lang.Long)
     */
    public T getEntity(Long id) throws NotFoundException {
        T entity = entityRepository.getById(id);
        if(null == entity) {
            throw new NotFoundException("no entity with id " + id + " exists");
        }
        return entity;
    }

    /* (non-Javadoc)
     * @see org.sagebionetworks.repo.web.controller.AbstractEntityController#createEntity(T)
     */
    public T createEntity(T newEntity) {
        // TODO check newEntity.isValid()
        // newEntity.getValidationErrorEntity()
        entityRepository.create(newEntity);
        return newEntity;
    }

    /* (non-Javadoc)
     * @see org.sagebionetworks.repo.web.controller.AbstractEntityController#updateEntity(java.lang.Long, java.lang.Integer, T)
     */
    public T updateEntity(Long id, 
            Integer etag, 
            T updatedEntity) throws NotFoundException, ConflictingUpdateException {
        T entity = entityRepository.getById(id);
        if(null == entity) {
            throw new NotFoundException("no entity with id " + id + " exists");
        }
        if(etag != entity.hashCode()) {
            throw new ConflictingUpdateException("entity with id " + id 
                    + "was updated since you last fetched it, retrieve it again and reapply the update");
        }
        entityRepository.create(updatedEntity);
        return updatedEntity;
    }
    
    /* (non-Javadoc)
     * @see org.sagebionetworks.repo.web.controller.AbstractEntityController#deleteEntity(java.lang.Long)
     */
    public void deleteEntity(Long id) throws NotFoundException {
        if(!entityRepository.deleteById(id)) {
            throw new NotFoundException("no entity with id " + id + " exists");   
        }
        return;
    }
}
