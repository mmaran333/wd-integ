package com.joy.integration.workday.util.dto;

public class WorkdayIntegrationRequestDto {
	
	private Object requestBody;
	private String targetRequestTypeFQCN;
	
	public void setRequestBody(Object requestBody) {
		this.requestBody = requestBody;
	}
	
	public Object getRequestBody() {
		return this.requestBody;
	}

	public String getTargetRequestTypeFQCN() {
		return targetRequestTypeFQCN;
	}

	public void setTargetRequestTypeFQCN(String targetRequestTypeFQCN) {
		this.targetRequestTypeFQCN = targetRequestTypeFQCN;
	}
	
}
