package rest;

import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;

@Path("/get")
public class FileResource {

    @GET
    public String getFile() {
        System.out.println("test");
        return "";
    }

    @PUT
    public void addFile() {

    }

    @GET
    public String getFiles() {
        return "";
    }

    @PUT
    public void addFiles() {

    }
}
