package services.classes;

import utilities.Utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static services.constants.PasswordServiceConstants.*;

public class PasswordService {
    /**
     * Encrypts a string with the SHA-1 Algorithm
     *
     * @param iob_password password string
     * @return Password as byte Array
     */
    private static byte[] encryptPasswordSHA1(String iob_password) {
        MessageDigest lob_md;

        try {
            lob_md = MessageDigest.getInstance(ALGORITHM);
            lob_md.update(iob_password.getBytes());
            return lob_md.digest();
        } catch (NoSuchAlgorithmException ex) {
            System.err.println(ERR_NO_ALGORITHM_IMPLEMENTATION);
            ex.printStackTrace();
        }

        return null;
    }

    /**
     * Encrypts a string
     *
     * @param iob_password password string
     * @return encrypted string
     */
    public static String encryptPassword(String iob_password) {
        StringBuilder lob_sb = new StringBuilder();

        Utils.check(!iob_password.isEmpty(), ERR_MSG_EMPTY_PASSWORD);
        byte[] lar_mdBytes = encryptPasswordSHA1(iob_password);
        Utils.check(lar_mdBytes != null, ERR_MSG_PASSWORD_ENCRYPTION_FAILED);

        for (byte lva_byte : lar_mdBytes) {
            lob_sb.append(Integer.toString((lva_byte & 0xff) + 0x100, 16).substring(1));
        }

        return lob_sb.toString();
    }

    /**
     * Compares an encrypted and a raw password
     *
     * @param iob_rawPassword raw password
     * @param iob_dbPassword  encrypted password from db
     * @return true if equals otherwise false
     */
    public static boolean checkPasswordEquals(String iob_rawPassword, String iob_dbPassword) {
        Utils.check(!iob_rawPassword.isEmpty() && !iob_dbPassword.isEmpty(), ERR_MSG_EMPTY_PASSWORD);

        String lob_convertedPassword = encryptPassword(iob_rawPassword);

        return lob_convertedPassword.equals(iob_dbPassword);
    }
}
