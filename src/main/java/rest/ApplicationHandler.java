package rest;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import static rest.constants.RestConstants.APPLICATION_PATH;

@ApplicationPath(APPLICATION_PATH)
public class ApplicationHandler extends Application {}
