package com.joy.integration.workday.core.configuration;

import java.io.IOException;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.xml.sax.SAXException;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.joy.integration.workday.util.WorkdayIntegrationUtilConstants;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;

@Configuration
public class WorkdayIntegrationCoreConfiguration {

	@Value("${joy.integration.api.workday.soap.schema-location}")
	private String xsdSchemaLocation;

	@Autowired
	private ResourceLoader resourceLoader;

	@Bean
	public Schema schema() throws SAXException, IOException {
		SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		Resource resource = this.resourceLoader.getResource(this.xsdSchemaLocation);
		if (resource.exists()) {
			Schema schema = schemaFactory.newSchema(new StreamSource(resource.getInputStream()));
			return schema;
		} else {
			throw new IllegalStateException("The schema.xsd file couldn't be located");
		}
	}

	@Bean
	public JAXBContext jaxbContext() throws JAXBException {
		JAXBContext jaxbContext = JAXBContext
				.newInstance(WorkdayIntegrationUtilConstants.DTO_AUTO_GENERATION_PACKAGE_NAME);
		return jaxbContext;
	}

	@Bean
	public ObjectMapper objectMapper() {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		objectMapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
		return objectMapper;
	}

}
