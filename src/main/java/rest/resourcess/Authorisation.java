package rest.resourcess;

import builder.ServiceObjectBuilder;
import models.interfaces.User;
import services.classes.PasswordService;
import services.interfaces.UserService;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.util.List;
import java.util.StringTokenizer;

@Provider
public class Authorisation implements ContainerRequestFilter {
    private UserService gob_userService = ServiceObjectBuilder.getUserServiceObject();

    @Override
    public void filter(ContainerRequestContext iob_containerRequest) {
        if(iob_containerRequest.getUriInfo().getPath().contains("auth")) {
            String iva_authCredentials = iob_containerRequest.getHeaderString(HttpHeaders.AUTHORIZATION);

            AuthService lob_authService = new AuthService();

            boolean lva_authStatus = lob_authService.authenticate(iva_authCredentials);

            if (!lva_authStatus) {
                throw new WebApplicationException(Response.Status.UNAUTHORIZED);
            }
        }
    }
}