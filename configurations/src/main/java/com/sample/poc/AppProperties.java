package com.sample.poc;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource("classpath:application.properties")
public class AppProperties {

    @Value("${config.file}")
	private String configFilePath;



	@Value("${swagger.server}")
	private String swaggerServer;


	public String getConfigFilePath() {
		return configFilePath;
	}

	public void setConfigFilePath(String configFilePath) {
		this.configFilePath = configFilePath;
	}


	public String getSwaggerServer() {
		return swaggerServer;
	}

	public void setSwaggerServer(String swaggerServer) {
		this.swaggerServer = swaggerServer;
	}
}
