package rest.resourcess;

import models.classes.FileTreeCollection;
import models.interfaces.User;
import org.apache.commons.io.IOUtils;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
import services.classes.FileService;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.io.*;
import java.util.List;
import java.util.Map;

@Path("/files")
public class FileResource {

    @GET
    @Path("{path}")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getFile(@PathParam("path") String iva_filePath, User iob_user) {
        FileTreeCollection lob_tree_collection = FileTreeCollection.getInstance();
        File file = new File(lob_tree_collection.getTreeFromUser(null).getNode(iva_filePath).getPath());
        Response.ResponseBuilder response = Response.ok(file);
        response.header("Content-Disposition", "attachment;filename=" + file.getName());
        return response.build();
    }

    @POST
    @Path("/upload")
    @Consumes({MediaType.MULTIPART_FORM_DATA})
    public Response postFile(MultipartFormDataInput input) {

        String fileName = "";

        Map<String, List<InputPart>> uploadForm = input.getFormDataMap();
        List<InputPart> inputParts = uploadForm.get("attachment");
        FileService.addNewFile(inputParts);
        return Response.status(200)
                .entity("uploadFile is called, Uploaded file name : " + fileName).build();
    }
}
