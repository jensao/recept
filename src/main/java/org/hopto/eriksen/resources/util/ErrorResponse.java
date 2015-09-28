package org.hopto.eriksen.resources.util;

import javax.xml.bind.annotation.XmlRootElement;


/**
 * This class is used to get a uniform error response and must be used in all classes that creates error responses 
 * 
 * @author jens
 *
 */
@XmlRootElement
class ErrorResponse {
	
	public ErrorResponse(Integer errorCode, String name, String description) {
		this.errorCode = errorCode;
		this.name = name;
		this.description = description;
	}
	
	private Integer errorCode;
    private String name;
    private String description;
    
	public Integer getErrorCode() {
		return errorCode;
	}
	
	public void setErrorCode(Integer errorCode) {
		this.errorCode = errorCode;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
    
    
}
