package rest;

import services.classes.AuthService;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import static rest.constants.AdminResourceConstants.GC_ADMIN_NOT_AUTHORISED;
import static rest.constants.RestConstants.GC_ADMIN_URI;
import static rest.constants.RestConstants.GC_USER_URI;
import static rest.constants.UserResourceConstants.GC_USER_NOT_AUTHORISED;

@Provider
public class Authorisation implements ContainerRequestFilter {

    @Override
    public void filter(ContainerRequestContext iob_containerRequest) {
        AuthService lob_authService;
        String iva_authCredentials;
        Response lob_unauthorizedStatus;

        if (iob_containerRequest.getUriInfo().getPath().contains(GC_USER_URI)) {
            iva_authCredentials = iob_containerRequest.getHeaderString(HttpHeaders.AUTHORIZATION);
            lob_authService = new AuthService();

            if (lob_authService.authenticateUser(iva_authCredentials) == null) {

                lob_unauthorizedStatus = Response
                        .status(Response.Status.UNAUTHORIZED)
                        .entity(GC_USER_NOT_AUTHORISED)
                        .build();

                iob_containerRequest.abortWith(lob_unauthorizedStatus);
            }
        }

        if (iob_containerRequest.getUriInfo().getPath().contains(GC_ADMIN_URI)) {
            iva_authCredentials = iob_containerRequest.getHeaderString(HttpHeaders.AUTHORIZATION);
            lob_authService = new AuthService();

            if (lob_authService.authenticateAdmin(iva_authCredentials) == null) {

                lob_unauthorizedStatus = Response
                        .status(Response.Status.UNAUTHORIZED)
                        .entity(GC_ADMIN_NOT_AUTHORISED)
                        .build();

                iob_containerRequest.abortWith(lob_unauthorizedStatus);
            }
        }
    }
}