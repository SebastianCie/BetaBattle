package de.heim.apps.resource;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.util.Map;

/**
 * WebApplicationException(message, status) trägt die Message nur in der Exception selbst,
 * nicht im HTTP-Body. Ohne diesen Mapper kommen alle so geworfenen Fehler (z.B. in AuthService)
 * als leerer Response-Body beim Client an.
 */
@Provider
public class WebApplicationExceptionMapper implements ExceptionMapper<WebApplicationException> {

    @Override
    public Response toResponse(WebApplicationException exception) {
        Response original = exception.getResponse();
        if (original.hasEntity()) {
            return original;
        }
        return Response.fromResponse(original)
                .entity(Map.of("message", exception.getMessage()))
                .type("application/json")
                .build();
    }
}
