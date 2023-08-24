package com.joy.integration.workday.core.route;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import com.joy.integration.workday.core.route.processor.WorkdayRequestProcessor;
import com.joy.integration.workday.core.route.processor.WorkdayResponseProcessor;
import com.joy.integration.workday.core.util.WorkdayIntegrationConstants;

@Component
public class WorkdayIntegrationRoute extends RouteBuilder {

	private static final String LOG_ERROR = "log:error";

	@Value("${joy.integration.api.workday.soap.uri}")
	private String workdaySoapApiUri;

	@Autowired
	private WorkdayRequestProcessor workdayRequestProcessor;

	@Autowired
	private WorkdayResponseProcessor workdayResponseProcessor;

	@Override
	public void configure() throws Exception {

		from(WorkdayIntegrationConstants.DIRECT_SOAP_ROUTE).process(workdayRequestProcessor)
				.log(WorkdayIntegrationConstants.EXCHANGE_BODY)
				.setHeader(WorkdayIntegrationConstants.CAMEL_HTTP_METHOD, constant(HttpMethod.POST.name()))
				.setHeader(WorkdayIntegrationConstants.CONTENT_TYPE, constant(MediaType.TEXT_XML_VALUE)).setBody()
				.simple(WorkdayIntegrationConstants.EXCHANGE_BODY).log(WorkdayIntegrationConstants.EXCHANGE_BODY)
				.to(this.workdaySoapApiUri).log(WorkdayIntegrationConstants.EXCHANGE_BODY)
				.log(WorkdayIntegrationConstants.EXCHANGE_BODY).process(this.workdayResponseProcessor)
				.onException(Exception.class).handled(true).to(LOG_ERROR)
				.to(WorkdayIntegrationConstants.ERROR_HANDLER_ROUTE).log(WorkdayIntegrationConstants.EXCHANGE_BODY);
	}

}
