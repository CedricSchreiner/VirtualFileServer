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
    public void filter(ContainerRequestContext containerRequest) {
        if(containerRequest.getUriInfo().getPath().contains("auth")) {
            String authCredentials = containerRequest.getHeaderString(HttpHeaders.AUTHORIZATION);

            AuthService authsvc = new AuthService();

            boolean authStatus = authsvc.authenticate(authCredentials);

            if (!authStatus) {
                throw new WebApplicationException(Response.Status.UNAUTHORIZED);
            }
        }
    }
}