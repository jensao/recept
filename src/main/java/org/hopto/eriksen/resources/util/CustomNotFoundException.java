package org.hopto.eriksen.resources.util;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@SuppressWarnings("serial")
public class CustomNotFoundException extends WebApplicationException {

	/**
	 * Creates a HTTP 404 (Not Found) exception.
	 * 
	 * @param message
	 *            the String that is the entity of the 404 response.
	 */

	public CustomNotFoundException(String message) {		
		super(Response.status(Response.Status.NOT_FOUND).entity(new ErrorResponse(Response.Status.NOT_FOUND.getStatusCode(), "not_found", message) ).type(MediaType.APPLICATION_JSON).build());
	}

}
