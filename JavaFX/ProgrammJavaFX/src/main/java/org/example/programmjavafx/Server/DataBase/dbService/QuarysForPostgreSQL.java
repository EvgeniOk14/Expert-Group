package org.example.programmjavafx.Server.DataBase.dbService;

/** Класс хранит запросы к таблицам: lathe_plates и events базы данных PostgreSQL **/
public class QuarysForPostgreSQL
{

   public static final String QUERY_GET_EVENT_TYPE= "SELECT " +
           "lp.plate_number," +
           " e.event_message, e.event_type," +
           " e.event_time" +
           " FROM events e JOIN lathe_plates lp ON e.plate_id = lp.id " +
           "WHERE e.event_type = ?";

   public static final String QUERY_GET_PLATE_NUMBER = "SELECT e.*" +
           "FROM events e " +
           "JOIN lathe_plates lp ON e.plate_id = lp.id " +
           "WHERE lp.plate_number = ?";

   public static final String QUERY_GET_EVENT_TIME_CONCRETE = "SELECT lp.plate_number, e.event_message, e.event_type, e.event_time " +
           "FROM events e " +
           "JOIN lathe_plates lp ON e.plate_id = lp.id " +
           "WHERE e.event_time = ?";

   public static final String QUERY_GET_EVENT_TIME_FROM = "SELECT lp.plate_number, e.event_message, e.event_type, e.event_time " +
           "FROM events e " +
           "JOIN lathe_plates lp ON e.plate_id = lp.id " +
           "WHERE e.event_time >= ?";

   public static final String QUERY_GET_EVENT_TIME_TILL = "SELECT lp.plate_number, e.event_message, e.event_type, e.event_time " +
           "FROM events e " +
           "JOIN lathe_plates lp ON e.plate_id = lp.id " +
           "WHERE e.event_time <= ?";

    public static final String QUERY_GET_EVENT_TIME_BEETWEN = "SELECT lp.plate_number, e.event_message, e.event_type, e.event_time " +
           "FROM events e " +
           "JOIN lathe_plates lp ON e.plate_id = lp.id " +
           "WHERE e.event_time BETWEEN ? AND ?";

public static final String QUERY_ALL_FILTERS_PLATE_NUMBER_AND_BETWEEN_PERIOD = "SELECT lp.plate_number, e.event_message, e.event_type, e.event_time " +
        "FROM events e " +
        "JOIN lathe_plates lp ON e.plate_id = lp.id " +
        "WHERE lp.plate_number = ? AND e.event_time BETWEEN ? AND ?";
}
//public static final String QUERY_ALL_FILTERS_PLATE_NUMBER_AND_BETWEEN_PERIOD1 = "SELECT lp.plate_number, e.event_message, e.event_type, e.event_time FROM events e JOIN lathe_plates lp ON e.plate_id = lp.id WHERE lp.plate_number = ? AND e.event_time BETWEEN ? AND ?";
//
//public static final String QUERY_ALL_FILTERS_PLATE_NUMBER_AND_FROM_PERIOD = "SELECT lp.plate_number, e.event_message, e.event_type, e.event_time " +
//        "FROM events e " +
//        "JOIN lathe_plates lp ON e.plate_id = lp.id " +
//        "WHERE lp.plate_number = ? AND e.event_time>=?";
//public static final String QUERY_ALL_FILTERS_PLATE_NUMBER_AND_TILL_PERIOD = "SELECT lp.plate_number, e.event_message, e.event_type, e.event_time " +
//        "FROM events e " +
//        "JOIN lathe_plates lp ON e.plate_id = lp.id " +
//        "WHERE lp.plate_number = ? AND e.event_time<=?";
//public static final String QUERY_ALL_FILTERS_PLATE_NUMBER_AND_CONCRETE_PERIOD = "SELECT lp.plate_number, e.event_message, e.event_type, e.event_time " +
//        "FROM events e " +
//        "JOIN lathe_plates lp ON e.plate_id = lp.id " +
//        "WHERE lp.plate_number = ? AND e.event_time=?";
//
//public static final String QUERY_ALL_FILTERS_BETWEEN_PERIOD ="SELECT e.*, lp.plate_number " +
//        "FROM events e " +
//        "JOIN lathe_plates lp ON e.plate_id = lp.id " +
//        "WHERE e.event_time BETWEEN ? AND ?";
//public static final String QUERY_ALL_FILTERS_FROM_PERIOD ="SELECT e.*, lp.plate_number " +
//        "FROM events e " +
//        "JOIN lathe_plates lp ON e.plate_id = lp.id " +
//        "WHERE e.event_time >=?";
//public static final String QUERY_ALL_FILTERS_TILL_PERIOD ="SELECT e.*, lp.plate_number " +
//        "FROM events e " +
//        "JOIN lathe_plates lp ON e.plate_id = lp.id " +
//        "WHERE e.event_time <=?";
//public static final String QUERY_ALL_FILTERS_CONCRETE_DATA ="SELECT e.*, lp.plate_number " +
//        "FROM events e " +
//        "JOIN lathe_plates lp ON e.plate_id = lp.id " +
//        "WHERE e.event_time =?";