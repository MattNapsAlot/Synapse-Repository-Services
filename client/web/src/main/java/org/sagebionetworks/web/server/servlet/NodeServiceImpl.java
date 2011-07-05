package org.sagebionetworks.web.server.servlet;

import java.util.List;
import java.util.logging.Logger;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.sagebionetworks.web.client.DisplayConstants;
import org.sagebionetworks.web.client.services.NodeService;
import org.sagebionetworks.web.server.RestTemplateProvider;
import org.sagebionetworks.web.shared.NodeType;
import org.sagebionetworks.web.shared.users.AclAccessType;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.google.inject.Inject;

/**
 * The server-side implementation of the DatasetService. This serverlet will
 * communicate with the platform API via REST.
 * 
 * @author dburdick
 * 
 */
@SuppressWarnings("serial")
public class NodeServiceImpl extends RemoteServiceServlet implements
		NodeService {

	private static Logger logger = Logger.getLogger(NodeServiceImpl.class
			.getName());

	public static final String PATH_DATASET = "dataset";
	public static final String PATH_LAYER = "layer";
	public static final String PATH_PROJECT = "project";
	public static final String ANNOTATIONS_PATH = "annotations";
	public static final String PREVIEW_PATH = "preview";
	public static final String LOCATION_PATH = "location";
	
	public static final String PATH_SCHEMA = "schema";
	public static final String PATH_ACL = "acl"; 	
	
	private RestTemplateProvider templateProvider = null;
	private ServiceUrlProvider urlProvider;

	/**
	 * The rest template will be injected via Guice.
	 * 
	 * @param template
	 */
	@Inject
	public void setRestTemplate(RestTemplateProvider template) {
		this.templateProvider = template;
	}

	/**
	 * Injected vid Gin
	 * @param provider
	 */
	@Inject
	public void setServiceUrlProvider(ServiceUrlProvider provider){
		this.urlProvider = provider;
	}
	
	/**
	 * Validate that the service is ready to go. If any of the injected data is
	 * missing then it cannot run. Public for tests.
	 */
	public void validateService() {
		if (templateProvider == null)
			throw new IllegalStateException(
					"The org.sagebionetworks.web.server.RestTemplateProvider was not injected into this service");
		if (templateProvider.getTemplate() == null)
			throw new IllegalStateException(
					"The org.sagebionetworks.web.server.RestTemplateProvider returned a null template");
		if (urlProvider == null)
			throw new IllegalStateException(
					"The org.sagebionetworks.rest.api.root.url was not set");
	}

	@Override
	public String getNodeJSONSchema(NodeType type) {
		// First make sure the service is ready to go.
		validateService();
		
		// Build up the path
		StringBuilder builder = getBaseUrlBuilder(type);
		builder.append("/");
		builder.append(PATH_SCHEMA);		
		String url = builder.toString();		
		logger.info("GET: " + url);
		return getJsonStringForUrl(url, HttpMethod.GET);
	}

	@Override
	public String getNodeJSON(NodeType type, String id) {
		// First make sure the service is ready to go.
		validateService();
		
		// Build up the path
		StringBuilder builder = getBaseUrlBuilder(type);
		builder.append("/");
		builder.append(id);		
		String url = builder.toString();		
		return getJsonStringForUrl(url, HttpMethod.GET);
	}
	
	@Override
	public String createNode(NodeType type, String propertiesJson) {
		// First make sure the service is ready to go.
		validateService();
		
		// Build up the path
		StringBuilder builder = getBaseUrlBuilder(type);		 		
		String url = builder.toString();		
		return getJsonStringForUrl(url, HttpMethod.POST, propertiesJson);
	}

	@Override
	public String updateNode(NodeType type, String id, String propertiesJson, String eTag) {
		// First make sure the service is ready to go.
		validateService();
		
		// Build up the path
		StringBuilder builder = getBaseUrlBuilder(type);
		builder.append("/" + id);
		String url = builder.toString();		
		return getJsonStringForUrl(url, HttpMethod.PUT, propertiesJson, eTag);
	}
	
	@Override
	public void deleteNode(NodeType type, String id) {
		// First make sure the service is ready to go.
		validateService();
		
		// Build up the path
		StringBuilder builder = getBaseUrlBuilder(type);
		builder.append("/" + id);
		String url = builder.toString();		
		getJsonStringForUrl(url, HttpMethod.DELETE);
	}

	@Override
	public String getNodeAnnotationsJSON(NodeType type, String id) {
		// Build up the path
		StringBuilder builder = getBaseUrlBuilder(type);
		builder.append("/" + id);
		builder.append("/" + ANNOTATIONS_PATH);
		String url = builder.toString();	
		return getJsonStringForUrl(url, HttpMethod.GET);
	}
	
	@Override
	public String getNodePreview(NodeType type, String id) {
		// Build up the path
		StringBuilder builder = getBaseUrlBuilder(type);
		builder.append("/" + id);
		builder.append("/" + PREVIEW_PATH);
		String url = builder.toString();	
		return getJsonStringForUrl(url, HttpMethod.GET);
	}
	
	@Override
	public String getNodeLocations(NodeType type, String id) {
		// Build up the path
		StringBuilder builder = getBaseUrlBuilder(type);
		builder.append("/" + id);
		builder.append("/" + LOCATION_PATH);
		String url = builder.toString();	
		return getJsonStringForUrl(url, HttpMethod.GET);
	}


	@Override
	public String updateNodeAnnotations(NodeType type, String id, String annotationsJson, String etag) {
		// First make sure the service is ready to go.
		validateService();
		
		// Build up the path
		StringBuilder builder = getBaseUrlBuilder(type);
		builder.append("/" + id);
		builder.append("/" + ANNOTATIONS_PATH);
		String url = builder.toString();		
		return getJsonStringForUrl(url, HttpMethod.PUT, annotationsJson, etag);
	}


	@Override
	public String getNodeAclJSON(NodeType type, String id) {
		// Build up the path
		StringBuilder builder = getBaseUrlBuilder(type);
		builder.append("/" + id);
		builder.append("/" + PATH_ACL);
		String url = builder.toString();	
		return getJsonStringForUrl(url, HttpMethod.GET);
	}

	@Override
	public String createAcl(NodeType type, String id, String userGroupId, List<AclAccessType> accessTypes) {
		// First make sure the service is ready to go.
		validateService();
		
		// Build up the path
		StringBuilder builder = getBaseUrlBuilder(type);
		builder.append("/" + id);
		builder.append("/" + PATH_ACL);
		String url = builder.toString();		
		
		// convert 
		JSONObject obj = new JSONObject();
		try {
			obj.put("resourceId", id);			
			if(userGroupId != null && accessTypes != null) {
				JSONArray accesses = new JSONArray();
				JSONObject accessObj = new JSONObject();
				accessObj.put("userGroupId", userGroupId);
				for(AclAccessType accessType : accessTypes) {
					accessObj.put("accessType", accessType.toString());
				}
				accesses.put(accessObj);				
				obj.put("resourceAccess", accesses);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		String requestJson = obj.toString();

		return getJsonStringForUrl(url, HttpMethod.POST, requestJson);
	}

	@Override
	public String updateAcl(NodeType type, String id, String aclJson, String etag) {
		// First make sure the service is ready to go.
		validateService();
		
		// Build up the path
		StringBuilder builder = getBaseUrlBuilder(type);
		builder.append("/" + id);
		builder.append("/" + PATH_ACL);
		String url = builder.toString();		
		
		return getJsonStringForUrl(url, HttpMethod.PUT, aclJson, etag);	
	}

	@Override
	public String deleteAcl(NodeType type, String id) {
		// First make sure the service is ready to go.
		validateService();
		
		// Build up the path
		StringBuilder builder = getBaseUrlBuilder(type);
		builder.append("/" + id);
		builder.append("/" + PATH_ACL);
		String url = builder.toString();		

		return getJsonStringForUrl(url, HttpMethod.DELETE);		
	}
	
	
	/*
	 * Private Methods
	 */
	private String getJsonStringForUrl(String url, HttpMethod method) {
		return getJsonStringForUrl(url, method, null, null);
	}
	
	private String getJsonStringForUrl(String url, HttpMethod method, String entityString) {
		return getJsonStringForUrl(url, method, entityString, null);
	}
	
	private String getJsonStringForUrl(String url, HttpMethod method, String entityString, String etag) {
		// First make sure the service is ready to go.
		validateService();

		String logString = method.toString() + ": " + url;
		if(entityString != null) {
			logString += ", " + "JSON: " + entityString; 
		}		
		logger.info(logString);
		
		// Setup the header
		HttpHeaders headers = new HttpHeaders();
		// If the user data is stored in a cookie, then fetch it and the session token to the header.
		UserDataProvider.addUserDataToHeader(this.getThreadLocalRequest(), headers);
		headers.setContentType(MediaType.APPLICATION_JSON);
		if(etag != null) headers.set(DisplayConstants.SERVICE_HEADER_ETAG_KEY, etag);
		if(entityString == null) entityString = "";
		HttpEntity<String> entity = new HttpEntity<String>(entityString, headers);
		
		// Make the actual call.
		try {
			ResponseEntity<String> response = templateProvider.getTemplate().exchange(url, method, entity, String.class);
			if (response.getStatusCode() == HttpStatus.OK) {			
				return response.getBody();
			} else {
				throw new UnknownError("Status code:" + response.getStatusCode().value());
			}
		} catch (HttpClientErrorException ex) {

//			if(ex.getStatusCode() == HttpStatus.UNAUTHORIZED) {
//				throw new UnauthorizedException();
//			} else if(ex.getStatusCode() == HttpStatus.FORBIDDEN) {
//				throw new ForbiddenException();
//			} else {
//				throw new UnknownError("Status code:" + ex.getStatusCode().value());
//			}
			
			// temporary solution to not being able to throw caught exceptions (due to Gin 1.0)
			JSONObject obj = new JSONObject();
			JSONObject errorObj = new JSONObject();
			try {
				Integer code = ex.getStatusCode().value();
				if(code != null) errorObj.put("statusCode", code);
				obj.put("error", errorObj);
				return obj.toString();
			} catch (JSONException e) {
				throw new UnknownError();
			}
		}		
	}
	
	private StringBuilder getBaseUrlBuilder(NodeType type) {
		StringBuilder builder = new StringBuilder();
		builder.append(urlProvider.getBaseUrl());
		// set path based on type
		switch(type) {
		case DATASET:
			builder.append(PATH_DATASET);
			break;
		case PROJECT:
			builder.append(PATH_PROJECT);
			break;
		case LAYER:
			builder.append(PATH_LAYER);
			break;
		default:
			throw new IllegalArgumentException("Unsupported type:" + type.toString());
		}
		return builder;
	}
	
}