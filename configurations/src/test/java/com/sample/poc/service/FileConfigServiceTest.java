package com.sample.poc.service;

import com.sample.poc.repository.UserRepository;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.sample.poc.AppProperties;


@RunWith(SpringRunner.class)
@EnableAutoConfiguration
@SpringBootTest
//@ContextConfiguration({ "classpath:application.properties" })
public class FileConfigServiceTest {

	@Autowired
	AppProperties appProperties;
	@Autowired
	ConfigService configService;


	@Autowired
	UserRepository repo;

	@After
	public void clean() {
		repo.deleteAll();
	}
	@Test
	public void whenMatch_CountOfConfig() throws ConfigurationException {
		System.out.println(appProperties);
		//configService.init();
		Assert.assertEquals(5, configService.getAllConfigValues().size());
		//fail("Not yet implemented");
	}

	@Test
	public void whenNewValueComes_AfterPut() throws ConfigurationException {
		System.out.println(appProperties);
		//configService.init();
		configService.setConfigValue(new Property("Test-123", "TestValue"));
		Assert.assertEquals("TestValue", configService.getConfigValue("Test-123").getValue());
		
		configService.setConfigValue(new Property("Test-123", "TestValue2"));
		Assert.assertEquals("TestValue2", configService.getConfigValue("Test-123").getValue());
		//fail("Not yet implemented");
	}

}
