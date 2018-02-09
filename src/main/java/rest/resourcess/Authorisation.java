package rest.resourcess;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

@Provider
public class Authorisation implements ContainerRequestFilter {
    @Override
    public void filter(ContainerRequestContext iob_containerRequest) {
        if(iob_containerRequest.getUriInfo().getPath().contains("auth")) {
            String iva_authCredentials = iob_containerRequest.getHeaderString(HttpHeaders.AUTHORIZATION);

            AuthService lob_authService = new AuthService();

            boolean lva_authStatus = lob_authService.authenticate(iva_authCredentials);

            if (!lva_authStatus) {
                Response unauthorizedStatus = Response
                        .status(Response.Status.UNAUTHORIZED)
                        .entity("User cant access the resource")
                        .build();
                iob_containerRequest.abortWith(unauthorizedStatus);
            }
        }
    }
}