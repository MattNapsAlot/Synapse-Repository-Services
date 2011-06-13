package org.sagebionetworks.repo.web.controller;

import org.sagebionetworks.repo.model.BaseChild;
import org.sagebionetworks.repo.model.InputDataLayer;
import org.sagebionetworks.repo.model.InvalidModelException;
import org.sagebionetworks.repo.model.LayerLocation;
import org.sagebionetworks.repo.model.Nodeable;
import org.sagebionetworks.repo.model.ObjectType;

/**
 * A utility for creating various object types with all of the required fields set.
 * @author jmhill
 *
 */
public class ObjectTypeFactory {
	
	
	/**
	 * A factory method used by tests to create an object with all of the required fields filled in.
	 * @param name
	 * @param type
	 * @return
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws InvalidModelException 
	 */
	public static Nodeable createObjectForTest(String name, ObjectType type, String parentId) throws InstantiationException, IllegalAccessException, InvalidModelException{
		Nodeable object = type.getClassForType().newInstance();
		object.setName(name);
		// Handle layers
		if(object instanceof InputDataLayer){
			InputDataLayer layer = (InputDataLayer) object;
			layer.setType(InputDataLayer.LayerTypeNames.C.name());
		}else if(object instanceof LayerLocation){
			LayerLocation location = (LayerLocation) object;
			location.setType(LayerLocation.LocationTypeNames.sage.name());
			location.setPath("/somePath");
			location.setMd5sum("md5sum");
		}
		// Any object that needs  parent
		if(object instanceof BaseChild){
			BaseChild child = (BaseChild) object;
			child.setParentId(parentId);
		}
		return object;
	}
	
	

}
