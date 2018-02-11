package rest.resources;

import builder.ServiceObjectBuilder;
import models.classes.FileTreeCollection;
import models.classes.User;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
import rest.Initializer;
import services.classes.AuthService;
import services.interfaces.FileService;

import javax.ws.rs.*;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.util.List;
import java.util.Map;

import static rest.constants.FileResourceConstants.*;

@Path(GC_FILE_RESOURCE_PATH)
public class FileResource {

    private static FileService gob_fileService = ServiceObjectBuilder.getFileServiceObject();

    @GET
    @Path(GC_FILE_DOWNLOAD_PATH + GC_PATH_PARAMETER_FILE_PATH)
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response downloadFile(@PathParam(GC_PARAMETER_PATH_NAME) String iva_filePath,
                                 @Context ContainerRequestContext iob_requestContext) {
        FileTreeCollection lob_tree_collection = FileTreeCollection.getInstance();
        User lob_user = getUserFromContext(iob_requestContext);

        try {
            if (lob_user == null) {
                throw new Exception();
            }

            iva_filePath = Initializer.getUserBasePath() + lob_user.getName() + lob_user.getUserId() + "\\" + iva_filePath;
            File lob_file = lob_tree_collection.getTreeFromUser(lob_user).getFile(iva_filePath);
            Response.ResponseBuilder response = Response.ok(lob_file);
            response.header(GC_CONTENT_DISPOSITION, "attachment;filename=" + lob_file.getName());
            return response.build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    @POST
    @Path(GC_FILE_UPLOAD_PATH)
    @Consumes({MediaType.MULTIPART_FORM_DATA})
    public Response uploadFile(MultipartFormDataInput iob_input, @QueryParam(GC_PARAMETER_PATH_NAME) String iva_filePath
            , @Context ContainerRequestContext iob_requestContext) {
        Map<String, List<InputPart>> lco_uploadForm = iob_input.getFormDataMap();
        List<InputPart> lob_inputParts = lco_uploadForm.get(GC_ATTACHMENT);

        User lob_user = getUserFromContext(iob_requestContext);

        if (!gob_fileService.addNewFile(lob_inputParts, iva_filePath, lob_user)) {
            return Response.status(Response.Status.EXPECTATION_FAILED).build();
        }

        return Response.ok().entity(FILE_UPLOADED).build();
    }

    @POST
    @Path(GC_FILE_RENAME_PATH)
    @Consumes(MediaType.TEXT_PLAIN)
    public Response renameFile(@Context ContainerRequestContext iob_requestContext,
                               @QueryParam(GC_PARAMETER_PATH_NAME) String iva_path, String iva_newFileName) {

        User lob_user = getUserFromContext(iob_requestContext);

        if (!gob_fileService.renameFile(iva_path, iva_newFileName, lob_user)) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        return Response.ok().entity(FILE_RENAMED).build();
    }

    @POST
    @Path(GC_FILE_DELETE_PATH)
    public Response deleteFile(@Context ContainerRequestContext iob_requestContext, String iva_path) {

        User lob_user = getUserFromContext(iob_requestContext);

        if (!gob_fileService.deleteFile(iva_path, lob_user)) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        return Response.ok().entity(FILE_DELETED).build();
    }

    @POST
    @Path(GC_FILE_MOVE_PATH)
    public Response moveFile(@Context ContainerRequestContext iob_requestContext,
                             @QueryParam(GC_PARAMETER_PATH_NAME) String iva_path, String iva_newFilePath) {

        User lob_user = getUserFromContext(iob_requestContext);

        if (!gob_fileService.moveFile(iva_path, iva_newFilePath, lob_user)) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        return Response.ok().entity(FILE_MOVED).build();
    }

    @POST
    @Path(GC_FILE_REMOVE_DIR_ONLY_PATH)
    public Response deleteDirectoryOnly(@Context ContainerRequestContext iob_requestContext, String iva_filePath) {
        User lob_user = getUserFromContext(iob_requestContext);

        if (!gob_fileService.deleteDirectoryOnly(iva_filePath, lob_user)) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        return Response.ok().entity(DIRECTORY_DELETED).build();
    }

    @POST
    @Path(GC_CREATE_DIRECTORY_PATH)
    public Response createDirectory(@Context ContainerRequestContext iob_requestContext, String iva_filePath) {
        User lob_user = getUserFromContext(iob_requestContext);

        if (!gob_fileService.createDirectory(iva_filePath, lob_user)) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        return Response.ok().entity(DIRECTORY_DELETED).build();
    }

    private User getUserFromContext(ContainerRequestContext iob_requestContext) {
        //----------------------------Variables----------------------------------
        AuthService lob_authService;
        String iva_authCredentials;
        //-----------------------------------------------------------------------

        iva_authCredentials = iob_requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
        lob_authService = new AuthService();

        if (lob_authService.authenticateUser(iva_authCredentials) == null) {
            return null;
        }

        return lob_authService.authenticateUser(iva_authCredentials);
    }
}
