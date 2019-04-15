package com.sample.poc.service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import com.sample.poc.exception.KeyExistException;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.convert.DefaultListDelimiterHandler;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sample.poc.AppProperties;
import com.sample.poc.exception.ConfigNotFoundException;

@Service
public class FileConfigService implements ConfigService {

	@Autowired
	private AppProperties appProperties;

	private PropertiesConfiguration config;

	private FileBasedConfigurationBuilder<PropertiesConfiguration> builder;

	@PostConstruct
	public void init() throws ConfigurationException {
		builder = new FileBasedConfigurationBuilder<PropertiesConfiguration>(PropertiesConfiguration.class)
				.configure(new Parameters().properties().setFileName(appProperties.getConfigFilePath())
						.setListDelimiterHandler(new DefaultListDelimiterHandler(',')));
		config = builder.getConfiguration();

	}

	@Override
	public Property getConfigValue(String key) {
		if(config.getString(key) == null) {
			throw new ConfigNotFoundException(" Key: " + key + " not found");
		}
		return new Property(key, config.getString(key));
		// or may be an exception that key not found ???
	}

	@Override
	public Property setConfigValue(Property value) {
		if (config.containsKey(value.getKey())) {
			throw new KeyExistException(" Key: " + value.getKey() + " already exists.");
		}
		return setValue(value);
	}

	@Override
	public Property updateConfigValue(Property value) {
		return setValue(value);
	}

	@Override
	public List<Property> getAllConfigValues() {
		List<Property> properties = new ArrayList<>();
		config.getKeys().forEachRemaining(t -> properties.add(new Property(t, config.getString(t))));
		return properties;
	}

	private Property setValue(Property value) {
		String old = config.getString(value.getKey());
		String key = value.getKey();
		try {
			config.setProperty(key, value.getValue());
			builder.save();
		} catch (ConfigurationException cex) {
			// revert configuration
			config.setProperty(key, old);
			value.setValue(old);
			// error handling
			throw new RuntimeException(cex);
		}

		return value;
	}

}
