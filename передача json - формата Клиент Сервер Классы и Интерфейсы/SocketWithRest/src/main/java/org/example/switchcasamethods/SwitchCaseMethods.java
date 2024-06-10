package org.example.switchcasamethods;

import com.google.gson.JsonObject;
import org.eclipse.jetty.websocket.api.Session;
import org.example.interfaces.InterfaceMethods;

import java.io.IOException;

public class SwitchCaseMethods
{
    public static void switchCaseMethod(InterfaceMethods handler,
                                 String method,
                                 Session session,
                                 String data,
                                 JsonObject response) throws Exception
    {
        switch (method.toLowerCase())
        {
            case "get":
                handler.get(new InterfaceMethods.Args(session, data));
                break;
            case "update":
                handler.update(new InterfaceMethods.Args(session));
                break;
            case "save":
                handler.save(new InterfaceMethods.Args(session, data));
                break;
            case "delete":
                handler.delete(new InterfaceMethods.Args(session));
                break;
            case "create":
                handler.create(new InterfaceMethods.Args(session));
                break;
            default:
                response.addProperty("status", "error");
                response.addProperty("message", "Unsupported method.");
                session.getRemote().sendString(response.toString());
        }
    }
}
