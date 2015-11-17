package org.hopto.eriksen.resources.util;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**  
 * @deprecated Use {@link CustomNotFoundException} instead. The reason is that HTTP 422 is a WebDAV (RFC 4918) Error code. 
 * 
 * Creates a HTTP 422 (Unprocessable Entity) exception.
 * This shall be used for validation errors, e.g. negative values in query params or missing fields in payload.
 * 
 * @param message
 *            the String that is the entity of the error response.
 * 
 */
@Deprecated
@SuppressWarnings("serial")
public class CustomUnprocessableEntityException  extends WebApplicationException {

	public CustomUnprocessableEntityException(String message) {
		super(
				Response
				.status(new CustomUnprocessableEntityRequestType() )
				.entity(new ErrorResponse(422, "unprocessable_entity", message) )
				.type(MediaType.APPLICATION_JSON)
				.build());
	}
	
	// From: http://stackoverflow.com/questions/21276988/return-custom-error-codes-to-client-in-the-response-for-a-jax-rs
	public static class CustomUnprocessableEntityRequestType implements Response.StatusType {

	    @Override
	    public int getStatusCode() {
	        return 422;
	    }

	    @Override
	    public String getReasonPhrase() {
	        return "Unprocessable Entity";
	    }

	    @Override
	    public Response.Status.Family getFamily() {
	        return Response.Status.Family.CLIENT_ERROR;
	    }
	}
}
