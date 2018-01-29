package rest.resourcess;

import builder.ServiceObjectBuilder;
import models.interfaces.User;
import services.classes.PasswordService;
import services.interfaces.UserService;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.util.List;
import java.util.StringTokenizer;

@Provider
public class Authorisation implements ContainerRequestFilter{
    private UserService gob_userService = ServiceObjectBuilder.getUserServiceObject();

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        String lob_email, lob_password, lob_decodeString, lob_authToken;
        StringTokenizer lob_tokenizer;
        List<String> lob_authHeader;
        byte[] lva_decoded;
        User lob_user;

        if(requestContext.getUriInfo().getPath().contains("auth")){
            lob_authHeader = requestContext.getHeaders().get("Authorization");

            if(lob_authHeader != null && lob_authHeader.size() > 0){
                lob_authToken = lob_authHeader.get(0);
                lob_authToken = lob_authToken.replaceFirst("Basic", "");
                lva_decoded = DatatypeConverter.parseBase64Binary(lob_authToken);
                lob_decodeString = new String(lva_decoded, "UTF-8");
                lob_tokenizer = new StringTokenizer(lob_decodeString, ":");
                lob_email = lob_tokenizer.nextToken();
                lob_password = lob_tokenizer.nextToken();

                lob_user = gob_userService.getUserByEmail(lob_email);

                if(PasswordService.checkPasswordEquals(lob_password,lob_user.getPassword())) {
                    return;
                }
            }

            Response unauthorizedStatus = Response
                    .status(Response.Status.UNAUTHORIZED)
                    .entity("User cant access the resource")
                    .build();
            requestContext.abortWith(unauthorizedStatus);
        }
    }
}
