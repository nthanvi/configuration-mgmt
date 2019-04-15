package com.sample.poc.controller;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.Semaphore;
import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sample.poc.exception.ConfigNotFoundException;
import com.sample.poc.exception.OnlyOneRequestPermittedException;
import com.sample.poc.service.ConfigService;
import com.sample.poc.service.Property;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;

@RestController
@RequestMapping("/configs")
public class ConfigController {

	@Autowired
	private ConfigService configService;
	private Semaphore semaphore = new Semaphore(1);

	@RequestMapping(method = RequestMethod.GET)
	@ApiOperation(value = "", authorizations = { @Authorization(value = "apiKey") })
	public List<Property> getAllConfigValues() {
		return configService.getAllConfigValues();
	}

	@RequestMapping(value = "/{key}", method = RequestMethod.GET)
	@ApiOperation(value = "", authorizations = { @Authorization(value = "apiKey") })
	public Property getConfigValue(@PathVariable String key) {
		return Optional.ofNullable(configService.getConfigValue(key)).orElseThrow(()
				-> new ConfigNotFoundException(" Key: " + key + " not found"));
	}

	@RequestMapping(value = "/key", method = RequestMethod.PUT)
	@ApiOperation(value = "", authorizations = { @Authorization(value = "apiKey") })
	public Property updateConfigValue(@RequestBody Property value) {
		return executeOneAtATime(() -> configService.updateConfigValue(value));
	}


	@RequestMapping(value = "/key", method = RequestMethod.POST)
	@ApiOperation(value = "", authorizations = { @Authorization(value = "apiKey") })
	public ResponseEntity<Void> saveConfigValue(@RequestBody Property value) {
		configService.setConfigValue(value);
		return new ResponseEntity<>(HttpStatus.CREATED);
	}


	private <R> R executeOneAtATime(Supplier<R> func) {
		try {
			if (!semaphore.tryAcquire()) {
				throw new OnlyOneRequestPermittedException(
						"There is already one request is in process. So cannot proceed more.");
			}
			return func.get();
		} finally {
			semaphore.release();
		}
	}
}
