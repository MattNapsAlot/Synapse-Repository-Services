package org.sagebionetworks.repo.manager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.sagebionetworks.repo.model.Annotations;
import org.sagebionetworks.repo.model.ConflictingUpdateException;
import org.sagebionetworks.repo.model.Dataset;
import org.sagebionetworks.repo.model.DatastoreException;
import org.sagebionetworks.repo.model.EntityType;
import org.sagebionetworks.repo.model.InvalidModelException;
import org.sagebionetworks.repo.model.Layer;
import org.sagebionetworks.repo.model.LayerTypeNames;
import org.sagebionetworks.repo.model.Project;
import org.sagebionetworks.repo.model.QueryResults;
import org.sagebionetworks.repo.model.UnauthorizedException;
import org.sagebionetworks.repo.model.UserInfo;
import org.sagebionetworks.repo.model.query.BasicQuery;
import org.sagebionetworks.repo.model.query.Compartor;
import org.sagebionetworks.repo.model.query.CompoundId;
import org.sagebionetworks.repo.model.query.Expression;
import org.sagebionetworks.repo.web.GenericEntityController;
import org.sagebionetworks.repo.web.NotFoundException;
import org.sagebionetworks.repo.web.util.UserProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-context.xml" })
public class QueryManagerAutowireTest {
	
	@Autowired
	GenericEntityController entityController;
	@Autowired
	public UserProvider testUserProvider;
	
	List<String> toDelete = null;
	
	private long totalEntities = 10;
	
	private UserInfo userInfo;
	private String userId;
	HttpServletRequest mockRequest;
	
	@Before
	public void before() throws DatastoreException, InvalidModelException, NotFoundException, UnauthorizedException, ConflictingUpdateException{
		assertNotNull(entityController);
		assertNotNull(testUserProvider);
		userInfo=testUserProvider.getTestAdminUserInfo();
		UserInfo.validateUserInfo(userInfo);
		userId = userInfo.getUser().getUserId();
		toDelete = new ArrayList<String>();
		mockRequest = Mockito.mock(HttpServletRequest.class);
		when(mockRequest.getServletPath()).thenReturn("/repo/v1");
		
		Project project = new Project();
		project.setName("QueryManagerAutowireTest.rootProject2");
		project = entityController.createEntity(userId, project, mockRequest);
		toDelete.add(project.getId());
		
		// Create some datasets
		for(int i=0; i<totalEntities; i++){
			Dataset ds = createForTest(i);
			ds.setParentId(project.getId());
			ds = entityController.createEntity(userId, ds, mockRequest);
			assertNotNull(ds);
			assertNotNull(ds.getId());
			toDelete.add(ds.getId());
			Annotations annos = entityController.getEntityAnnotations(userId, ds.getId(), mockRequest);
			assertNotNull(annos);
			// Add some annotations
			annos.addAnnotation("stringKey", "string"+i);
			annos.addAnnotation("stringListKey", "one");
			annos.addAnnotation("stringListKey", "two");
			annos.addAnnotation("stringListKey", "three");
			annos.addAnnotation("longKey", new Long(i));
			annos.addAnnotation("dateKey", new Date(10000+i));
			annos.addAnnotation("doubleKey", new Double(42*i));
			entityController.updateEntityAnnotations(userId, ds.getId(), annos, mockRequest);
			// Add a layer to each dataset
			Layer inLayer = createLayerForTest(i);
			inLayer.setParentId(ds.getId());
			inLayer = entityController.createEntity(userId, inLayer, mockRequest);
		}
	}
	
	private Dataset createForTest(int i){
		Dataset ds = new Dataset();
		ds.setName("someName"+i);
		ds.setDescription("someDesc"+i);
		ds.setCreatedBy("magic"+i);
		ds.setCreatedOn(new Date(1001));
		ds.setAnnotations("someAnnoUrl"+1);
		ds.setHasClinicalData(false);
		ds.setHasExpressionData(true);
		ds.setHasGeneticData(true);
		ds.setLayers("someLayerUrl"+i);
		ds.setReleaseDate(new Date(15689));
		ds.setStatus("someStatus"+i);
		ds.setVersion("someVersion"+i);
		ds.setUri("someUri"+i);
		return ds;
	}
	
	private Layer createLayerForTest(int i) throws InvalidModelException{
		Layer layer = new Layer();
		layer.setName("layerName"+i);
		layer.setDescription("layerDesc"+i);
		layer.setCreatedOn(new Date(1001));
		layer.setType(LayerTypeNames.G);
		return layer;
	}
	
	@After
	public void after(){
		if(entityController != null && toDelete != null){
			for(String id: toDelete){
				try{
					entityController.deleteEntity(userId, id);
				}catch(Exception e){}
			}
		}
	}
	
	@Test
	public void testExecuteQuery() throws DatastoreException, NotFoundException, UnauthorizedException {
		// Build up the query.
		BasicQuery query = new BasicQuery();
		query.setFrom(EntityType.dataset);
		query.setOffset(0);
		query.setLimit(totalEntities-2);
		query.setSort("longKey");
		query.setAscending(false);
		query.addExpression(new Expression(new CompoundId("dataset", "doubleKey"), Compartor.GREATER_THAN, "0.0"));
		// Execute it.
		long start = System.currentTimeMillis();
		QueryResults results = entityController.executeQueryWithAnnotations(userId, query, Dataset.class, mockRequest);
		long end = System.currentTimeMillis();
		System.out.println("Executed the query in: "+(end-start)+" ms");
		assertNotNull(results);
		assertEquals(totalEntities-1, results.getTotalNumberOfResults());
		// Spot check the results
		assertNotNull(results);
		System.out.println(results);
		assertNotNull(results.getResults());
		List<Map<String, Object>> list = results.getResults();
		assertEquals(8, list.size());
		Map<String, Object> row = list.get(0);
		assertNotNull(row);
		Object ob = row.get("stringListKey");
		assertTrue(ob instanceof Collection);
		Collection<String> collect = (Collection<String>) ob;
		assertTrue(collect.contains("three"));
		
		// Every dataset should have these properties
		assertFalse((Boolean) row.get("hasClinicalData"));
		assertTrue((Boolean) row.get("hasGeneticData"));
		assertFalse((Boolean) row.get("hasExpressionData"));
	}

}