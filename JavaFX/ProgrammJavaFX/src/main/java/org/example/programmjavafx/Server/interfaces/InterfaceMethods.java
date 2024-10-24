package org.example.programmjavafx.Server.interfaces;

import org.eclipse.jetty.websocket.api.Session;
import java.io.File;
import java.util.Map;

/**
 * InterfaceMethods - это интерфейс, который определяет набор методов
 * для работы с различными сущностями.
 * Он включает в себя вложенный класс Args,
 * который служит для передачи параметров в методы интерфейса.
 * **/
public interface InterfaceMethods
{
    /**
     *  вложенный класс Args
     *  служит для описания необходимых полей,
     *  которые необходимо передать в тот или иной метод
     *  с возможностью доступа к ним в этих методах,
     *  через Args.поле (например:  Args.session)
     *  **/
    final class Args
    {
        //region Fields
        public Session session;

        public String data;

        public String jsonFileDirectory = "C:" + File.separator + "Expert Group" + File.separator + "Json_projtcts" + File.separator + "untitled";

        public String logFileDirectory = "C:" + File.separator  + "Expert Group" + File.separator + "JavaFX" + File.separator + "ProgrammJavaFX" + File.separator + "savedFiles";

        public static String plateNumber;

        public Map<Session, String> sessions;

        public String entity;
        //endregion

        // конструкторы, для возможности вызова метода с любым количеством параметров:

        //region Constructors
        public Args(Session session, String data, String jsonFileDirectory, String logFileDirectory, String plateNumber, Map<Session, String> sessions, String entity)
        {
            this.session = session;
            this.data = data;
            this.jsonFileDirectory = jsonFileDirectory;
            this.logFileDirectory = logFileDirectory;
            this.plateNumber = plateNumber;
            this.sessions = sessions;
            this.entity = entity;
        }

        public Args(Session session, String data, String jsonFileDirectory, String plateNumber)
        {
            this.session = session;
            this.data = data;
            this.jsonFileDirectory = jsonFileDirectory;
            this.plateNumber = plateNumber;
        }

        public Args(Session session, String data, String jsonFileDirectory)
        {
            this.session = session;
            this.data = data;
            this.jsonFileDirectory = jsonFileDirectory;
        }

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
            //default constructor
        }
        //endregion

        //region Getters & Setters
        public String getPlateNumber()
        {
            return plateNumber;
        }

        public void setPlateNumber(String plateNumber)
        {
            this.plateNumber = plateNumber;
        }
        //endregion
    }

    //region Methods
    // методы которые будут переопределятся в сущностях имплементирующих данный интерфейс:
    void get(Args args) throws Exception; // используемый метод

    void save(Args args) throws Exception; // метод который возможно использовать

    void delete(Args args) throws Exception; // метод который возможно использовать

    void create(Args args) throws Exception; // метод который возможно использовать

    void update(Args args) throws Exception;
    //endregion
}
