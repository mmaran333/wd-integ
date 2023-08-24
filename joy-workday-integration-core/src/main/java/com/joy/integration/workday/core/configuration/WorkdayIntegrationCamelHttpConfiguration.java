package com.joy.integration.workday.core.configuration;

import org.apache.camel.CamelContext;
import org.apache.camel.component.http.HttpComponent;
import org.apache.camel.support.jsse.KeyStoreParameters;
import org.apache.camel.support.jsse.SSLContextParameters;
import org.apache.camel.support.jsse.TrustManagersParameters;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.joy.integration.workday.core.util.WorkdayIntegrationConstants;

@Configuration
public class WorkdayIntegrationCamelHttpConfiguration {

	@Value("${joy.integration.api.workday.soap.truststore-location}")
	private String workdayTruststoreLocation;
	
	@Value("${joy.integration.api.workday.soap.truststore-secret}")
	private String workdayTruststoreSecret;

	@Bean
	public HttpComponent httpComponent(CamelContext camelContext) {
		
		SSLContextParameters sslContextParameters = new SSLContextParameters();

		KeyStoreParameters truststoreParameters = new KeyStoreParameters();
		truststoreParameters.setResource(this.workdayTruststoreLocation);
		truststoreParameters.setPassword(this.workdayTruststoreSecret);

		TrustManagersParameters trustManagersParameters = new TrustManagersParameters();
		trustManagersParameters.setKeyStore(truststoreParameters);

		sslContextParameters.setTrustManagers(trustManagersParameters);
		
		HttpComponent httpComponent = camelContext.getComponent(WorkdayIntegrationConstants.HTTPS, HttpComponent.class);
		httpComponent.setSslContextParameters(sslContextParameters);
		
		return httpComponent;
	}
}
