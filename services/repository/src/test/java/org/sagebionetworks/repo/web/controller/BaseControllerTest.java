package org.sagebionetworks.repo.web.controller;

import static org.junit.Assert.assertEquals;

import javax.servlet.http.HttpServletRequest;

import org.apache.http.conn.ConnectTimeoutException;
import org.junit.Test;
import org.sagebionetworks.repo.model.ErrorResponse;
import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author deflaux
 *
 */
public class BaseControllerTest {

	/**
	 * 
	 */
	@Test
	public void testCSExceptionScrubbing() {

		BaseController controller = new SearchController();
		HttpServletRequest request = new MockHttpServletRequest();
		Throwable ex = new ConnectTimeoutException(
				"Connect to search-prod-20120206-vigj35bjslyimyxftqh4mludxm.us-east-1.cloudsearch.amazonaws.com:80 timed out");
		// org.apache.http.conn.ConnectTimeoutException: Connect to
		// search-prod-20120206-vigj35bjslyimyxftqh4mludxm.us-east-1.cloudsearch.amazonaws.com:80
		// timed out
		ErrorResponse response = controller.handleException(ex, request);
		assertEquals("search failed, try again", response.getReason());
	}

}
