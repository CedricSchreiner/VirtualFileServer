package rest;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import static rest.ApplicationHandler.APPLICATION_PATH;

@ApplicationPath(APPLICATION_PATH)
public class ApplicationHandler extends Application {
    static final String APPLICATION_PATH = "/api";
}
