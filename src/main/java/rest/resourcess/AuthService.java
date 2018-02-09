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
    private UserService gob_userService = ServiceObjectBuilder.getUserServiceObject();

    public boolean authenticate(String iva_authCredentials) {
        final String lva_encodedUserPassword;
        final StringTokenizer lob_tokenizer;
        final String lva_username;
        final String lva_password;

        String lva_userNameAndPassword = null;
        byte[] lar_decodedBytes;
        User lob_user;

        if (iva_authCredentials == null) {
            return false;
        }

        lva_encodedUserPassword = iva_authCredentials.replaceFirst("Basic" + " ", "");

        try {
            lar_decodedBytes = Base64.getDecoder().decode(lva_encodedUserPassword);
            lva_userNameAndPassword = new String(lar_decodedBytes, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        lob_tokenizer = new StringTokenizer(lva_userNameAndPassword, ":");
        lva_username = lob_tokenizer.nextToken();
        lva_password = lob_tokenizer.nextToken();

        lob_user = gob_userService.getUserByEmail(lva_username);

        return lob_user.getEmail().equals(lva_username) &&
                PasswordService.checkPasswordEquals(lva_password, lob_user.getPassword());
    }
}
