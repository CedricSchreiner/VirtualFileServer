package rest;

import javax.ws.rs.core.Response;

public class UserResource {

    public Response login() {
        return Response.ok().build();
    }

    public Response changePassword() {
        return Response.ok().build();
    }
}
