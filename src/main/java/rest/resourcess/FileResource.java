package rest.resourcess;

import builder.ServiceObjectBuilder;
import models.classes.FileTreeCollection;
import models.interfaces.User;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
import rest.Initializer;
import services.classes.AuthService;
import services.interfaces.FileService;

import javax.ws.rs.*;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.*;
import java.util.List;
import java.util.Map;

import static rest.constants.FileResourceConstants.*;

@Path(GC_FILE_RESOURCE_PATH)
public class FileResource {

    private static FileService gob_fileService = ServiceObjectBuilder.getFileServiceObject();

    @GET
    @Path(GC_FILE_DOWNLOAD_PATH + GC_PATH_PARAMETER_FILE_PATH)
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response downloadFile(@PathParam(GC_PARAMETER_PATH_NAME) String iva_filePath, ContainerRequestContext iob_requestContext) {
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
    public Response uploadFile(MultipartFormDataInput iob_input) {
        Map<String, List<InputPart>> lco_uploadForm = iob_input.getFormDataMap();
        List<InputPart> lob_inputParts = lco_uploadForm.get(GC_ATTACHMENT);
        if (!gob_fileService.addNewFile(lob_inputParts)) {
            return Response.status(Response.Status.EXPECTATION_FAILED).build();
        }

        return Response.ok().entity(FILE_UPLOADED).build();
    }

    @POST
    @Path(GC_FILE_RENAME_PATH + GC_PATH_PARAMETER_FILE_PATH + GC_PATH_PARAMETER_NEW_FILE_NAME)
    public Response renameFile(User iob_user, @PathParam(GC_PARAMETER_PATH_NAME) String iva_path, @PathParam(GC_PARAMETER_NEW_FILE_NAME) String iva_newFileName) {
        if(!gob_fileService.renameFile(iva_path, iva_newFileName, iob_user)) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        return Response.ok().entity(FILE_RENAMED).build();
    }

    @POST
    @Path(GC_FILE_DELETE_PATH + GC_PATH_PARAMETER_FILE_PATH)
    public Response deleteFile(User iob_user, @PathParam(GC_PARAMETER_PATH_NAME) String iva_path) {
        if (!gob_fileService.deleteFile(iva_path, iob_user)) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        return Response.ok().entity(FILE_DELETED).build();
    }

    @POST
    @Path(GC_FILE_MOVE_PATH + GC_PATH_PARAMETER_FILE_PATH + GC_PATH_PARAMETER_NEW_FILE_PATH)
    public Response moveFile(User iob_user, @PathParam(GC_PARAMETER_PATH_NAME) String iva_path, @PathParam(GC_PARAMETER_NEW_FILE_PATH) String iva_newFilePath) {
        if (!gob_fileService.moveFile(iva_path, iva_newFilePath, iob_user)) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        return Response.ok().entity(FILE_MOVED).build();
    }

    @POST
    @Path(GC_FILE_REMOVE_DIR_ONLY_PATH + GC_PATH_PARAMETER_FILE_PATH)
    public Response deleteDirectoryOnly(User iob_user, @PathParam(GC_PARAMETER_PATH_NAME) String iva_filePath) {
        if (!gob_fileService.deleteDirectoryOnly(iva_filePath, iob_user)) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        return Response.ok().entity(DIRECTORY_DELETED).build();
    }

    @POST
    @Path(GC_CREATE_DIRECTORY_PATH + GC_PATH_PARAMETER_FILE_PATH)
    public Response createDirectory(User iob_user, @PathParam(GC_PARAMETER_PATH_NAME) String iva_filePath) {
        if (!gob_fileService.createDirectory(iva_filePath, iob_user)) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        return Response.ok().entity(DIRECTORY_DELETED).build();
    }

    private User getUserFromContext(ContainerRequestContext iob_requestContext) {
        AuthService lob_authService;
        String iva_authCredentials;

        if(iob_requestContext.getUriInfo().getPath().contains("auth")) {
            iva_authCredentials = iob_requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);

            lob_authService = new AuthService();

            if(lob_authService.authenticateUser(iva_authCredentials) == null) {
                return null;
            }

            return lob_authService.authenticateUser(iva_authCredentials);
        }
        return null;
    }
}
