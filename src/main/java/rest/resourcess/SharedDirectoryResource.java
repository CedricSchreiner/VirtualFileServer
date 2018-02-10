package rest.resourcess;

import builder.ServiceObjectBuilder;
import models.interfaces.SharedDirectory;
import models.interfaces.User;
import services.interfaces.SharedDirectoryService;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static rest.resourcess.SharedDirectoryResource.GC_SHARED_DIRECTORY_BASE_PATH;

@Path(GC_SHARED_DIRECTORY_BASE_PATH)
@Produces(MediaType.APPLICATION_JSON)
public class SharedDirectoryResource {
    static final String GC_SHARED_DIRECTORY_BASE_PATH = "sharedDirectory/";
    private static final String GC_GET_ALL_SHARED_DIRECTORIES_FROM_USER = "getAllSharedDirectoriesFromUser/";
    private static final String GC_ADD_NEW_SHARED_DIRECTORY = "addNewSharedDirectory";
    private static final String GC_ADD_NEW_MEMBER_TO_SHARED_DIRECTORY = "addNewMemberToSharedDirectory";
    private static final String GC_DELETE_SHARED_DIRECTORY = "deleteSharedDirectory";
    private static final String GC_REMOVE_MEMBER_FROM_SHARED_DIRECTORY = "removeMemberFromSharedDirectory";

    private SharedDirectoryService gob_sharedDirectoryService = ServiceObjectBuilder.getSharedDirectoryServiceObject();

    @Path(GC_GET_ALL_SHARED_DIRECTORIES_FROM_USER)
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getAllSharedDirectoriesFromUser(User iob_user) {
        return null;
    }

    @Path(GC_ADD_NEW_SHARED_DIRECTORY)
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addNewSharedDirectory(SharedDirectory iob_sharedDirectory) {
        return null;
    }

    @Path(GC_ADD_NEW_MEMBER_TO_SHARED_DIRECTORY)
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addNewMemberToSharedDirectory(SharedDirectory iob_sharedDirectory) {
        return null;
    }

    @Path(GC_DELETE_SHARED_DIRECTORY)
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response deleteSharedDirectory(SharedDirectory iob_sharedDirectory) {
        return null;
    }

    @Path(GC_REMOVE_MEMBER_FROM_SHARED_DIRECTORY)
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response removeMemberFromSharedDirectory(SharedDirectory iob_sharedDirectory, User iob_user) {
        return null;
    }
}
