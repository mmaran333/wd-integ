package com.joy.integration.workday.core.manager;

import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.joy.integration.workday.core.util.WorkdayIntegrationConstants;
import com.joy.integration.workday.util.dto.WorkdayIntegrationRequestDto;
import com.joy.integration.workday.util.dto.WorkdayIntegrationResponseDto;

@Service
public class WorkdayIntegrationManager {

	@Autowired
	private CamelContext camelContext;

	public WorkdayIntegrationResponseDto consumeAndConvert(WorkdayIntegrationRequestDto workdayIntegrationRequestDto) {
		ProducerTemplate producerTemplate = camelContext.createProducerTemplate();
		return producerTemplate.requestBody(WorkdayIntegrationConstants.DIRECT_SOAP_ROUTE, workdayIntegrationRequestDto,
				WorkdayIntegrationResponseDto.class);
	}
}
