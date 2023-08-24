package com.joy.integration.workday.core.route.processor;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.validation.Schema;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.joy.integration.workday.core.util.WorkdayIntegrationConstants;
import com.joy.integration.workday.util.dto.WorkdayIntegrationRequestDto;
import com.joy.integration.workday.util.generated.dto.ObjectFactory;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.soap.MessageFactory;
import jakarta.xml.soap.SOAPElement;
import jakarta.xml.soap.SOAPException;
import jakarta.xml.soap.SOAPHeader;
import jakarta.xml.soap.SOAPMessage;

@Component
public class WorkdayRequestProcessor implements Processor {

	@Value("${joy.integration.api.workday.soap.username}")
	private String workdaySoapApiSecurityUsername;

	@Value("${joy.integration.api.workday.soap.password}")
	private String workdaySoapApiSecurityPassword;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private JAXBContext jaxbContext;

	@Autowired
	private Schema schema;

	@Override
	public void process(Exchange exchange) throws IllegalArgumentException, ClassNotFoundException, JAXBException,
			IllegalAccessException, InvocationTargetException, NoSuchMethodException, SecurityException,
			ParserConfigurationException, SOAPException, IOException {
		WorkdayIntegrationRequestDto jsonRequestDto = exchange.getIn().getBody(WorkdayIntegrationRequestDto.class);
		Object converted = this.objectMapper.convertValue(jsonRequestDto.getRequestBody(),
				Class.forName(jsonRequestDto.getTargetRequestTypeFQCN()));
		Marshaller marshaller = this.jaxbContext.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		marshaller.setSchema(this.schema);
		JAXBElement<?> jaxbElement = (JAXBElement<?>) invokeTargetReflection(converted,
				jsonRequestDto.getTargetRequestTypeFQCN());
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document document = builder.newDocument();
		marshaller.marshal(jaxbElement, document);
		SOAPMessage xmlRequest = MessageFactory.newInstance().createMessage();
		xmlRequest.setProperty(SOAPMessage.WRITE_XML_DECLARATION, Boolean.TRUE.toString());
		xmlRequest.setProperty(SOAPMessage.CHARACTER_SET_ENCODING, "utf-8");
		xmlRequest.getSOAPBody().addDocument(document);
		xmlRequest.saveChanges();
		addSecurityHeader(xmlRequest);
		ByteArrayOutputStream xmlRequestOutputStream = new ByteArrayOutputStream();
		xmlRequest.writeTo(xmlRequestOutputStream);
		exchange.getIn().setBody(new String(xmlRequestOutputStream.toByteArray()));
	}

	private Object invokeTargetReflection(Object value, String targetTypeFQCN)
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException,
			SecurityException, ClassNotFoundException {
		Class<?> targetTypeClass = Class.forName(targetTypeFQCN);
		String simpleClassName = targetTypeClass.getSimpleName();
		String targetMethodNamePrelim = String.format(WorkdayIntegrationConstants.CREATE_METHOD_FORMAT,
				simpleClassName);
		String targetMethodName = targetMethodNamePrelim.substring(0,
				targetMethodNamePrelim.lastIndexOf(WorkdayIntegrationConstants.TYPE));
		ObjectFactory objectFactory = new ObjectFactory();
		Class<ObjectFactory> objectFactoryClass = ObjectFactory.class;
		Method method = objectFactoryClass.getMethod(targetMethodName, targetTypeClass);
		return method.invoke(objectFactory, value);
	}

	private void addSecurityHeader(SOAPMessage xmlRequest) throws SOAPException {
		SOAPHeader header = xmlRequest.getSOAPPart().getEnvelope().getHeader();
		SOAPElement security = header.addChildElement(WorkdayIntegrationConstants.SECURITY,
				WorkdayIntegrationConstants.WSSE, WorkdayIntegrationConstants.WSSE_XSD_LOCATION);
		SOAPElement usernameToken = security.addChildElement(WorkdayIntegrationConstants.USERNAME_TOKEN,
				WorkdayIntegrationConstants.WSSE);
		usernameToken.addAttribute(new QName(WorkdayIntegrationConstants.XMLNS_WSU),
				WorkdayIntegrationConstants.XMLNS_WSU_LOCATION);
		SOAPElement username = usernameToken.addChildElement(WorkdayIntegrationConstants.USERNAME,
				WorkdayIntegrationConstants.WSSE);
		username.addTextNode(this.workdaySoapApiSecurityUsername);
		SOAPElement password = usernameToken.addChildElement(WorkdayIntegrationConstants.PASSWORD,
				WorkdayIntegrationConstants.WSSE);
		password.addTextNode(this.workdaySoapApiSecurityPassword);
		xmlRequest.saveChanges();

	}

}
