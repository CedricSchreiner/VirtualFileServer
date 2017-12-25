package rest;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import static constants.ApplicationHandlerConstants.APPLICATION_PATH;

/**
 * Created by Cedruc on 23.12.2017.
 * Legt fest welche Arten von Resourcen an den Server weiter gegeben werden
 * wenn keine Resourcen angebeben sind == Klasse leer -> Per default: alle
 * Path: /webapi
 */

@ApplicationPath(APPLICATION_PATH)
public class ApplicationHandler extends Application {
}
