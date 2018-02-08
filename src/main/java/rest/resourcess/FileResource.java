package rest.resourcess;

import models.classes.FileTreeCollection;
import models.interfaces.User;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
import rest.Initializer;
import services.classes.FileService;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.*;
import java.util.List;
import java.util.Map;

import static rest.constants.FileResourceConstants.*;

@Path(GC_FILE_RESOURCE_PATH)
public class FileResource {

    @GET
    @Path(GC_FILE_DOWNLOAD_PATH + GC_PATH_PARAMETER_FILE_PATH)
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response downloadFile(@PathParam(GC_PARAMETER_PATH_NAME) String iva_filePath, User iob_user) {
        FileTreeCollection lob_tree_collection = FileTreeCollection.getInstance();
        try {
            //File file = new File(lob_tree_collection.getTreeFromUser(iob_user).getFile(iva_filePath).getCanonicalPath());
            iva_filePath = Initializer.getUserBasePath() + iob_user.getName() + iob_user.getUserId() + "\\" + iva_filePath;
            File lob_file = lob_tree_collection.getTreeFromUser(iob_user).getFile(iva_filePath);
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
        FileService.addNewFile(lob_inputParts);
        return Response.ok().entity(FILE_UPLOADED).build();
    }

    @POST
    @Path(GC_FILE_RENAME_PATH + GC_PATH_PARAMETER_FILE_PATH + GC_PATH_PARAMETER_NEW_FILE_NAME)
    public Response renameFile(User iob_user, @PathParam(GC_PARAMETER_PATH_NAME) String iva_path, @PathParam(GC_PARAMETER_NEW_FILE_NAME) String iva_newFileName) {
        if(!FileService.renameFile(iva_path, iva_newFileName, iob_user)) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        return Response.ok().entity(FILE_RENAMED).build();
    }

    @POST
    @Path(GC_FILE_DELETE_PATH + GC_PATH_PARAMETER_FILE_PATH)
    public Response deleteFile(User iob_user, @PathParam(GC_PARAMETER_PATH_NAME) String iva_path) {
        if (!FileService.deleteFile(iva_path, iob_user)) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        return Response.ok().entity(FILE_DELETED).build();
    }

    @POST
    @Path(GC_FILE_MOVE_PATH + GC_PATH_PARAMETER_FILE_PATH + GC_PATH_PARAMETER_NEW_FILE_PATH)
    public Response moveFile(User iob_user, @PathParam(GC_PARAMETER_PATH_NAME) String iva_path, @PathParam(GC_PARAMETER_NEW_FILE_PATH) String iva_newFilePath) {
        if (!FileService.moveFile(iva_path, iva_newFilePath, iob_user)) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        return Response.ok().entity(FILE_MOVED).build();
    }

    @POST
    @Path(GC_FILE_REMOVE_DIR_ONLY_PATH + GC_PATH_PARAMETER_FILE_PATH)
    public Response deleteDirectoryOnly(User iob_user, @PathParam(GC_PARAMETER_PATH_NAME) String iva_filePath) {
        if (!FileService.deleteDirectoryOnly(iva_filePath, iob_user)) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        return Response.ok().entity(DIRECTORY_DELETED).build();
    }

    @POST
    @Path(GC_CREATE_DIRECTORY_PATH + GC_PATH_PARAMETER_FILE_PATH)
    public Response createDirectory(User iob_user, @PathParam(GC_PARAMETER_PATH_NAME) String iva_filePath) {
        if (!FileService.createDirectory(iva_filePath, iob_user)) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        return Response.ok().entity(DIRECTORY_DELETED).build();
    }
}
