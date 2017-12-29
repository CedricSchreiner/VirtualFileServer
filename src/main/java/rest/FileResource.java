package rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/get")
public class FileResource {

    @GET
    public String test() {
        System.out.println("test");
        return "";
    }
}
