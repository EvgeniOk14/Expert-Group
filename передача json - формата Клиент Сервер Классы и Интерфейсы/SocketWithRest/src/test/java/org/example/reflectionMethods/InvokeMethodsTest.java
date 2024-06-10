package org.example.reflectionMethods;

import com.google.gson.JsonObject;
import org.example.entities.LogEntity;

import static org.junit.jupiter.api.Assertions.*;

class InvokeMethodsTest {

    @org.junit.jupiter.api.Test
    void invokeMethod() throws Exception {
        InvokeMethods invokeMethods = new InvokeMethods();
        invokeMethods.invokeMethod(new LogEntity(), "get", "6", new JsonObject());
    }

    @org.junit.jupiter.api.Test
    void invokeMethod2() throws Exception {
        InvokeMethods invokeMethods = new InvokeMethods();
        invokeMethods.invokeMethod(new LogEntity(), "get", null, new JsonObject());
    }

//    @org.junit.jupiter.api.Test
//    void instanceOf() throws Exception {
//       String t = null;
//       if (t instanceof String) {
//           System.out.println("true");
//       } else {
//           System.out.println(false);
//       }
//    }
}