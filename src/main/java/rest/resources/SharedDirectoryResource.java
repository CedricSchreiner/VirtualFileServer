package rest.resources;

import models.classes.SharedDirectory;
import models.classes.User;
import models.exceptions.SharedDirectoryException;
import services.interfaces.SharedDirectoryService;

import javax.ws.rs.*;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static builder.ServiceObjectBuilder.getSharedDirectoryServiceObject;
import static rest.constants.RestConstants.GC_USERS;
import static rest.constants.SharedDirectoryConstants.*;
import static utilities.RestUtils.checkIfUsersNotEqual;
import static utilities.RestUtils.getUserFromContext;

@Path(GC_SHARED_DIRECTORY_BASE_PATH)
@Produces(MediaType.APPLICATION_JSON)
public class SharedDirectoryResource {

    private SharedDirectoryService gob_sharedDirectoryService = getSharedDirectoryServiceObject();

    /**
     * Add a new shared directory and al its member
     *
     * @param iob_requestContext  header with the user who requested this resource
     * @param iob_sharedDirectory the new shared directory
     * @return Response with the status message
     */
    @POST
    @Path(GC_ADD_NEW_SHARED_DIRECTORY)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addNewSharedDirectory(@Context ContainerRequestContext iob_requestContext,
                                          SharedDirectory iob_sharedDirectory) {

        User lob_user;

        // Get the user who requested this resource
        // Check if the user who requested and who wants to create the shared directory are the same
        lob_user = getUserFromContext(iob_requestContext);
        if (checkIfUsersNotEqual(lob_user, iob_sharedDirectory.getOwner())) {
            return Response
                    .status(Response.Status.FORBIDDEN)
                    .entity(GC_USERS)
                    .build();
        }

        try {

            // Add the new shared directory
            // If successfully return a positive response
            if (gob_sharedDirectoryService.addNewSharedDirectory(iob_sharedDirectory)) {

                return Response
                        .ok()
                        .entity(GC_S_DIR_SUCCESSFULLY_ADDED)
                        .build();
            }

            // Something went wrong and DB threw a exception
            // return a negative response
            return Response
                    .status(Response.Status.CONFLICT)
                    .entity(GC_S_DIR_COULD_NOT_BE_ADDED)
                    .build();

        } catch (SharedDirectoryException ex) {

            // The request didn't contained all required information
            // return a negative response
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity(ex.getMessage())
                    .build();
        }
    }

    /**
     * Add a member to the shared directory
     *
     * @param sharedDirectoryId  the shared directory id
     * @param iob_requestContext header with the user who requested this resource
     * @param iob_user           the new member
     * @return Response with the status message
     */
    @PUT
    @Path(GC_ADD_NEW_MEMBER_TO_SHARED_DIRECTORY + GC_REMOVE_MEMBER_PARAM)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addNewMemberToSharedDirectory(@PathParam(GC_REMOVE_MEMBER) int sharedDirectoryId,
                                                  @Context ContainerRequestContext iob_requestContext,
                                                  User iob_user) {

        User lob_user;
        SharedDirectory lob_sharedDirectory;

        // Get the shared directory for the passed id
        lob_sharedDirectory = gob_sharedDirectoryService.getSharedDirectoryById(sharedDirectoryId);

        // Get the user who requested this resource
        // Check if the user who requested and who wants to create the shared directory are the same
        lob_user = getUserFromContext(iob_requestContext);
        if (checkIfUsersNotEqual(lob_user, lob_sharedDirectory.getOwner())) {

            return Response
                    .status(Response.Status.FORBIDDEN)
                    .entity(GC_USERS)
                    .build();
        }

        try {

            // Add the new member to the shared directory
            // If successfully return a positive response
            if (gob_sharedDirectoryService.addNewMemberToSharedDirectory(lob_sharedDirectory, iob_user)) {

                return Response
                        .ok()
                        .entity(GC_S_DIR_MEMBER_SUCCESSFULLY_ADDED)
                        .build();
            }

            // Something went wrong and DB threw a exception
            // return a negative response
            return Response
                    .status(Response.Status.CONFLICT)
                    .entity(GC_S_DIR_MEMBER_NOT_ADDED)
                    .build();

        } catch (SharedDirectoryException ex) {

            // The request didn't contained all required information
            // return a negative response
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity(ex.getMessage())
                    .build();
        }
    }

