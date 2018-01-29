package rest.resourcess;

import builder.ServiceObjectBuilder;
import models.classes.UserImpl;
import models.exceptions.UserEmptyException;
import org.json.simple.JSONObject;
import services.exceptions.AdminAlreadyExistsException;
import services.interfaces.AdminService;

import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import static rest.constants.AdminResourceConstants.GC_ADD_ADMIN_STATUS;
import static rest.constants.AdminResourceConstants.GC_ADMIN_NOT_ADDED;
import static rest.constants.AdminResourceConstants.GC_ADMIN_SUCCESSFULLY_ADDED;
import static rest.resourcess.AdminResource.ADMIN_PATH;

@Path(ADMIN_PATH)
public class AdminResource {
    static final String ADMIN_PATH = "/admin";
    private static final String ADD_NEW_ADMIN_PATH = "/addNewAdmin";

    private AdminService adminService = ServiceObjectBuilder.getAdminServiceObject();

    @PUT
    @Path(ADD_NEW_ADMIN_PATH)
    public Response addNewAdmin(UserImpl iob_user) {
        JSONObject lob_returnMessage = new JSONObject();
        boolean lva_adminAdded;

        try {
            lva_adminAdded = adminService.addNewAdmin(iob_user);

            if (!lva_adminAdded) {
                lob_returnMessage.put(GC_ADD_ADMIN_STATUS, GC_ADMIN_NOT_ADDED);

                return Response.status(Response.Status.CONFLICT)
                        .entity(lob_returnMessage.toJSONString())
                        .build();
            }

            lob_returnMessage.put(GC_ADD_ADMIN_STATUS, GC_ADMIN_SUCCESSFULLY_ADDED);
            return Response.ok()
                    .entity(lob_returnMessage.toJSONString())
                    .build();

        } catch (UserEmptyException ex) {
            lob_returnMessage.put(GC_ADD_ADMIN_STATUS, ex.getMessage());
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(lob_returnMessage.toJSONString())
                    .build();
        } catch (AdminAlreadyExistsException ex) {
            lob_returnMessage.put(GC_ADD_ADMIN_STATUS, ex.getMessage());

            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(lob_returnMessage.toJSONString())
                    .build();
        }
    }
}
