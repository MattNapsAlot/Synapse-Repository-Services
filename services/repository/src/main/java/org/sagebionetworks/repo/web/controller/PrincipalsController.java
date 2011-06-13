package org.sagebionetworks.repo.web.controller;

import java.util.Collection;

import org.codehaus.jackson.schema.JsonSchema;
import org.sagebionetworks.repo.manager.PermissionsManager;
import org.sagebionetworks.repo.model.DatastoreException;
import org.sagebionetworks.repo.model.UserGroup;
import org.sagebionetworks.repo.util.SchemaHelper;
import org.sagebionetworks.repo.web.UrlHelpers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller

public class PrincipalsController extends BaseController {

	@Autowired
	PermissionsManager permissionsManager;
	
	/**
	 * Get the Individuals in the system
	 * @param userId - The user that is making the request.
	 * @param request
	 * @return The UserGroups for individuals
	 * @throws DatastoreException - Thrown when there is a server-side problem.
	 */
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(value = UrlHelpers.USER, method = RequestMethod.GET)
	public @ResponseBody
	Collection<UserGroup> getIndividuals() throws DatastoreException {
		return permissionsManager.getIndividuals();
	}

	/**
	 * Get the user-groups in the system
	 * @param userId - The user that is making the request.
	 * @param request
	 * @return The UserGroups for individuals
	 * @throws DatastoreException - Thrown when there is a server-side problem.
	 */
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(value = UrlHelpers.USERGROUP, method = RequestMethod.GET)
	public @ResponseBody
	Collection<UserGroup> getUserGroups() throws DatastoreException {
		return permissionsManager.getGroups();
	}

	/**
	 * Get the schema for an ACL
	 * @param id
	 * @param request
	 * @return
	 * @throws DatastoreException
	 */
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(value ={UrlHelpers.USERGROUP + UrlHelpers.SCHEMA}, method = RequestMethod.GET)
	public @ResponseBody
	JsonSchema getGroupSchema() throws DatastoreException {
		return SchemaHelper.getSchema(UserGroup.class);
	}
	

	
}
