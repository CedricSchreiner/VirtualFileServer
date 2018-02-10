package rest.resourcess;

import builder.ServiceObjectBuilder;
import models.classes.UserImpl;
import models.exceptions.UserEmptyException;
import services.exceptions.AdminAlreadyExistsException;
import services.interfaces.AdminService;

import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static rest.constants.AdminResourceConstants.GC_ADMIN_NOT_ADDED;
import static rest.constants.AdminResourceConstants.GC_ADMIN_SUCCESSFULLY_ADDED;
import static rest.resourcess.AdminResource.ADMIN_PATH;

@Path(ADMIN_PATH)
@Produces(MediaType.APPLICATION_JSON)
public class AdminResource {
    static final String ADMIN_PATH = "/admin";
    private static final String ADD_NEW_ADMIN_PATH = "/adminAuth/addNewAdmin";

    private AdminService adminService = ServiceObjectBuilder.getAdminServiceObject();

    @PUT
    @Path(ADD_NEW_ADMIN_PATH)
    public Response addNewAdmin(UserImpl iob_user) {
        boolean lva_adminAdded;

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
