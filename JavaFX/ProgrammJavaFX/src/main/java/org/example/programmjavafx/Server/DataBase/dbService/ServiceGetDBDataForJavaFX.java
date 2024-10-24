package org.example.programmjavafx.Server.DataBase.dbService;

import org.example.programmjavafx.Server.DataBase.ConfigDB;
import org.example.programmjavafx.Server.entityDB.Event;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Класс сервис - ServiceGetDBDataForJavaFX
 * содержит методы получения информации из базы данных,
 * по заданным параметрам,
 * для вывода в графическом окне JavaFX, в окне table-view
 * **/
public class ServiceGetDBDataForJavaFX
{
    //region Fields
    private static List<String[]> eventMessages;
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    //endregion

    /**
     * метод получения всех сообщений из таблицы "events" по заданному номеру платы,
     * а именно, всех записей из колонки "event_message"
     **/
    public List<Event> getEventsByPlateNumber(String plateNumber, String QUERY)
    {
        List<Event> events = new ArrayList<>();

        try (Connection connection = ConfigDB.getConnection();
             PreparedStatement ps = connection.prepareStatement(QUERY))
        {
            ps.setString(1, plateNumber);

            try (ResultSet resultSet = ps.executeQuery())
            {
                while (resultSet.next())
                {
                    // Создаем объект Event и заполняем его данными из resultSet
                    Event event = new Event();
                    event.setId(resultSet.getInt("id"));
                    event.setPlateId(resultSet.getInt("plate_id"));
                    event.setEventType(resultSet.getString("event_type"));
                    event.setEventTime(resultSet.getTimestamp("event_time"));
                    event.setEventMessage(resultSet.getString("event_message"));

                    events.add(event); // Добавляем объект Event в список
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return events;
    }

    /**
     * метод получения события и относящихся к нему сообщений:
     * 1) из таблицы lathe_plates номера платы
     * 2) из таблицы "events" всех сообщений,
     * по заданному фильтру: WHERE event_type=?
     * аргумент eventMessage - описывает текст сообщения того события, которое необходимо найти
     **/
    public List<String[]> getEventMessages(String eventMessage, String QUERY)
    {
        try (Connection connection = ConfigDB.getConnection();
             PreparedStatement ps = connection.prepareStatement(QUERY))
        {
            ps.setString(1, eventMessage);

            try (ResultSet rs = ps.executeQuery())
            {
                eventMessages = processResultSet(rs);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return eventMessages;
    }


    /**
     * метод для получения сообщений, из таблиц lathe_plates и events
     * по заданому времени и дате
     * аргументы: 1) период времени; 2) запрос к базе данных по данному периоду
     **/
    public List<String[]> getDataByPeriod(Timestamp dataFromPeriod, String QUERY)
    {
        try (Connection connection = ConfigDB.getConnection();
             PreparedStatement ps = connection.prepareStatement(QUERY))
        {
            ps.setTimestamp(1, dataFromPeriod);

            try (ResultSet rs = ps.executeQuery())
            {
                eventMessages = processResultSet(rs);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return eventMessages;
    }

    /**
     * метод для получения сообщений, из таблиц lathe_plates и events
     * по заданому времени и дате
     * аргументы: 1) начальное значение времени 2) конечное значение времени; 3) запрос к базе данных по данному периоду
     **/
    public List<String[]> getDataByPeriodFromTill(Timestamp dataFromPeriod, Timestamp dataTillPeriod, String QUERY)
    {
        try (Connection connection = ConfigDB.getConnection();
             PreparedStatement ps = connection.prepareStatement(QUERY))
        {
            ps.setTimestamp(1, dataFromPeriod);
            ps.setTimestamp(2, dataTillPeriod);

            try (ResultSet rs = ps.executeQuery())
            {
                eventMessages = processResultSet(rs);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return eventMessages;
    }

    /**
     * метод получения данных из результата запроса к БД
     * создания и заполнения списка сообщений
     * (используется в других методах этого класса)
     **/
    public List<String[]> processResultSet(ResultSet rs) throws SQLException
    {
        List<String[]> eventMessages = new ArrayList<>(); // создаём список сообщений
        while (rs.next()) // устанавливаем курсор на следующую колонку результата запроса объекта ResultSet
        {
            String plateNumber = rs.getString("plate_number"); // получаем значение из колонки plate_number
            String message = rs.getString("event_message");   // получаем значение из колонки event_message
            String typeEvent = rs.getString("event_type");   // получаем значение из колонки event_type
            Timestamp timeEvent = rs.getTimestamp("event_time"); // получаем значение из колонки event_time
            eventMessages.add(new String[]{plateNumber, message, typeEvent, timeEvent.toString()}); // добавляем массив строк в список
        }
        return eventMessages; // возвращаем список сообщений
    }

    // получение текущего времени в формате String
    public String getCurrentDataTime()
    {
        //получаем текущее время и дату
        LocalDateTime currentDateTime = LocalDateTime.now(); // текущее время
        String currentDataTimeString = currentDateTime.format(dateTimeFormatter);
        return currentDataTimeString;
    }
    // получение текущего времени минус 1 час, в формате String
    public String getDataTimeMinusHour()
    {
        // получаем время и дату меньшую на один час от текущей
        LocalDateTime currentTimeMinusOneHour = LocalDateTime.now().minusHours(1);
        return currentTimeMinusOneHour.format(dateTimeFormatter);
    }

    // получение текущего времени минус 1 день, в формате String
    public String getDataTimeMinusDay()
    {
        // получаем время и дату меньшую на один день от текущей
        LocalDateTime currentTimeMinusOneHour = LocalDateTime.now().minusDays(1);
        return currentTimeMinusOneHour.format(dateTimeFormatter);
    }

    // получение текущего времени минус 1 неделя, в формате String
    public String getDataTimeMinusWeek()
    {
        // получаем время и дату меньшую на одну неделю от текущей
        LocalDateTime currentTimeMinusOneWeek = LocalDateTime.now().minusWeeks(1);
        return currentTimeMinusOneWeek.format(dateTimeFormatter);
    }

    // получение текущего времени минус 1 месяц, в формате String
    public String getDataTimeMinusMonth()
    {
        // получаем время и дату меньшую на один месяц от текущей
        LocalDateTime currentTimeMinusOneMonth = LocalDateTime.now().minusMonths(1);
        return currentTimeMinusOneMonth.format(dateTimeFormatter);
    }

    // получение текущего времени минус 1 год, в формате String
    public String getDataTimeMinusYear()
    {
        // получаем время и дату меньшую на один год от текущей
        LocalDateTime currentTimeMinusOneYear = LocalDateTime.now().minusYears(1);
        return currentTimeMinusOneYear.format(dateTimeFormatter);
    }

}



///**
// * метод получения всех сообщений из таблицы "events" по заданному номеру платы,
// * а именно, всех записей из колонки "event_message"
// **/
//public List<String[]> getEventsByPlateNumberAndTimePeriod(Timestamp startTime, Timestamp endTime, String plateNumber)
//{
//    try (Connection connection = ConfigDB.getConnection();
//         PreparedStatement ps = connection.prepareStatement(QuarysForPostgreSQL.QUERY_ALL_FILTERS_PLATE_NUMBER_AND_BETWEEN_PERIOD)) {
//        ps.setString(1, plateNumber);
//        ps.setTimestamp(2, startTime);
//        ps.setTimestamp(3, endTime);
//
//        try (ResultSet resultSet = ps.executeQuery())
//        {
//            eventMessages = processResultSet(resultSet);
//        }
//        return eventMessages;
//    }
//    catch (Exception e)
//    {
//        e.printStackTrace();
//    }
//    return null;
//}
//
