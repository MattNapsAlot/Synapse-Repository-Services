package org.sagebionetworks.web.unitclient.mvp;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.sagebionetworks.web.client.ProtalGinInjector;
import org.sagebionetworks.web.client.mvp.AppActivityMapper;
import org.sagebionetworks.web.client.place.Dataset;
import org.sagebionetworks.web.client.place.DatasetsHome;
import org.sagebionetworks.web.client.place.Home;
import org.sagebionetworks.web.client.presenter.DatasetPresenter;
import org.sagebionetworks.web.client.presenter.DatasetsHomePresenter;
import org.sagebionetworks.web.client.presenter.HomePresenter;
import org.sagebionetworks.web.client.security.AuthenticationController;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.place.shared.Place;

/**
 * Test the activity mapper.
 * 
 * @author jmhill
 *
 */
public class AppActivityMapperTest {
	
	ProtalGinInjector mockInjector;
	AuthenticationController mockController;
	DatasetsHomePresenter mockHome;
	DatasetPresenter mockPresenter;
	HomePresenter mockAll;
	
	@Before
	public void before(){
		// Mock the views
		mockInjector = Mockito.mock(ProtalGinInjector.class);
		// Controller
		mockController = Mockito.mock(AuthenticationController.class);
		when(mockController.isLoggedIn()).thenReturn(true);
		when(mockInjector.getAuthenticationController()).thenReturn(mockController);
		// Dataset home
		mockHome = Mockito.mock(DatasetsHomePresenter.class);
		when(mockInjector.getDatasetsHomePresenter()).thenReturn(mockHome);
		// Dataset presenter
		mockPresenter = Mockito.mock(DatasetPresenter.class);
		when(mockInjector.getDatasetPresenter()).thenReturn(mockPresenter);
		// Home
		mockAll = Mockito.mock(HomePresenter.class);
		when(mockInjector.getHomePresenter()).thenReturn(mockAll);

	}
	
	@Test
	public void testDatasetsHome(){
		// Test this place
		DatasetsHome allDatestsPlace = new DatasetsHome(null);

		// Create the mapper
		AppActivityMapper mapper = new AppActivityMapper(mockInjector);

		Activity object = mapper.getActivity(allDatestsPlace);
		assertNotNull(object);
		assertTrue(object instanceof DatasetsHomePresenter);
		// Validate that the place was set.
		verify(mockHome).setPlace(allDatestsPlace);
	}
	
	@Test
	public void testDatasets(){
		// Mock the views
		ProtalGinInjector mockInjector = Mockito.mock(ProtalGinInjector.class);
		// Controller
		AuthenticationController mockController = Mockito.mock(AuthenticationController.class);
		when(mockController.isLoggedIn()).thenReturn(true);
		when(mockInjector.getAuthenticationController()).thenReturn(mockController);
		// Mock the v
		DatasetPresenter mockPresenter = Mockito.mock(DatasetPresenter.class);
		when(mockInjector.getDatasetPresenter()).thenReturn(mockPresenter);
		// This is the place
		Dataset datasetPlace = new Dataset("ID");

		// Create the mapper
		AppActivityMapper mapper = new AppActivityMapper(mockInjector);

		Activity object = mapper.getActivity(datasetPlace);
		assertNotNull(object);
		assertTrue(object instanceof DatasetPresenter);
//		AllDatasetPresenter resultPresenter =  (AllDatasetPresenter) object;
		// Validate that the place was set.
		verify(mockPresenter).setPlace(datasetPlace);
	}
	
	
	@Test
	public void testUnknown(){

		// This is the place we will pass in
		Place unknownPlace = new Place(){}; 

		// Create the mapper
		AppActivityMapper mapper = new AppActivityMapper(mockInjector);

		Activity object = mapper.getActivity(unknownPlace);
		assertNotNull(object);
		assertTrue(object instanceof HomePresenter);
		// Validate that the place was set.
		verify(mockAll).setPlace((Home) anyObject());
	}

}
