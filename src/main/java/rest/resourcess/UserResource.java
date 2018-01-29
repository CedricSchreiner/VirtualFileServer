package rest.resourcess;

import builder.ServiceObjectBuilder;
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

import static rest.constants.UserResourceConstants.*;
import static rest.resourcess.UserResource.USER_RESOURCE_PATH;

@Path(USER_RESOURCE_PATH)
@Produces(MediaType.TEXT_PLAIN)
public class UserResource {
    static final String USER_RESOURCE_PATH = "user/";
    private static final String USER_LOGIN_PATH = "/auth/login/";
    private static final String USER_CHANGE_PASSWORD_PATH = "/auth/changePassword/";
    private static final String USER_ADD_NEW_USER_PATH = "addNewUser/";

    private UserService gob_userService = ServiceObjectBuilder.getUserServiceObject();

    @PUT
    @Path(USER_LOGIN_PATH)
    public Response login(UserImpl user) {
        JSONObject lob_returnMessage = new JSONObject();

        try{
            User aUser = gob_userService.getUserByEmail(user.getEmail());

            lob_returnMessage.put(EMAIL, aUser.getEmail());
            lob_returnMessage.put(PASSWORD, aUser.getPassword());
            lob_returnMessage.put(NAME, aUser.getName());
            lob_returnMessage.put(IS_ADMIN, aUser.getIsAdmin());
            lob_returnMessage.put(ADMIN_ID, aUser.getAdminId());
            lob_returnMessage.put(USER_ID, aUser.getUserId());

            return Response.ok()
                    .entity(lob_returnMessage.toJSONString())
                    .build();
        } catch (UsersNotEqualException ex) {
            lob_returnMessage.put(USER_LOGIN_STATUS, ex.getMessage());

            return Response.status(Response.Status.CONFLICT)
                    .entity(lob_returnMessage.toJSONString())
                    .build();
        } catch (UserEmptyException ex) {
            lob_returnMessage.put(USER_LOGIN_STATUS, ex.getMessage());

            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(lob_returnMessage.toJSONString())
                    .build();
        }
    }

    @PUT
    @Path(USER_CHANGE_PASSWORD_PATH)
    public Response changePassword(UserImpl iob_user) {
        JSONObject lob_returnMessage = new JSONObject();
        boolean lva_passwordChanged;

        try {
            lva_passwordChanged = gob_userService.changePassword(iob_user, "test");

            if (lva_passwordChanged) {
                lob_returnMessage.put(PASSWORD_CHANGE_STATUS, PASSWORD_SUCCESSFULLY_CHANGED);

                return Response.ok()
                        .entity(lob_returnMessage.toJSONString())
                        .build();
            } else {
                lob_returnMessage.put(PASSWORD_CHANGE_STATUS, PASSWORD_NOT_CHANGED);

                return Response.status(Response.Status.CONFLICT)
                        .entity(lob_returnMessage.toJSONString())
                        .build();
            }
        } catch (UserEmptyException ex) {
            lob_returnMessage.put(PASSWORD_CHANGE_STATUS, ex.getMessage());

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
                lob_returnMessage.put(USER_ADD_STATUS, USER_SUCCESSFULLY_ADDED);

                return Response.ok()
                        .entity(lob_returnMessage.toJSONString())
                        .build();
            } else {
                lob_returnMessage.put(USER_ADD_STATUS, USER_NOT_ADDED);

                return Response.status(Response.Status.CONFLICT)
                        .entity(lob_returnMessage.toJSONString())
                        .build();
            }
        } catch (UserAlreadyExistsException ex) {
            lob_returnMessage.put(USER_ADD_STATUS, ex.getMessage());

            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(lob_returnMessage.toJSONString())
                    .build();
        }
    }
}
