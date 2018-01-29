package rest.resourcess;

import models.classes.FileTreeCollection;
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
    public Response getFile(@PathParam("path") String test) {
        FileTreeCollection lob_tree_collection = FileTreeCollection.getInstance();
        File file = new File(lob_tree_collection.getTreeFromUser(null).getNode(test).getPath());
        Response.ResponseBuilder response = Response.ok(file);
        response.header("Content-Disposition", "attachment;filename=" + file.getName());
        return response.build();
    }


    //Todo Refactoring
    @POST
    @Path("/upload")
    @Consumes({MediaType.MULTIPART_FORM_DATA})
    public Response postFile(MultipartFormDataInput input) {

        String fileName = "";

        Map<String, List<InputPart>> uploadForm = input.getFormDataMap();
        List<InputPart> inputParts = uploadForm.get("attachment");
        FileService.addNewFile(inputParts);
//        for (InputPart inputPart : inputParts) {
//
//            try {
//
//                MultivaluedMap<String, String> header = inputPart.getHeaders();
//                fileName = getFileName(header);
//
//                //convert the uploaded file to inputstream
//                InputStream inputStream = inputPart.getBody(InputStream.class,null);
//
//                byte [] bytes = IOUtils.toByteArray(inputStream);
//
//                //constructs upload file path
//                fileName = "C:\\Users\\Cedric\\Documents\\" + fileName;
//
//                writeFile(bytes,fileName);
//
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//        }

        return Response.status(200)
                .entity("uploadFile is called, Uploaded file name : " + fileName).build();
    }

    //save to somewhere
//    private void writeFile(byte[] content, String filename) throws IOException {
//
//        File file = new File(filename);
//
//        FileOutputStream fop = new FileOutputStream(file);
//
//        fop.write(content);
//        fop.flush();
//        fop.close();
//
//    }
}
