package com.joy.integration.workday.core.route.processor;

import java.io.IOException;

import javax.xml.transform.TransformerException;
import javax.xml.validation.Schema;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.http.base.HttpOperationFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import com.joy.integration.workday.core.util.WorkdayIntegrationCoreUtil;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.soap.SOAPException;

@Component
public class WorkdayIntegrationExceptionProcessor implements Processor {

	@Autowired
	private JAXBContext jaxbContext;

	@Autowired
	private Schema schema;

	@Override
	public void process(Exchange exchange) throws Exception {

		Exception exception = exchange.getProperty(Exchange.EXCEPTION_CAUGHT, HttpOperationFailedException.class);
		if (exception instanceof HttpOperationFailedException) {
			handleHttpOperationFailedException(exchange, (HttpOperationFailedException) exception);
		}
		// TODO: More exception handlers to be added in case of other exceptions
	}

	private void handleHttpOperationFailedException(Exchange exchange, HttpOperationFailedException exception)
			throws ClassNotFoundException, IOException, SOAPException, TransformerException, SAXException,
			JAXBException {

		int statusCode = exception.getStatusCode();
		if (statusCode == 500) {
			String response = exception.getResponseBody();
			exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, statusCode);
			exchange.getIn().setBody(response);
			WorkdayIntegrationCoreUtil.unmarshalWorkdayXmlToPojo(exchange, this.jaxbContext, this.schema);
		}
	}

}
