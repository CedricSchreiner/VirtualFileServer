package rest.resources;

import builder.ServiceObjectBuilder;
import com.thoughtworks.xstream.XStream;
import models.classes.DownloadContent;
import models.classes.TreeDifference;
import models.classes.User;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
import services.classes.AuthService;
import services.interfaces.FileService;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Map;

import static rest.constants.FileResourceConstants.*;

@Path(GC_FILE_RESOURCE_PATH)
public class FileResource {

    private static FileService gob_fileService = ServiceObjectBuilder.getFileServiceObject();

    @GET
    @Path(GC_FILE_DOWNLOAD_PATH)
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response downloadFile(@QueryParam(GC_PARAMETER_PATH_NAME) String iva_filePath,
                                 @QueryParam(GC_PARAMETER_DIRECTORY_ID) int iva_directoryId,
                @Context ContainerRequestContext iob_requestContext) {
            User lob_user = getUserFromContext(iob_requestContext);
            DownloadContent lob_content = gob_fileService.downloadFile(iva_filePath, lob_user, iva_directoryId);

            if (lob_content == null) {
                return Response.status(404).build();
            }

        if (lob_content.getFile().isDirectory()) {
            return Response.status(204).header(GC_CONTENT_DISPOSITION, Integer.toString(lob_content.getVersion())).build();
        }

        Response.ResponseBuilder response = Response.ok(lob_content.getFile());
        response.header(GC_CONTENT_DISPOSITION, Integer.toString(lob_content.getVersion()));
        return response.build();
    }

    @POST
    @Path(GC_FILE_UPLOAD_PATH)
    @Consumes({MediaType.MULTIPART_FORM_DATA})
    public Response uploadFile(MultipartFormDataInput iob_input,
                               @QueryParam(GC_PARAMETER_PATH_NAME) String iva_filePath,
                               @QueryParam(GC_PARAMETER_DIRECTORY_ID) int iva_directoryId,
                               @QueryParam("version") int iva_version,
                               @Context ContainerRequestContext iob_requestContext,
                               @Context HttpServletRequest iob_servletRequest) {
        Map<String, List<InputPart>> lco_uploadForm = iob_input.getFormDataMap();
        List<InputPart> lob_inputParts = lco_uploadForm.get(GC_ATTACHMENT);
        int lva_result;
        User lob_user = getUserFromContext(iob_requestContext);

        lva_result = gob_fileService.addNewFile(lob_inputParts, iva_filePath, lob_user, iva_directoryId, iob_servletRequest.getRemoteAddr(), iva_version);

        if (lva_result == 1 || lva_result == 2) {
            return Response.status(422).build();
        }

        return Response.ok().entity(FILE_UPLOADED).build();
    }

    @POST
    @Path(GC_FILE_RENAME_PATH)
    @Consumes(MediaType.TEXT_PLAIN)
    public Response renameFile(@Context ContainerRequestContext iob_requestContext,
                               @QueryParam(GC_PARAMETER_PATH_NAME) String iva_path,
                               @QueryParam("directoryId") int iva_directoryId,
                               String iva_newFileName,
                               @Context HttpServletRequest iob_servletRequest) {

        User lob_user = getUserFromContext(iob_requestContext);

        if (!gob_fileService.renameFile(iva_path, iva_newFileName, lob_user, iva_directoryId, iob_servletRequest.getRemoteAddr())) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        return Response.ok().entity(FILE_RENAMED).build();
    }
//
//    @POST
//    @Path(GC_FILE_DELETE_PATH)
//    public Response deleteFile(@Context ContainerRequestContext iob_requestContext, String iva_path,
//                               @QueryParam(GC_PARAMETER_DIRECTORY_ID) int iva_directoryId,
//                               @Context HttpServletRequest iob_servletRequest) {
//
//        User lob_user = getUserFromContext(iob_requestContext);
//
//        if (!gob_fileService.deleteFile(iva_path, lob_user, iva_directoryId, iob_servletRequest.getRemoteAddr())) {
//            return Response.status(Response.Status.BAD_REQUEST).build();
//        }
//        return Response.ok().entity(FILE_DELETED).build();
//    }
//
//    @POST
//    @Path(GC_FILE_MOVE_PATH)
//    public Response moveFile(@Context ContainerRequestContext iob_requestContext,
//                             @QueryParam(GC_PARAMETER_PATH_NAME) String iva_path, String iva_newFilePath,
//                             @QueryParam("sourceDirectoryId") int iva_sourceDirectoryId,
//                             @QueryParam("destinationDirectoryId") int iva_destinationDirectoryId,
//                             @Context HttpServletRequest iob_servletRequest) {
//
//        int lva_result;
//        User lob_user = getUserFromContext(iob_requestContext);
//
//        lva_result = gob_fileService.moveFile(iva_path, iva_newFilePath, lob_user, iva_sourceDirectoryId, iva_destinationDirectoryId, iob_servletRequest.getRemoteAddr());
//        switch (lva_result) {
//            case GC_ERROR:
//                return Response.status(422).build();
//
//            case GC_MISSING_OR_WRONG_ARGUMENT:
//                return Response.status(Response.Status.BAD_REQUEST).build();
//
//            case GC_SUCCESS:
//                return Response.ok().entity(FILE_MOVED).build();
//
//            default:
//                return Response.status(Response.Status.BAD_REQUEST).build();
//        }
//    }
//
//    @POST
//    @Path(GC_FILE_REMOVE_DIR_ONLY_PATH)
//    public Response deleteDirectoryOnly(@Context ContainerRequestContext iob_requestContext, String iva_filePath,
//                                        @QueryParam(GC_PARAMETER_DIRECTORY_ID) int iva_directoryId,
//                                        @Context HttpServletRequest iob_servletRequest) {
//        User lob_user = getUserFromContext(iob_requestContext);
//
//        if (!gob_fileService.deleteDirectoryOnly(iva_filePath, lob_user, iva_directoryId, iob_servletRequest.getRemoteAddr())) {
//            return Response.status(Response.Status.BAD_REQUEST).build();
//        }
//        return Response.ok().entity(DIRECTORY_DELETED).build();
//    }

    @POST
    @Path(GC_CREATE_DIRECTORY_PATH)
    public Response createDirectory(@Context ContainerRequestContext iob_requestContext, String iva_filePath,
                                    @QueryParam(GC_PARAMETER_DIRECTORY_ID) int iva_directoryId,
                                    @Context HttpServletRequest iob_servletRequest) {
        User lob_user = getUserFromContext(iob_requestContext);
        int lva_result = gob_fileService.createDirectory(iva_filePath, lob_user, iva_directoryId, iob_servletRequest.getRemoteAddr());

//        if (!gob_fileService.createDirectory(iva_filePath, lob_user, iva_directoryId, iob_servletRequest.getRemoteAddr())) {
//            return Response.status(Response.Status.BAD_REQUEST).build();
//        }
        return Response.ok().entity(DIRECTORY_DELETED).build();
    }

    @POST
    @Path("compare")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.TEXT_PLAIN)
    public Response compareTrees(@Context ContainerRequestContext iob_requestContext, String lva_clientFilesAsXml) {
        User lob_user = getUserFromContext(iob_requestContext);

        TreeDifference lob_difference = gob_fileService.compareFiles(lva_clientFilesAsXml, lob_user);

        XStream lob_xStream = new XStream();
        String rva_xmlString = lob_xStream.toXML(lob_difference);

        return Response.ok().entity(rva_xmlString).build();
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
