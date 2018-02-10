package rest.resourcess;

import models.interfaces.User;
import services.classes.AuthService;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import static rest.constants.AdminResourceConstants.GC_ADMIN_NOT_AUTHORISED;
import static rest.constants.UserResourceConstants.GC_USER_NOT_AUTHORISED;

@Provider
public class Authorisation implements ContainerRequestFilter {
    private static final String GC_USER_URI = "auth";
    private static final String GC_ADMIN_URI = "adminAuth";

    @Override
    public void filter(ContainerRequestContext iob_containerRequest) {
        AuthService lob_authService;
        String iva_authCredentials;
        Response unauthorizedStatus;

        if(iob_containerRequest.getUriInfo().getPath().contains(GC_USER_URI)) {
            iva_authCredentials = iob_containerRequest.getHeaderString(HttpHeaders.AUTHORIZATION);
            lob_authService = new AuthService();

            User test = lob_authService.authenticateUser(iva_authCredentials);

            if(test == null) {
                unauthorizedStatus = Response
                        .status(Response.Status.UNAUTHORIZED)
                        .entity(GC_USER_NOT_AUTHORISED)
                        .build();
                iob_containerRequest.abortWith(unauthorizedStatus);
            }
        }

        if (iob_containerRequest.getUriInfo().getPath().contains(GC_ADMIN_URI)) {
            iva_authCredentials = iob_containerRequest.getHeaderString(HttpHeaders.AUTHORIZATION);
            lob_authService = new AuthService();

            if(lob_authService.authenticateAdmin(iva_authCredentials) == null) {
                unauthorizedStatus = Response
                        .status(Response.Status.UNAUTHORIZED)
                        .entity(GC_ADMIN_NOT_AUTHORISED)
                        .build();
                iob_containerRequest.abortWith(unauthorizedStatus);
            }
        }
    }
}