package rest;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

/**
 * Created by Cedruc on 23.12.2017.
 * Legt fest welche Arten von Resourcen an den Server weiter gegeben werden
 * wenn keine Resourcen angebeben sind == Klasse leer -> Per default: alle
 * Path: /api
 */

@ApplicationPath("/api")
public class ApplicationHandler extends Application {
}
