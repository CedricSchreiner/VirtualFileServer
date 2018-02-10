package rest.resources;

import builder.ServiceObjectBuilder;
import models.classes.SharedDirectory;
import models.classes.User;
import services.interfaces.SharedDirectoryService;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

import static rest.constants.RestConstants.GC_USERS;
import static rest.constants.SharedDirectoryConstants.*;
import static utilities.RestUtils.checkIfUsersNotEqual;
import static utilities.RestUtils.getUserFromContext;

@Path(GC_SHARED_DIRECTORY_BASE_PATH)
@Produces(MediaType.APPLICATION_JSON)
public class SharedDirectoryResource {

    private SharedDirectoryService gob_sharedDirectoryService = ServiceObjectBuilder.getSharedDirectoryServiceObject();

    @POST
    @Path(GC_GET_ALL_SHARED_DIRECTORIES_FROM_USER)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getAllSharedDirectoriesFromUser(@Context ContainerRequestContext iob_requestContext) {
        User lob_user;

        lob_user = getUserFromContext(iob_requestContext);

        List<SharedDirectory> lob_sharedDirectories;
        lob_sharedDirectories = gob_sharedDirectoryService.getSharedDirectoryService(lob_user);

        return Response
                .ok()
                .entity(lob_sharedDirectories)
                .build();
    }

    @POST
    @Path(GC_ADD_NEW_SHARED_DIRECTORY)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addNewSharedDirectory(@Context ContainerRequestContext iob_requestContext,
                                          SharedDirectory iob_sharedDirectory) {

        User lob_user;

        lob_user = getUserFromContext(iob_requestContext);

        if (checkIfUsersNotEqual(lob_user, iob_sharedDirectory.getOwner())) {
            return Response
                    .status(Response.Status.FORBIDDEN)
                    .entity(GC_USERS)
                    .build();
        }

        return null;
    }

    @POST
    @Path(GC_ADD_NEW_MEMBER_TO_SHARED_DIRECTORY)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addNewMemberToSharedDirectory(@Context ContainerRequestContext iob_requestContext,
                                                  SharedDirectory iob_sharedDirectory) {

        User lob_user;

        lob_user = getUserFromContext(iob_requestContext);

        if (checkIfUsersNotEqual(lob_user, iob_sharedDirectory.getOwner())) {
            return Response
                    .status(Response.Status.FORBIDDEN)
                    .entity(GC_USERS)
                    .build();
        }

        return null;
    }

    @POST
    @Path(GC_DELETE_SHARED_DIRECTORY)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response deleteSharedDirectory(@Context ContainerRequestContext iob_requestContext,
                                          SharedDirectory iob_sharedDirectory) {

        User lob_user;

        lob_user = getUserFromContext(iob_requestContext);

        if (checkIfUsersNotEqual(lob_user, iob_sharedDirectory.getOwner())) {
            return Response
                    .status(Response.Status.FORBIDDEN)
                    .entity(GC_USERS)
                    .build();
        }

        return null;
    }

    @POST
    @Path(GC_REMOVE_MEMBER_FROM_SHARED_DIRECTORY)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response removeMemberFromSharedDirectory(@Context ContainerRequestContext iob_requestContext,
                                                    SharedDirectory iob_sharedDirectory) {

        User lob_user;

        lob_user = getUserFromContext(iob_requestContext);

        if (checkIfUsersNotEqual(lob_user, iob_sharedDirectory.getOwner())) {
            return Response
                    .status(Response.Status.FORBIDDEN)
                    .entity(GC_USERS)
                    .build();
        }

        return null;
    }
}
