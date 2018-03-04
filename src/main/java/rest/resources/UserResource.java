package rest.resources;

import builder.ServiceObjectBuilder;
import cache.Cache;
import com.fasterxml.jackson.databind.ObjectMapper;
import models.classes.User;
import models.exceptions.UserAlreadyExistsException;
import models.exceptions.UserEmptyException;
import models.exceptions.UsersNotEqualException;
import services.interfaces.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.List;

import static rest.constants.RestConstants.GC_USERS;
import static rest.constants.UserResourceConstants.*;
import static utilities.RestUtils.checkIfUsersNotEqual;
import static utilities.RestUtils.getUserFromContext;

@Path(GC_USER_RESOURCE_PATH)
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {

    private UserService gob_userService = ServiceObjectBuilder.getUserServiceObject();

    @PUT
    @Path(GC_USER_LOGIN_PATH)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response login(@Context HttpServletRequest iob_servletRequest,
                          @Context ContainerRequestContext iob_requestContext,
                          User iob_user) {
        User lob_user;
        String lva_jsonString;
        ObjectMapper lob_mapper;
        String lva_ipAddress;
        Cache lob_ipCache = Cache.getIpCache();

        lob_user = getUserFromContext(iob_requestContext);

        if (checkIfUsersNotEqual(lob_user, iob_user)) {
            return Response
                    .status(Response.Status.FORBIDDEN)
                    .entity(GC_USERS)
                    .build();
        }

        try {

            lob_user = gob_userService.getUserByEmail(iob_user.getEmail());
            lob_mapper = new ObjectMapper();
            lva_jsonString = lob_mapper.writeValueAsString(lob_user);
            lva_ipAddress = iob_servletRequest.getRemoteAddr();

            lob_ipCache.put(lob_user.getEmail(), lva_ipAddress);

            return Response.ok()
                    .entity(lva_jsonString)
                    .build();

        } catch (IOException | UsersNotEqualException ex) {
            return Response
                    .status(Response.Status.CONFLICT)
                    .entity(ex.getMessage())
                    .build();

        } catch (UserEmptyException ex) {
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity(ex.getMessage())
                    .build();
        }
    }

    @PUT
    @Path(GC_USER_CHANGE_PASSWORD_PATH)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response changePassword(@Context ContainerRequestContext iob_requestContext, User iob_user) {
        boolean lva_passwordChanged;
        User lob_user;

        lob_user = getUserFromContext(iob_requestContext);

        if (checkIfUsersNotEqual(lob_user, iob_user)) {
            return Response
                    .status(Response.Status.FORBIDDEN)
                    .entity(GC_USERS)
                    .build();
        }

        try {
            lva_passwordChanged = gob_userService.changePassword(iob_user, iob_user.getPassword());

            if (lva_passwordChanged) {
                return Response.ok()
                        .entity(GC_PASSWORD_SUCCESSFULLY_CHANGED)
                        .build();
            } else {
                return Response.status(Response.Status.CONFLICT)
                        .entity(GC_PASSWORD_NOT_CHANGED)
                        .build();
            }
        } catch (UserEmptyException ex) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(ex.getMessage())
                    .build();
        }
    }

    @PUT
    @Path(GC_USER_ADD_NEW_USER_PATH)
    public Response registerNewUser(@Context ContainerRequestContext iob_requestContext, User iob_user) {
        boolean lva_userAdded;

        try {
            lva_userAdded = gob_userService.createNewUserInDatabase(iob_user);

            if (lva_userAdded) {
                return Response.ok()
                        .entity(GC_USER_SUCCESSFULLY_ADDED)
                        .build();
            } else {
                return Response.status(Response.Status.CONFLICT)
                        .entity(GC_USER_NOT_ADDED)
                        .build();
            }
        } catch (UserAlreadyExistsException ex) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(ex.getMessage())
                    .build();
        }
    }

    @GET
    @Path(GC_USER_GET_ALL_USER_PATH)
    public List<User> getAllUser() {
        return gob_userService.getAllUser();
    }

    @GET
    @Path(GC_USER_UNREGISTER_IP)
    public void unregisterIp(@Context ContainerRequestContext iob_requestContext,
                             @Context HttpServletRequest iob_servletRequest) {
        Cache lob_ipCache;
        User lob_user;
        String lva_ipAddress;

        lob_ipCache = Cache.getIpCache();
        lob_user = getUserFromContext(iob_requestContext);
        lva_ipAddress = iob_servletRequest.getRemoteAddr();

        lob_ipCache.removeEntry(lob_user.getEmail(), lva_ipAddress);
    }
}
