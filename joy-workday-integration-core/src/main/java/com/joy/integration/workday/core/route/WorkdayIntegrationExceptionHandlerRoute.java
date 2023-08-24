package com.joy.integration.workday.core.route;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.joy.integration.workday.core.route.processor.WorkdayIntegrationExceptionProcessor;
import com.joy.integration.workday.core.util.WorkdayIntegrationConstants;

@Component
public class WorkdayIntegrationExceptionHandlerRoute extends RouteBuilder {

	@Autowired
	private WorkdayIntegrationExceptionProcessor workdayIntegrationExceptionProcessor;

	@Override
	public void configure() throws Exception {

		from(WorkdayIntegrationConstants.ERROR_HANDLER_ROUTE).process(workdayIntegrationExceptionProcessor)
				.log(WorkdayIntegrationConstants.EXCHANGE_BODY);
	}

}
