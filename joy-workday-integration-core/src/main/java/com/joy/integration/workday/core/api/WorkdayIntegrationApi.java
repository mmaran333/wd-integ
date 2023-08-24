package com.joy.integration.workday.core.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.joy.integration.workday.core.manager.WorkdayIntegrationManager;
import com.joy.integration.workday.util.dto.WorkdayIntegrationRequestDto;
import com.joy.integration.workday.util.dto.WorkdayIntegrationResponseDto;

import jakarta.xml.bind.JAXBElement;

@RestController
@RequestMapping("/v1/workday-integrations")
public class WorkdayIntegrationApi {

	@Autowired
	private WorkdayIntegrationManager workdayIntegrationManager;

	@PostMapping
	public ResponseEntity<JAXBElement<?>> consumeAndConvert(
			@RequestBody WorkdayIntegrationRequestDto workdayIntegrationRequestDto) {
		WorkdayIntegrationResponseDto workdayIntegrationResponseDto = this.workdayIntegrationManager.consumeAndConvert(workdayIntegrationRequestDto);
		return new ResponseEntity<>(workdayIntegrationResponseDto.getJaxbElement(), workdayIntegrationResponseDto.getHttpStatus());
	}
}
