package org.hopto.eriksen.resources.util;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class CustomConflictException  extends WebApplicationException {
	private static final long serialVersionUID = 1L;
	
	public CustomConflictException(String message) {
		super(
				Response.
					status(Response.Status.CONFLICT ).
					entity(new ErrorResponse(Response.Status.CONFLICT.getStatusCode(), "conflict", message) ).
					type(MediaType.APPLICATION_JSON).
					build());
	}
}
