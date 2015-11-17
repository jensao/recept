package org.hopto.eriksen.resources.util;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


/**
 * This exception shall be used when a request is faulty, it will send the 400 status code back.
 * <br>
 * Description from <a href="https://www.ietf.org/rfc/rfc7231.txt">RFC 7231</a>:
 * 
 * <pre>
 * The 400 (Bad Request) status code indicates that the server cannot or
 * will not process the request due to something that is perceived to be
 * a client error (e.g., malformed request syntax, invalid request
 * message framing, or deceptive request routing).
 * </pre>
 *
 */
public class CustomBadRequestException extends WebApplicationException  {

	private static final long serialVersionUID = 1L;

	public CustomBadRequestException(String message) {
		super(
				Response.
					status(Response.Status.BAD_REQUEST).
					entity(new ErrorResponse(Response.Status.BAD_REQUEST.getStatusCode(), "bad_request", message) ).
					type(MediaType.APPLICATION_JSON).
					build());
	}
}
