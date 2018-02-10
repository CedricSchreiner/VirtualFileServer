package utilities;

import models.classes.User;
import services.classes.AuthService;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.HttpHeaders;

public class RestUtils {
    public static User getUserFromContext(ContainerRequestContext iob_requestContext) {
        AuthService lob_authService;
        String iva_authCredentials;

        iva_authCredentials = iob_requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
        lob_authService = new AuthService();

        return lob_authService.authenticateUser(iva_authCredentials);
    }

    public static boolean checkIfUsersNotEqual(User iob_userWhoDoesRequest, User iob_userWhoWantsDoAction) {
        return (!iob_userWhoDoesRequest.getEmail().equals(iob_userWhoWantsDoAction.getEmail()));
    }
}
