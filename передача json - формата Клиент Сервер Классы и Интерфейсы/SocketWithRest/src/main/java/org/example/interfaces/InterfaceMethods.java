package org.example.interfaces;

import org.eclipse.jetty.websocket.api.Session;

public interface InterfaceMethods
{

    final class Args
    {
        public Session session;

        public String data;

        public Args(Session session, String data)
        {
            this.session = session;
            this.data = data;
        }

        public Args(String data)
        {
            this.data = data;
        }

        public Args(Session session)
        {
            this.session = session;
        }

        public Args()
        {

        }
    }
    void get(Args args) throws Exception; // используемый метод

    void save(Args args); // метод который возможно использовать

    void delete(Args args); // метод который возможно использовать

    void create(Args args); // метод который возможно использовать

    void update(Args args);
}

//    void get(Session session, String data) throws Exception; // используемый метод
//
//    void save(Session session, String content); // метод который возможно использовать
//
//    void delete(Session session); // метод который возможно использовать
//
//    void create(Session session); // метод который возможно использовать
//
//    void update(Session session);