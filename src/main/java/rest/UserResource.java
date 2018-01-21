package rest;

import dao.classes.DatabaseConnection;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/test")
@Produces(MediaType.TEXT_PLAIN)
public class UserResource {

    @GET
    public Response login() {
        DatabaseConnection connection = DatabaseConnection.getInstance();
        return Response.accepted().entity("hi").build();
    }

    public Response changePassword() {
        return Response.ok().build();
    }

    public Response registerNewUser() {
        return Response.ok().build();
    }
}
