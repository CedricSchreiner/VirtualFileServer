package rest.resources;

import builder.ServiceObjectBuilder;
import models.classes.User;
import models.exceptions.UserEmptyException;
import services.exceptions.AdminAlreadyExistsException;
import services.interfaces.AdminService;

import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static rest.constants.AdminResourceConstants.*;
import static rest.constants.RestConstants.GC_USERS;
import static utilities.RestUtils.checkIfUsersNotEqual;
import static utilities.RestUtils.getUserFromContext;

@Path(GC_ADMIN_PATH)
@Produces(MediaType.APPLICATION_JSON)
public class AdminResource {

    private AdminService adminService = ServiceObjectBuilder.getAdminServiceObject();

    @PUT
    @Path(GC_ADD_NEW_ADMIN_PATH)
    public Response addNewAdmin(@Context ContainerRequestContext iob_requestContext, User iob_user) {
        boolean lva_adminAdded;
        User lob_user;

        lob_user = getUserFromContext(iob_requestContext);

        if (checkIfUsersNotEqual(lob_user, iob_user)) {
            return Response
                    .status(Response.Status.FORBIDDEN)
                    .entity(GC_USERS)
                    .build();
        }

        try {
            lva_adminAdded = adminService.addNewAdmin(iob_user);

            if (!lva_adminAdded) {
                return Response.status(Response.Status.CONFLICT)
                        .entity(GC_ADMIN_NOT_ADDED)
                        .build();
            }

            return Response.ok()
                    .entity(GC_ADMIN_SUCCESSFULLY_ADDED)
                    .build();

        } catch (UserEmptyException | AdminAlreadyExistsException ex) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(ex.getMessage())
                    .build();
        }
    }
}
