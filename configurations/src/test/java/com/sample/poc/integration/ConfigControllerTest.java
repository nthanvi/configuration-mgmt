package com.sample.poc.integration;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Consumer;

import com.sample.poc.repository.UserRepository;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sample.poc.model.AppUser;
import com.sample.poc.service.Property;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ConfigControllerTest {

	@LocalServerPort
	private int port;

	@Autowired
	private TestRestTemplate template;

	HttpHeaders headers = new HttpHeaders();

	@Autowired
	UserRepository repo;

	@After
	public void clean() {
		repo.deleteAll();
	}

	@Test
	public void WithAuthRequest_ValidateConfigGetAllKeyRequest() throws Exception {
		exeucteWithLogin(entity -> {
			ResponseEntity<String> getResponse = template.exchange(createURLWithPort("/configs"), HttpMethod.GET,
					entity, String.class);

			assertEquals(200, getResponse.getStatusCode().value());

		});
	}

	@Test
	public void WithAuthRequest_ValidateConfigPutKeyRequest() throws Exception {
		exeucteWithLogin(entity -> {
			Property property = new Property("name", "newvalue");
			HttpEntity<String> putEntity = null;
			try {
				putEntity = new HttpEntity<String>(new ObjectMapper().writeValueAsString(property), headers);
			} catch (Exception e) {
				Assert.fail(e.getMessage());
			}

			ResponseEntity<String> putResponse = template.exchange(createURLWithPort("/configs/key"), HttpMethod.PUT,
					putEntity, String.class);

			assertEquals(200, putResponse.getStatusCode().value());
		});

	}

	@Test
	public void WithAuthRequest_ValidateConfigPostKeyRequest() throws Exception {
		exeucteWithLogin(entity -> {
			Property property = new Property("newName", "newvalue");
			HttpEntity<String> postEntity = null;
			try {
				postEntity = new HttpEntity<String>(new ObjectMapper().writeValueAsString(property), headers);
			} catch (Exception e) {
				Assert.fail(e.getMessage());
			}

			ResponseEntity<String> postResponse = template.exchange(createURLWithPort("/configs/key"),
					HttpMethod.POST, postEntity, String.class);

			assertEquals(201, postResponse.getStatusCode().value());

			ResponseEntity<String> getResponse = template.exchange(createURLWithPort("/configs/newName"),
					HttpMethod.GET, entity, String.class);

			assertEquals(200, getResponse.getStatusCode().value());
		});
	}

	@Test
	public void WithAuthRequest_ValidateConfigGetKeyRequest() throws Exception {
		exeucteWithLogin(entity -> {

			ResponseEntity<String> getResponse = template.exchange(createURLWithPort("/configs/name"), HttpMethod.GET,
					entity, String.class);

			assertEquals(200, getResponse.getStatusCode().value());
			try {
				assertEquals("newvalue",
						new ObjectMapper().readValue(getResponse.getBody(), Property.class).getValue());
			} catch (Exception e) {
				Assert.fail(e.getMessage());
			}
		});

	}

	@Test
	public void WithAuthRequest_OnlyOnePutRequestSucceed() throws Exception {
		exeucteWithLogin(entity -> {

			ResponseEntity<String> getResponse = template.exchange(createURLWithPort("/configs/name"), HttpMethod.GET,
					entity, String.class);

			assertEquals(200, getResponse.getStatusCode().value());
			try {
				assertEquals("newvalue",
						new ObjectMapper().readValue(getResponse.getBody(), Property.class).getValue());
			} catch (Exception e) {
				Assert.fail(e.getMessage());
			}

			List<HttpStatus> returnCodes = run2PutRequestsTogether(entity);
			
			Assert.assertTrue(returnCodes.contains(HttpStatus.TOO_MANY_REQUESTS) && returnCodes.contains(HttpStatus.OK));
		});

	}

	private List<HttpStatus> run2PutRequestsTogether(HttpEntity<String> entity) {

		ExecutorService executorService = Executors.newFixedThreadPool(2);
		Future<HttpStatus> future1 = executorService.submit(createPutTask());
		Future<HttpStatus> future2 = executorService.submit(createPutTask());
		try {
			return Arrays.asList(future1.get(), future2.get());
		} catch (InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new ArrayList<>();

	}

	private Callable<HttpStatus> createPutTask() {

		return () -> {
			Property property = new Property("name", "newvalue" + System.currentTimeMillis());
			HttpEntity<String> putEntity = null;
			try {
				putEntity = new HttpEntity<String>(new ObjectMapper().writeValueAsString(property), headers);
			} catch (Exception e) {
				Assert.fail(e.getMessage());
			}
			return template.exchange(createURLWithPort("/configs/key"), HttpMethod.PUT, putEntity, String.class)
					.getStatusCode();
		};

	}

	private void exeucteWithLogin(Consumer<HttpEntity<String>> func) throws Exception {
		AppUser user = getUser(System.currentTimeMillis() + "");

		sign_up(user);

		ResponseEntity<String> response = login(user);
		response.getHeaders().toSingleValueMap().forEach((x, y) -> headers.add(x, y));
		HttpEntity<String> entity = new HttpEntity<String>("{}", response.getHeaders());
		func.accept(entity);
	}

	private AppUser getUser(String id) {
		AppUser user = new AppUser();
		user.setUsername(id);
		user.setPassword("password");
		return user;
	}

	private ResponseEntity<String> sign_up(AppUser user) throws Exception {
		headers.add("Content-Type", "application/json");
		HttpEntity<String> entity = new HttpEntity<String>(new ObjectMapper().writeValueAsString(user), headers);
		ResponseEntity<String> response = template.exchange(createURLWithPort("/sign-up"), HttpMethod.POST, entity,
				String.class);

		assertEquals(200, response.getStatusCode().value());

		return response;
	}

	private ResponseEntity<String> login(AppUser user) throws Exception {
		HttpEntity<String> entity = new HttpEntity<String>(new ObjectMapper().writeValueAsString(user), headers);
		ResponseEntity<String> response = template.exchange(createURLWithPort("/login"), HttpMethod.POST, entity,
				String.class);

		assertEquals(200, response.getStatusCode().value());

		return response;
	}

	private String createURLWithPort(String uri) {
		return "http://localhost:" + port + uri;
	}
}
