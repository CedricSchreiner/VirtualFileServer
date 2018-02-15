package rest.resources;

import builder.ServiceObjectBuilder;
import models.classes.SharedDirectory;
import models.classes.User;
import models.exceptions.SharedDirectoryException;
import services.interfaces.SharedDirectoryService;

import javax.ws.rs.*;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static rest.constants.RestConstants.GC_USERS;
import static rest.constants.SharedDirectoryConstants.*;
import static utilities.RestUtils.checkIfUsersNotEqual;
import static utilities.RestUtils.getUserFromContext;

@Path(GC_SHARED_DIRECTORY_BASE_PATH)
@Produces(MediaType.APPLICATION_JSON)
public class SharedDirectoryResource {
    private SharedDirectoryService gob_sharedDirectoryService = ServiceObjectBuilder.getSharedDirectoryServiceObject();

    @POST
    @Path(GC_ADD_NEW_SHARED_DIRECTORY)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addNewSharedDirectory(@Context ContainerRequestContext iob_requestContext,
                                          SharedDirectory iob_sharedDirectory) {

        User lob_user;
        boolean lva_hasAdded;

        lob_user = getUserFromContext(iob_requestContext);

        if (checkIfUsersNotEqual(lob_user, iob_sharedDirectory.getOwner())) {
            return Response
                    .status(Response.Status.FORBIDDEN)
                    .entity(GC_USERS)
                    .build();
        }

        try {
            lva_hasAdded = gob_sharedDirectoryService.addNewSharedDirectory(iob_sharedDirectory);

            if (lva_hasAdded) {
                return Response
                        .ok()
                        .entity(GC_S_DIR_SUCCESSFULLY_ADDED)
                        .build();
            }

            return Response
                    .status(Response.Status.CONFLICT)
                    .entity(GC_S_DIR_COULD_NOT_BE_ADDED)
                    .build();

        } catch (SharedDirectoryException ex) {
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity(ex.getMessage())
                    .build();
        }
    }

    @PUT
    @Path(GC_ADD_NEW_MEMBER_TO_SHARED_DIRECTORY + GC_REMOVE_MEMBER_PARAM)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addNewMemberToSharedDirectory(@PathParam(GC_REMOVE_MEMBER) int sharedDirectoryId,
                                                  @Context ContainerRequestContext iob_requestContext,
                                                  User iob_user) {

        User lob_user;
        SharedDirectory lob_sharedDirectory;

        lob_sharedDirectory = gob_sharedDirectoryService.getSharedDirectoryById(sharedDirectoryId);
        lob_user = getUserFromContext(iob_requestContext);

        if (checkIfUsersNotEqual(lob_user, lob_sharedDirectory.getOwner())) {
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
        boolean lva_hasDeleted;

        lob_user = getUserFromContext(iob_requestContext);

        if (checkIfUsersNotEqual(lob_user, iob_sharedDirectory.getOwner())) {
            return Response
                    .status(Response.Status.FORBIDDEN)
                    .entity(GC_USERS)
                    .build();
        }

        try {
            lva_hasDeleted = gob_sharedDirectoryService.deleteSharedDirectory(iob_sharedDirectory);

            if (lva_hasDeleted) {
                return Response
                        .ok()
                        .entity(GC_S_DIR_SUCCESSFULLY_DELETED)
                        .build();
            }

            return Response
                    .status(Response.Status.CONFLICT)
                    .entity(GC_S_DIR_NOT_DELETED)
                    .build();

        } catch (SharedDirectoryException ex) {

            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity(ex.getMessage())
                    .build();
        }
    }

    @PUT
    @Path(GC_REMOVE_MEMBER_FROM_SHARED_DIRECTORY + GC_REMOVE_MEMBER_PARAM)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response removeMemberFromSharedDirectory(@PathParam(GC_REMOVE_MEMBER) int sharedDirectoryId,
                                                    @Context ContainerRequestContext iob_requestContext,
                                                    User iob_user) {

        User lob_user;
        boolean lva_hasRemoved;
        SharedDirectory lob_sharedDirectory;

        lob_sharedDirectory = gob_sharedDirectoryService.getSharedDirectoryById(sharedDirectoryId);
        lob_user = getUserFromContext(iob_requestContext);

        if (checkIfUsersNotEqual(lob_user, lob_sharedDirectory.getOwner())) {
            return Response
                    .status(Response.Status.FORBIDDEN)
                    .entity(GC_USERS)
                    .build();
        }

        try {
            lva_hasRemoved = gob_sharedDirectoryService.removeMemberFromSharedDirectory(lob_sharedDirectory, iob_user);

            if (lva_hasRemoved) {
                return Response
                        .ok()
                        .entity(GC_S_DIR_MEMBER_REMOVED)
                        .build();
            }

            return Response
                    .status(Response.Status.CONFLICT)
                    .entity(GC_S_DIR_MEMBER_NOT_REMOVED)
                    .build();
        } catch (SharedDirectoryException ex) {
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity(ex.getMessage())
                    .build();
        }
    }
}
