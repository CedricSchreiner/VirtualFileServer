package rest.resourcess;

import builder.ModelObjectBuilder;
import builder.ServiceObjectBuilder;
import models.interfaces.User;
import services.classes.PasswordService;
import services.interfaces.UserService;

import java.io.IOException;
import java.util.Base64;
import java.util.StringTokenizer;

public class AuthService {
    private UserService userService = ServiceObjectBuilder.getUserServiceObject();

    public boolean authenticate(String authCredentials) {
        User lob_user = ModelObjectBuilder.getUserModel();

        if (authCredentials == null) {
            return false;
        }

        final String encodedUserPassword = authCredentials.replaceFirst("Basic" + " ", "");
        String userNameAndPassword = null;

        try {
            byte[] decodedBytes = Base64.getDecoder().decode(encodedUserPassword);
            userNameAndPassword = new String(decodedBytes, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        final StringTokenizer tokenizer = new StringTokenizer(userNameAndPassword, ":");
        final String username = tokenizer.nextToken();
        final String password = tokenizer.nextToken();

        lob_user = userService.getUserByEmail(username);

        return lob_user.getEmail().equals(username) &&
                PasswordService.checkPasswordEquals(password, lob_user.getPassword());
    }
}
