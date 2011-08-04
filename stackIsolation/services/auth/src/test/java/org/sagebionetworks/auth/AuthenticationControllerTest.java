package org.sagebionetworks.auth;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.sagebionetworks.authutil.AuthenticationException;
import org.sagebionetworks.authutil.CrowdAuthUtil;
import org.sagebionetworks.authutil.User;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * 
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:authentication-context.xml", "classpath:authentication-servlet.xml" })
public class AuthenticationControllerTest {

	private static final Logger log = Logger
			.getLogger(AuthenticationControllerTest.class.getName());
	private Helpers helper = new Helpers();
	//private DispatcherServlet servlet;
		
	private CrowdAuthUtil crowdAuthUtil = null;
	
	private boolean isIntegrationTest() {
		String integrationTestEndpoint = System.getProperty("INTEGRATION_TEST_ENDPOINT");
		return (integrationTestEndpoint!=null && integrationTestEndpoint.length()>0);
	}


	String integrationTestUserEmail = null;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		if (!isIntegrationTest()) return;
		crowdAuthUtil = new CrowdAuthUtil();
		//DispatcherServlet servlet = 
			helper.setUp();
		CrowdAuthUtil.acceptAllCertificates2();
		
		// special userId for testing -- no confirmation email is sent!
		Properties props = new Properties();
        InputStream is = AuthenticationControllerTest.class.getClassLoader().getResourceAsStream("authenticationcontroller.properties");
        try {
        	props.load(is);
        } catch (IOException e) {
        	throw new RuntimeException(e);
        }
        integrationTestUserEmail = props.getProperty("integrationTestUser");
		assertNotNull(integrationTestUserEmail);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		helper.tearDown();
	}

	
	@Test
	public void testCreateSession() throws Exception {
		if (!isIntegrationTest()) return;
		JSONObject session = helper.testCreateJsonEntity("/session",
				"{\"email\":\"demouser@sagebase.org\",\"password\":\"demouser-pw\"}");
		assertTrue(session.has("sessionToken"));
		assertEquals("Demo User", session.getString("displayName"));
	}
	
	@Test
	public void testCreateSessionBadCredentials() throws Exception {
		if (!isIntegrationTest()) return;
		JSONObject session = helper.testCreateJsonEntityShouldFail("/session",
				"{\"email\":\"demouser@sagebase.org\",\"password\":\"incorrectPassword\"}", HttpStatus.BAD_REQUEST);
		assertEquals("Unable to authenticate", session.getString("reason"));
		// AuthenticationURL: https://ssl.latest.deflaux-test.appspot.com/auth/v1/session
	}

	
	@Test
	public void testRevalidateUtil() throws Exception {
		if (!isIntegrationTest()) return;
		// start session
		JSONObject session = helper.testCreateJsonEntity("/session",
				"{\"email\":\"demouser@sagebase.org\",\"password\":\"demouser-pw\"}");
		String sessionToken = session.getString("sessionToken");
		assertEquals("Demo User", session.getString("displayName"));
		
		// revalidate via utility function
		String userId = null;
		try {
			userId = crowdAuthUtil.revalidate(sessionToken);
		} catch (Exception e) {
			log.log(Level.WARNING, "exception during 'revalidate'", e);
		}
		log.info("UserId: "+userId);
		assertEquals("demouser@sagebase.org", userId);
	}
	
	
	@Test
	public void testRevalidateSvc() throws Exception {
		if (!isIntegrationTest()) return;
		// start session
		JSONObject session = helper.testCreateJsonEntity("/session",
				"{\"email\":\"demouser@sagebase.org\",\"password\":\"demouser-pw\"}", HttpStatus.CREATED);
		String sessionToken = session.getString("sessionToken");
		assertEquals("Demo User", session.getString("displayName"));
		
		// revalidate via web service
		helper.testUpdateJsonEntity("/session",	"{\"sessionToken\":\""+sessionToken+"\"}", HttpStatus.NO_CONTENT);
		
	}

	
	@Test
	public void testRevalidateBadTokenUtil() throws Exception {
		if (!isIntegrationTest()) return;
		try {
			crowdAuthUtil.revalidate("invalidToken");
			fail("exception expected");
		} catch (Exception e) {
			// as expected
			//log.log(Level.INFO, "this exception is expected", e);
		}
	}

	
	@Test
	public void testRevalidateBadTokenSvc() throws Exception {
		if (!isIntegrationTest()) return;
		
		// revalidate via web service
		helper.testUpdateJsonEntityShouldFail("/session", "{\"sessionToken\":\"invalid-token\"}", HttpStatus.NOT_FOUND);
	}

	
	@Test
	public void testCreateSessionThenLogout() throws Exception {
		if (!isIntegrationTest()) return;
		JSONObject session = helper.testCreateJsonEntity("/session",
				"{\"email\":\"demouser@sagebase.org\",\"password\":\"demouser-pw\"}");
		String sessionToken = session.getString("sessionToken");
		assertEquals("Demo User", session.getString("displayName"));
		
		helper.testDeleteJsonEntity("/session", "{\"sessionToken\":\""+sessionToken+"\"}");
		
	}

	
	@Test
	public void testCreateExistingUser() throws Exception {
		if (!isIntegrationTest()) return;
			helper.testCreateJsonEntityShouldFail("/user",
				"{\"email\":\"demouser@sagebase.org\","+
				"\"firstName\":\"Demo\","+
				"\"lastName\":\"User\","+
				"\"displayName\":\"Demo User\""+
					"}", HttpStatus.BAD_REQUEST);


	}
	
	
	@Test
	public void testCreateNewUser() throws Exception {
		if (!isIntegrationTest()) return;
		try {
			helper.testCreateJsonEntity("/user",
					"{"+
					"\"email\":\""+integrationTestUserEmail+"\","+
					 // integration testing with this special user is the only time a password may be specified
					"\"password\":\""+integrationTestUserEmail+"\","+
				"\"firstName\":\"New\","+
				"\"lastName\":\"User\","+
				"\"displayName\":\"New User\""+
					"}");
		} finally {
			User user = new User();
			user.setEmail(integrationTestUserEmail);
			try {
				crowdAuthUtil.deleteUser(user);
			} catch (AuthenticationException ae) {
				if (ae.getRespStatus()==HttpStatus.NOT_FOUND.value()) {
					// that's OK, it just means that the user never was created in the first place
				} else {
					throw ae;
				}
			}
		}

	}
	
	
	@Test
	public void testCreateAndGetNewUser() throws Exception {
		if (!isIntegrationTest()) return;
		try {
			helper.testCreateJsonEntity("/user",
					"{"+
					"\"email\":\""+integrationTestUserEmail+"\","+
					 // integration testing with this special user is the only time a password may be specified
					"\"password\":\""+integrationTestUserEmail+"\","+
				"\"firstName\":\"New\","+
				"\"lastName\":\"User\","+
				"\"displayName\":\"New User\""+
					"}");
			
			JSONObject user = helper.testGetJsonEntity("/user");
			assertEquals(integrationTestUserEmail, user.getString("email"));
			assertEquals("New", user.getString("firstName"));
			assertEquals("User", user.getString("lastName"));
			assertEquals("New User", user.getString("displayName"));

		} finally {
			User user = new User();
			user.setEmail(integrationTestUserEmail);
			try {
				crowdAuthUtil.deleteUser(user);
			} catch (AuthenticationException ae) {
				if (ae.getRespStatus()==HttpStatus.NOT_FOUND.value()) {
					// that's OK, it just means that the user never was created in the first place
				} else {
					throw ae;
				}
			}
		}

	}
	
	@Test
	public void testCreateAndUpdateUser() throws Exception {
		if (!isIntegrationTest()) return;
		
		// special userId for testing -- no confirmation email is sent!
		try {
			helper.testCreateJsonEntity("/user",
					"{"+
					"\"email\":\""+integrationTestUserEmail+"\","+
					 // integration testing with this special user is the only time a password may be specified
					"\"password\":\""+integrationTestUserEmail+"\","+
				"\"firstName\":\"New\","+
				"\"lastName\":\"User\","+
				"\"displayName\":\"New User\""+
					"}");
				
			helper.testUpdateJsonEntity("/user",
					"{"+
					"\"email\":"+integrationTestUserEmail+","+
					"\"firstName\":\"NewNEW\","+
					"\"lastName\":\"UserNEW\","+
					"\"displayName\":\"New NEW User\""+				
					"}", HttpStatus.NO_CONTENT);
			
			JSONObject user = helper.testGetJsonEntity("/user");
			assertEquals(integrationTestUserEmail, user.getString("email"));
			assertEquals("NewNEW", user.getString("firstName"));
			assertEquals("UserNEW", user.getString("lastName"));
			assertEquals("New NEW User", user.getString("displayName"));
		
		} finally {
			User user = new User();
			user.setEmail(integrationTestUserEmail);
			crowdAuthUtil.deleteUser(user);
		}
	}
	
	@Test
	public void testCreateUserAndChangePassword() throws Exception {
		if (!isIntegrationTest()) return;
		
		// special userId for testing -- no confirmation email is sent!
		try {
			helper.testCreateJsonEntity("/user",
					"{"+
					"\"email\":\""+integrationTestUserEmail+"\","+
					 // integration testing with this special user is the only time a password may be specified
					"\"password\":\""+integrationTestUserEmail+"\","+
				"\"firstName\":\"New\","+
				"\"lastName\":\"User\","+
				"\"displayName\":\"New User\""+
					"}");
				

			String testNewPassword = "newPassword";
			
			helper.testCreateNoResponse("/userPassword",
					"{"+
					"\"email\":\""+integrationTestUserEmail+"\","+
					"\"password\":\""+testNewPassword+"\""+					
					"}");

			
			// to check the password, we have to try to log-in
			JSONObject session = helper.testCreateJsonEntity("/session",
					"{\"email\":\""+integrationTestUserEmail+"\",\"password\":\""+testNewPassword+"\"}");
			assertTrue(session.has("sessionToken"));
			assertEquals("New User", session.getString("displayName"));
			
		} finally {
			User user = new User();
			user.setEmail(integrationTestUserEmail);
			crowdAuthUtil.deleteUser(user);
		}
	}
	
	// can't expect to do this regularly, as it generates email messages
	@Ignore
	@Test
	public void testSendResetPasswordEmail() throws Exception {
		if (!isIntegrationTest()) return;
		 helper.testCreateJsonEntity("/userPasswordEmail","{\"email\":\"demouser@sagebase.org\"}", HttpStatus.NO_CONTENT);
	}
	

	// can't expect to do this regularly, as it generates email messages
	@Ignore
	@Test
	public void testSetAPIPasswordEmail() throws Exception {
		if (!isIntegrationTest()) return;
		 helper.testCreateJsonEntity("/apiPasswordEmail","{\"email\":\"demouser@sagebase.org\"}", HttpStatus.NO_CONTENT);
	}
	

	@Test
	public void testSendEmailInvalidUser() throws Exception {
		if (!isIntegrationTest()) return;
		 helper.testCreateJsonEntity("/userPasswordEmail","{\"email\":\"foo@sagebase.org\"}", HttpStatus.BAD_REQUEST);
	}
	
	class MutableBoolean {
		boolean b = false;
		public void set(boolean b) {this.b=b;}
		public boolean get() {return b;}
	}
	
	// this is meant to recreate the problem described in PLFM-292
	// http://sagebionetworks.jira.com/browse/PLFM-292
	@Test 
	public void testMultipleLogins() throws Exception {
		if (!isIntegrationTest()) return;
		int n = 100;
		for (int i=0; i<n; i++) {
			final MutableBoolean b = new MutableBoolean();
		 	Thread thread = new Thread() {
				public void run() {
					try {
						JSONObject session = helper.testCreateJsonEntity("/session",
						"{\"email\":\"demouser@sagebase.org\",\"password\":\"demouser-pw\"}");
						assertTrue(session.has("sessionToken"));
						assertEquals("Demo User", session.getString("displayName"));
						b.set(true);
					} catch (Exception e) {
						fail(e.toString());
					}
				}
			};
			thread.start();
			long start = System.currentTimeMillis();
			try {
				thread.join(5000L); // time out after 5 sec
//				thread.join(20000L); // time out
			} catch (InterruptedException ie) {
				// as expected
			}
//			System.out.println(""+i+": done after "+(System.currentTimeMillis()-start)+" ms.");
			assertTrue("Failed or timed out after "+i+" iterations.", b.get()); // should have been set to 'true' if successful
		}
	}

}


