package com.sample.poc.service;

import java.util.List;

public interface ConfigService {

	public Property getConfigValue(String key);

	public Property setConfigValue(Property value);

	public Property updateConfigValue(Property value);

	public List<Property> getAllConfigValues();
}
