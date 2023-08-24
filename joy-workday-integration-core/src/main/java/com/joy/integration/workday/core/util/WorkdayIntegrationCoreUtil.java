package com.joy.integration.workday.core.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;

import org.apache.camel.Exchange;
import org.springframework.http.HttpStatus;
import org.xml.sax.SAXException;

import com.joy.integration.workday.util.dto.WorkdayIntegrationResponseDto;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import jakarta.xml.bind.ValidationEvent;
import jakarta.xml.bind.ValidationEventHandler;
import jakarta.xml.bind.ValidationEventLocator;
import jakarta.xml.soap.MessageFactory;
import jakarta.xml.soap.SOAPException;
import jakarta.xml.soap.SOAPMessage;

public class WorkdayIntegrationCoreUtil {

	public static void unmarshalWorkdayXmlToPojo(Exchange exchange, JAXBContext jaxbContext, Schema schema) throws IOException, SOAPException, TransformerException, SAXException, JAXBException,
			ClassNotFoundException {
		String xmlResponse = exchange.getIn().getBody(String.class);
		byte[] bytes = xmlResponse.getBytes(StandardCharsets.UTF_8);
		try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
				StringWriter writer = new StringWriter()) {
			SOAPMessage soapMessage = MessageFactory.newInstance().createMessage(null, byteArrayInputStream);
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(soapMessage.getSOAPBody().hasFault() ? soapMessage.getSOAPBody().getFault().getDetail().getFirstChild()
					: soapMessage.getSOAPBody().getFirstChild());
			StreamResult result = new StreamResult(writer);

			transformer.transform(source, result);
			byte[] bodyBytes = writer.toString().getBytes(StandardCharsets.UTF_8);
			InputStream inputStream = new ByteArrayInputStream(bodyBytes);
			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
			unmarshaller.setSchema(schema);
			unmarshaller.setEventHandler(new ValidationEventHandler() {
				@Override
				public boolean handleEvent(ValidationEvent event) {
					if (event.getSeverity() == ValidationEvent.FATAL_ERROR
							|| event.getSeverity() == ValidationEvent.ERROR) {
						ValidationEventLocator validationEventLocator = event.getLocator();
						throw new IllegalStateException(String.format(
								"An exception has occurred at line %d and column %d",
								validationEventLocator.getLineNumber(), validationEventLocator.getColumnNumber()));
					}
					return true;
				}
			});
			Object unmarshalledData = unmarshaller.unmarshal(new StreamSource(inputStream));
			Integer statusCode = (Integer) exchange.getIn().getHeader(Exchange.HTTP_RESPONSE_CODE);
			WorkdayIntegrationResponseDto workdayIntegrationResponseDto = new WorkdayIntegrationResponseDto(HttpStatus.resolve(statusCode), Objects.nonNull(unmarshalledData) && unmarshalledData instanceof JAXBElement<?> ? (JAXBElement<?>) unmarshalledData : null);
			exchange.getIn().setBody(workdayIntegrationResponseDto);
		}
	}

}
