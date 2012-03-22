package org.sagebionetworks.repo.web.controller.metadata;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.sagebionetworks.repo.model.EntityHeader;
import org.sagebionetworks.repo.model.Data;
import org.sagebionetworks.repo.model.InvalidModelException;
import org.sagebionetworks.repo.model.EntityType;
import org.sagebionetworks.repo.model.LayerTypeNames;

public class LayerMetadataProviderTest {
	
	
// TODO: Needed if LayerMetadataProvider sets default version
	@Test
	public void testValidate() throws InvalidModelException{
		LayerMetadataProvider provider = new LayerMetadataProvider();
		Data mock = new Data();
		mock.setParentId("12");
		mock.setType(LayerTypeNames.C);

		EntityHeader parent = new EntityHeader();
		parent.setId("344");
		parent.setType(EntityType.dataset.getUrlPrefix());
		parent.setName("Joe");
		List<EntityHeader> list  = new ArrayList<EntityHeader>();
		list.add(parent);
		
		provider.validateEntity(mock, new EntityEvent(EventType.CREATE, list, null));

	}
}
