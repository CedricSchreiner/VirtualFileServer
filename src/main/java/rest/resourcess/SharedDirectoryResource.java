package rest.resourcess;

import builder.ServiceObjectBuilder;
import models.interfaces.SharedDirectory;
import models.interfaces.User;
import services.classes.AuthService;
import services.interfaces.SharedDirectoryService;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.util.List;

import static rest.constants.RestConstants.GC_USERS;
import static rest.resourcess.SharedDirectoryResource.GC_SHARED_DIRECTORY_BASE_PATH;

@Path(GC_SHARED_DIRECTORY_BASE_PATH)
@Produces(MediaType.APPLICATION_JSON)
public class SharedDirectoryResource {
    static final String GC_SHARED_DIRECTORY_BASE_PATH = "sharedDirectory/auth";
    private static final String GC_GET_ALL_SHARED_DIRECTORIES_FROM_USER = "getAllSharedDirectoriesFromUser/";
    private static final String GC_ADD_NEW_SHARED_DIRECTORY = "addNewSharedDirectory";
    private static final String GC_ADD_NEW_MEMBER_TO_SHARED_DIRECTORY = "addNewMemberToSharedDirectory";
    private static final String GC_DELETE_SHARED_DIRECTORY = "deleteSharedDirectory";
    private static final String GC_REMOVE_MEMBER_FROM_SHARED_DIRECTORY = "removeMemberFromSharedDirectory";

    private SharedDirectoryService gob_sharedDirectoryService = ServiceObjectBuilder.getSharedDirectoryServiceObject();

    @Path(GC_GET_ALL_SHARED_DIRECTORIES_FROM_USER)
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getAllSharedDirectoriesFromUser(@Context ContainerRequestContext iob_requestContext) {
        User lob_user = getUserFromContext(iob_requestContext);

        List<SharedDirectory> lob_sharedDirectories;
        lob_sharedDirectories = gob_sharedDirectoryService.getSharedDirectoryService(lob_user);

        return Response
                .ok()
                .entity(lob_sharedDirectories)
                .build();
    }

    @Path(GC_ADD_NEW_SHARED_DIRECTORY)
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addNewSharedDirectory(@Context ContainerRequestContext iob_requestContext,
                                          SharedDirectory iob_sharedDirectory) {

        User lob_user = getUserFromContext(iob_requestContext);

        if (checkIfUsersNotEqual(lob_user, iob_sharedDirectory.getOwner())) {
            return Response
                    .status(Response.Status.FORBIDDEN)
                    .entity(GC_USERS)
                    .build();
        }

        return null;
    }

    @Path(GC_ADD_NEW_MEMBER_TO_SHARED_DIRECTORY)
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addNewMemberToSharedDirectory(@Context ContainerRequestContext iob_requestContext,
                                                  SharedDirectory iob_sharedDirectory) {

        User lob_user = getUserFromContext(iob_requestContext);

        if (checkIfUsersNotEqual(lob_user, iob_sharedDirectory.getOwner())) {
            return Response
                    .status(Response.Status.FORBIDDEN)
                    .entity(GC_USERS)
                    .build();
        }

        return null;
    }

    @Path(GC_DELETE_SHARED_DIRECTORY)
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response deleteSharedDirectory(@Context ContainerRequestContext iob_requestContext,
                                          SharedDirectory iob_sharedDirectory) {

        User lob_user = getUserFromContext(iob_requestContext);

        if (checkIfUsersNotEqual(lob_user, iob_sharedDirectory.getOwner())) {
            return Response
                    .status(Response.Status.FORBIDDEN)
                    .entity(GC_USERS)
                    .build();
        }

        return null;
    }

    @Path(GC_REMOVE_MEMBER_FROM_SHARED_DIRECTORY)
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response removeMemberFromSharedDirectory(@Context ContainerRequestContext iob_requestContext,
                                                    SharedDirectory iob_sharedDirectory) {

        User lob_user = getUserFromContext(iob_requestContext);

        if (checkIfUsersNotEqual(lob_user, iob_sharedDirectory.getOwner())) {
            return Response
                    .status(Response.Status.FORBIDDEN)
                    .entity(GC_USERS)
                    .build();
        }

        return null;
    }

    private User getUserFromContext(ContainerRequestContext iob_requestContext) {
        AuthService lob_authService;
        String iva_authCredentials;

        iva_authCredentials = iob_requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
        lob_authService = new AuthService();

        return lob_authService.authenticateUser(iva_authCredentials);
    }

    private boolean checkIfUsersNotEqual(User iob_userWhoDoesRequest, User iob_userWhoWantsDoAction) {
        return (!iob_userWhoDoesRequest.getEmail().equals(iob_userWhoWantsDoAction.getEmail()) ||
                !iob_userWhoDoesRequest.getPassword().equals(iob_userWhoWantsDoAction.getPassword()));
    }
}
