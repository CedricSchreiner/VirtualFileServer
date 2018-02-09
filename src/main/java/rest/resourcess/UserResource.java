package rest.resourcess;

import builder.ServiceObjectBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import models.classes.UserImpl;
import models.exceptions.UserAlreadyExistsException;
import models.exceptions.UserEmptyException;
import models.exceptions.UsersNotEqualException;
import models.interfaces.User;
import org.json.simple.JSONObject;
import services.interfaces.UserService;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.List;

import static rest.constants.UserResourceConstants.*;
import static rest.resourcess.UserResource.USER_RESOURCE_PATH;

@Path(USER_RESOURCE_PATH)
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {
    static final String USER_RESOURCE_PATH = "user/";
    private static final String USER_LOGIN_PATH = "/auth/login/";
    private static final String USER_CHANGE_PASSWORD_PATH = "/auth/changePassword/";
    private static final String USER_ADD_NEW_USER_PATH = "addNewUser/";
    private static final String USER_GET_ALL_USER_PATH = "/adminAuth/getAllUser";

    private UserService gob_userService = ServiceObjectBuilder.getUserServiceObject();

    @PUT
    @Path(USER_LOGIN_PATH)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response login(UserImpl user) {
        JSONObject lob_returnMessage = new JSONObject();

        try {
            User aUser = gob_userService.getUserByEmail(user.getEmail());
            String lva_jsonString = "";
            ObjectMapper lob_mapper = new ObjectMapper();

            try {
                lva_jsonString = lob_mapper.writeValueAsString(aUser);
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            return Response.ok()
                    .entity(lva_jsonString)
                    .build();

        } catch (UsersNotEqualException ex) {
            lob_returnMessage.put(GC_USER_LOGIN_STATUS, ex.getMessage());

            return Response.status(Response.Status.CONFLICT)
                    .entity(lob_returnMessage.toJSONString())
                    .build();
        } catch (UserEmptyException ex) {
            lob_returnMessage.put(GC_USER_LOGIN_STATUS, ex.getMessage());

            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(lob_returnMessage.toJSONString())
                    .build();
        }
    }

    @PUT
    @Path(USER_CHANGE_PASSWORD_PATH)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response changePassword(UserImpl iob_user) {
        JSONObject lob_returnMessage = new JSONObject();
        boolean lva_passwordChanged;

        try {
            lva_passwordChanged = gob_userService.changePassword(iob_user, iob_user.getPassword());

            if (lva_passwordChanged) {
                lob_returnMessage.put(GC_PASSWORD_CHANGE_STATUS, GC_PASSWORD_SUCCESSFULLY_CHANGED);

                return Response.ok()
                        .entity(lob_returnMessage.toJSONString())
                        .build();
            } else {
                lob_returnMessage.put(GC_PASSWORD_CHANGE_STATUS, GC_PASSWORD_NOT_CHANGED);

                return Response.status(Response.Status.CONFLICT)
                        .entity(lob_returnMessage.toJSONString())
                        .build();
            }
        } catch (UserEmptyException ex) {
            lob_returnMessage.put(GC_PASSWORD_CHANGE_STATUS, ex.getMessage());

            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(lob_returnMessage.toJSONString())
                    .build();
        }
    }

    @PUT
    @Path(USER_ADD_NEW_USER_PATH)
    public Response registerNewUser(UserImpl iob_user) {
        JSONObject lob_returnMessage = new JSONObject();
        boolean lva_userAdded;

        try {
            lva_userAdded = gob_userService.createNewUserInDatabase(iob_user);

            if (lva_userAdded) {
                lob_returnMessage.put(GC_USER_ADD_STATUS, GC_USER_SUCCESSFULLY_ADDED);

                return Response.ok()
                        .entity(lob_returnMessage.toJSONString())
                        .build();
            } else {
                lob_returnMessage.put(GC_USER_ADD_STATUS, GC_USER_NOT_ADDED);

                return Response.status(Response.Status.CONFLICT)
                        .entity(lob_returnMessage.toJSONString())
                        .build();
            }
        } catch (UserAlreadyExistsException ex) {
            lob_returnMessage.put(GC_USER_ADD_STATUS, ex.getMessage());

            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(lob_returnMessage.toJSONString())
                    .build();
        }
    }

    @GET
    @Path(USER_GET_ALL_USER_PATH)
    public List<User> getAllUser() {
        return gob_userService.getAllUser();
    }
}
