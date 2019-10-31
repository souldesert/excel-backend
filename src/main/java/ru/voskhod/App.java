package ru.voskhod;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.session.SessionHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;

public class App 
{
    public static void main( String[] args )
    {
        Server server = new Server(8080);
        ServletContextHandler handler = new ServletContextHandler();
        handler.addServlet(ExcelServlet.class, "/compute");
        handler.setSessionHandler(new SessionHandler());

        server.setHandler(handler);

        try {
            server.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
