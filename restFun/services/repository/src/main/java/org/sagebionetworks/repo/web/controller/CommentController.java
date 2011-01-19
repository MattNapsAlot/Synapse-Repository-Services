package org.sagebionetworks.repo.web.controller;

import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.sagebionetworks.repo.model.Comment;
import org.sagebionetworks.repo.view.PaginatedResults;
import org.sagebionetworks.repo.web.ConflictingUpdateException;
import org.sagebionetworks.repo.web.EntityControllerImp;
import org.sagebionetworks.repo.web.NotFoundException;
import org.sagebionetworks.repo.web.ServiceConstants;
import org.sagebionetworks.repo.web.UrlPrefixes;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;


/**
 * REST controller for CRUD operations on Entity objects
 * 
 * @author deflaux
 */
@Controller
@RequestMapping(UrlPrefixes.COMMENT)
public class CommentController extends BaseController implements AbstractEntityController<Comment> {

    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(CommentController.class.getName());
        
    // TODO maybe? atAutowired
    private EntityControllerImp<Comment> entityController = new EntityControllerImp<Comment>(Comment.class);
    
    /* (non-Javadoc)
     * @see org.sagebionetworks.repo.web.controller.AbstractEntityController#getEntities(java.lang.Integer, java.lang.Integer, javax.servlet.http.HttpServletRequest)
     */
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "", method = RequestMethod.GET)
    public @ResponseBody PaginatedResults<Comment> getEntities(
            @RequestParam(value=ServiceConstants.PAGINATION_OFFSET_PARAM, 
                    required=false, 
                    defaultValue=ServiceConstants.DEFAULT_PAGINATION_OFFSET_PARAM) Integer offset,
            @RequestParam(value=ServiceConstants.PAGINATION_LIMIT_PARAM, 
                    required=false, 
                    defaultValue=ServiceConstants.DEFAULT_PAGINATION_LIMIT_PARAM) Integer limit,
                    HttpServletRequest request) {
        return entityController.getEntities(offset, limit, request);
    }

    /* (non-Javadoc)
     * @see org.sagebionetworks.repo.web.controller.AbstractEntityController#getEntity(java.lang.Long)
     */
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
        public @ResponseBody Comment getEntity(@PathVariable Long id) throws NotFoundException {
        return entityController.getEntity(id);
    }

    /* (non-Javadoc)
     * @see org.sagebionetworks.repo.web.controller.AbstractEntityController#createEntity(T)
     */
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "", method = RequestMethod.POST)
    public @ResponseBody Comment createEntity(@RequestBody Comment newEntity) {
        return entityController.createEntity(newEntity);
    }

    /* (non-Javadoc)
     * @see org.sagebionetworks.repo.web.controller.AbstractEntityController#updateEntity(java.lang.Long, java.lang.Integer, T)
     */
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public @ResponseBody Comment updateEntity(@PathVariable Long id, 
            @RequestHeader(ServiceConstants.ETAG_HEADER) Integer etag, 
            @RequestBody Comment updatedEntity) throws NotFoundException, ConflictingUpdateException {
        return entityController.updateEntity(id, etag, updatedEntity);
    }
    
    /* (non-Javadoc)
     * @see org.sagebionetworks.repo.web.controller.AbstractEntityController#deleteEntity(java.lang.Long)
     */
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public void deleteEntity(@PathVariable Long id) throws NotFoundException {
        entityController.deleteEntity(id);
        return;
    }

    /**
     * Simple sanity check test request, using the default view<p> 
     * 
     * @param modelMap the parameter into which output data is to be stored
     * @return a dummy hard-coded response
     */
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "test", method = RequestMethod.GET)
        public String sanityCheck(ModelMap modelMap) {
        modelMap.put("hello","REST for Comments rocks");
        return ""; // use the default view
    }
        
}
