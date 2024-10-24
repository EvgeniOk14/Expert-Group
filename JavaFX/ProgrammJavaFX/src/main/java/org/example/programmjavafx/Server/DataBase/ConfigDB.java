package org.example.programmjavafx.Server.DataBase;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/** Класс отвечает за соединение с базой данных PostgreSQL **/
public class ConfigDB
{
    //region Fields
    private static final String URL = "jdbc:postgresql://localhost:5432/postgres"; // путь к базе данных, с именем postgres
    private static final String USER = "postgres"; // имя пользователя
    private static final String PASSWORD = "oew"; // пароль для доступа к базе данных
    //endregion

    /**
     * метод получения соединения с базо данных
     * **/
    public static Connection getConnection() throws SQLException
    {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
