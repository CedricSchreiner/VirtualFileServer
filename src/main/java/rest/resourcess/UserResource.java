package rest.resourcess;

import builder.ServiceObjectBuilder;
import models.exceptions.UserAlreadyExistsException;
import models.exceptions.UserEmptyException;
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
    static final String USER_RESOURCE_PATH= "user";

    private UserService gob_userService = ServiceObjectBuilder.getUserServiceObject();

    @GET
    public Response login(User iob_user) {

        gob_userService.getUserByEmail(iob_user.getEmail());

        return Response.accepted()
                .entity("hi")
                .build();
    }

    @PUT
    @Path("/auth/changePassword")
    public Response changePassword(User iob_user, String iob_newPassword) {
        JSONObject lob_returnMessage = new JSONObject();
        boolean lva_passwordChanged;

        try {
            lva_passwordChanged = gob_userService.changePassword(iob_user, iob_newPassword);

            if (lva_passwordChanged) {
                lob_returnMessage.put(PASSWORD_CHANGE_STATUS, PASSWORD_SUCCESSFULLY_CHANGED);
            } else {
                lob_returnMessage.put(PASSWORD_CHANGE_STATUS, PASSWORD_NOT_CHANGED);
            }
        } catch (UserEmptyException ex) {
            lob_returnMessage.put(PASSWORD_CHANGE_STATUS, ex.getMessage());
        }

        return Response.ok()
                .entity(lob_returnMessage.toJSONString())
                .build();
    }

    @PUT
    @Path("addNewUser")
    public Response registerNewUser(User iob_user) {
        JSONObject lob_returnMessage = new JSONObject();
        boolean lva_userAdded;

        try {
            lva_userAdded = gob_userService.createNewUserInDatabase(iob_user);

            if (lva_userAdded) {
                lob_returnMessage.put(USER_ADD_STATUS, USER_SUCCESSFULLY_ADDED);
            } else {
                lob_returnMessage.put(USER_ADD_STATUS, USER_NOT_ADDED);
            }
        } catch (UserAlreadyExistsException ex) {
            lob_returnMessage.put(USER_ADD_STATUS, ex.getMessage());
        }

        return Response.ok()
                .entity(lob_returnMessage)
                .build();
    }
}
