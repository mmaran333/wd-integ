package com.joy.integration.workday.util.dto;

import org.springframework.http.HttpStatus;

import jakarta.xml.bind.JAXBElement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkdayIntegrationResponseDto {
	
	private HttpStatus httpStatus;
	private JAXBElement<?> jaxbElement;

}