    /**
     * Delete a shared directory
     *
     * @param iob_requestContext  header with the user who requested this resource
     * @param iob_sharedDirectory the shared directory to delete
     * @return Response with the status message
     */
    @POST
    @Path(GC_DELETE_SHARED_DIRECTORY)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response deleteSharedDirectory(@Context ContainerRequestContext iob_requestContext,
                                          SharedDirectory iob_sharedDirectory) {

        User lob_user;

        // Get the user who requested this resource
        // Check if the user who requested and who wants to create the shared directory are the same
        lob_user = getUserFromContext(iob_requestContext);
        if (checkIfUsersNotEqual(lob_user, iob_sharedDirectory.getOwner())) {

            return Response
                    .status(Response.Status.FORBIDDEN)
                    .entity(GC_USERS)
                    .build();
        }

        try {

            // Delete the shared directory
            // If successfully return a positive response
            if (gob_sharedDirectoryService.deleteSharedDirectory(iob_sharedDirectory)) {

                return Response
                        .ok()
                        .entity(GC_S_DIR_SUCCESSFULLY_DELETED)
                        .build();
            }

            // Something went wrong and DB threw a exception
            // return a negative response
            return Response
                    .status(Response.Status.CONFLICT)
                    .entity(GC_S_DIR_NOT_DELETED)
                    .build();

        } catch (SharedDirectoryException ex) {

            // The request didn't contained all required information
            // return a negative response
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity(ex.getMessage())
                    .build();
        }
    }

    /**
     * Remove a member from shared directory
     *
     * @param sharedDirectoryId  the shared directory id
     * @param iob_requestContext header with the user who requested this resource
     * @param iob_user           the member who gets removed
     * @return Response with the status message
     */
    @PUT
    @Path(GC_REMOVE_MEMBER_FROM_SHARED_DIRECTORY + GC_REMOVE_MEMBER_PARAM)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response removeMemberFromSharedDirectory(@PathParam(GC_REMOVE_MEMBER) int sharedDirectoryId,
                                                    @Context ContainerRequestContext iob_requestContext,
                                                    User iob_user) {

        User lob_user;
        SharedDirectory lob_sharedDirectory;

        // Get the shared directory for the passed id
        lob_sharedDirectory = gob_sharedDirectoryService.getSharedDirectoryById(sharedDirectoryId);

        // Get the user who requested this resource
        // Check if the user who requested and who wants to create the shared directory are the same
        lob_user = getUserFromContext(iob_requestContext);
        if (checkIfUsersNotEqual(lob_user, lob_sharedDirectory.getOwner())) {
            return Response
                    .status(Response.Status.FORBIDDEN)
                    .entity(GC_USERS)
                    .build();
        }

        try {

            // Remove the member from shared directory
            // If successfully return a positive response
            if (gob_sharedDirectoryService.removeMemberFromSharedDirectory(lob_sharedDirectory, iob_user)) {

                return Response
                        .ok()
                        .entity(GC_S_DIR_MEMBER_REMOVED)
                        .build();
            }

            // Something went wrong and DB threw a exception
            // return a negative response
            return Response
                    .status(Response.Status.CONFLICT)
                    .entity(GC_S_DIR_MEMBER_NOT_REMOVED)
                    .build();

        } catch (SharedDirectoryException ex) {

            // The request didn't contained all required information
            // return a negative response
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity(ex.getMessage())
                    .build();
        }
    }
}