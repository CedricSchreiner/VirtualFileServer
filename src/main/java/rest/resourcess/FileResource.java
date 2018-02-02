package rest.resourcess;

import models.classes.FileTreeCollection;
import models.interfaces.User;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
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
    @Path(GC_PARAMETER_FILE_PATH)
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getFile(@PathParam(GC_PARAMETER_PATH_NAME) String iva_filePath, User iob_user) {
        FileTreeCollection lob_tree_collection = FileTreeCollection.getInstance();
        File file = new File(lob_tree_collection.getTreeFromUser(null).getNode(iva_filePath).getPath());
        Response.ResponseBuilder response = Response.ok(file);
        response.header(GC_CONTENT_DISPOSITION, "attachment;filename=" + file.getName());
        return response.build();
    }

    @POST
    @Path(GC_FILE_UPLOAD_PATH)
    @Consumes({MediaType.MULTIPART_FORM_DATA})
    public Response postFile(MultipartFormDataInput iob_input) {

        Map<String, List<InputPart>> lco_uploadForm = iob_input.getFormDataMap();
        List<InputPart> lob_inputParts = lco_uploadForm.get(GC_ATTACHMENT);
        FileService.addNewFile(lob_inputParts);
        return Response.status(200)
                .entity("File was uploaded").build();
    }
}
