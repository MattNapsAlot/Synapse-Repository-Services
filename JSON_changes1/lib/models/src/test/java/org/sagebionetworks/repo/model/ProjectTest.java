package org.sagebionetworks.repo.model;

import java.util.Date;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

import org.sagebionetworks.schema.adapter.JSONObjectAdapter;
import org.sagebionetworks.schema.adapter.org.json.JSONObjectAdapterImpl;
import org.sagebionetworks.schema.adapter.JSONObjectAdapterException;

public class ProjectTest {
	
	@Test
	public void testRoundTripProject() throws JSONObjectAdapterException {
		Project p1 = new Project();
		JSONObjectAdapter adapter1 = new JSONObjectAdapterImpl();
		JSONObjectAdapter adapter2 = new JSONObjectAdapterImpl();
		Date d = new Date();
		
		p1.setAccessControlList("/acl");
		p1.setAnnotations("/annotations");
		p1.setCreatedBy("createdBy");
		p1.setCreatedOn(d);
		p1.setDescription("description");
		p1.setEtag("etag");
		p1.setId("1");
		p1.setModifiedBy("modifiedBy");
		p1.setModifiedOn(d);
		p1.setName("name");
		p1.setParentId("0");
		p1.setUri("uri");
		p1.setVersion("version");
		
		p1.setStatus("status");

		adapter1 = p1.writeToJSONObject(adapter1);
		String s = adapter1.toJSONString();
		adapter2 = JSONObjectAdapterImpl.createAdapterFromJSONString(s);
		Project p2 = new Project(adapter2);
		
		assertEquals(p1, p2);
	}
//	
//	@Test
//	public void testNullNameProject() throws JSONObjectAdapterException {
//		Project p1 = new Project();
//		JSONObjectAdapter adapter = new JSONObjectAdapterImpl();
//		Date d = new Date();
//		
//		p1.setAccessControlList("/acl");
//		p1.setAnnotations("/annotations");
//		p1.setCreatedBy("createdBy");
//		p1.setCreatedOn(d);
//		p1.setDescription("description");
//		p1.setEtag("etag");
//		p1.setId("1");
//		p1.setModifiedBy("modifiedBy");
//		p1.setModifiedOn(d);
//		p1.setParentId("0");
//		p1.setUri("uri");
//		p1.setVersion("version");
//		adapter = p1.writeToJSONObject(adapter);
//		
//		return;
//	}
}
