package de.axxepta.exceptions;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class AppException implements ExceptionMapper<ResponseException>{

    @Override
    public Response toResponse(ResponseException exception) {
        return Response.status(exception.getCode()).entity(exception.getMessage()).build();
    }

}
