package com.joy.integration.workday.core.route.processor;

import javax.xml.validation.Schema;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.joy.integration.workday.core.util.WorkdayIntegrationCoreUtil;

import jakarta.xml.bind.JAXBContext;

@Component
public class WorkdayResponseProcessor implements Processor {

	@Autowired
	private JAXBContext jaxbContext;

	@Autowired
	private Schema schema;

	@Override
	public void process(Exchange exchange) throws Exception {
		WorkdayIntegrationCoreUtil.unmarshalWorkdayXmlToPojo(exchange, this.jaxbContext, this.schema);
	}

}
