package rest.resourcess;

import models.interfaces.User;
import services.classes.AuthService;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

@Provider
public class Authorisation implements ContainerRequestFilter {
    @Override
    public void filter(ContainerRequestContext iob_containerRequest) {
        AuthService lob_authService;
        String iva_authCredentials;
        Response unauthorizedStatus;

        if(iob_containerRequest.getUriInfo().getPath().contains("auth")) {
            iva_authCredentials = iob_containerRequest.getHeaderString(HttpHeaders.AUTHORIZATION);
            lob_authService = new AuthService();

            User test = lob_authService.authenticateUser(iva_authCredentials);

            if(test == null) {
                unauthorizedStatus = Response
                        .status(Response.Status.UNAUTHORIZED)
                        .entity("User cant access the resource")
                        .build();
                iob_containerRequest.abortWith(unauthorizedStatus);
            }
        }

        if (iob_containerRequest.getUriInfo().getPath().contains("adminAuth")) {
            iva_authCredentials = iob_containerRequest.getHeaderString(HttpHeaders.AUTHORIZATION);
            lob_authService = new AuthService();

            if(lob_authService.authenticateAdmin(iva_authCredentials) == null) {
                unauthorizedStatus = Response
                        .status(Response.Status.UNAUTHORIZED)
                        .entity("You are not an admin!")
                        .build();
                iob_containerRequest.abortWith(unauthorizedStatus);
            }
        }
    }
}