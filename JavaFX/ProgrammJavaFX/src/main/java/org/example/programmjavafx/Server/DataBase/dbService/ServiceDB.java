package org.example.programmjavafx.Server.DataBase.dbService;

import org.example.programmjavafx.Server.DataBase.ConfigDB;
import org.example.programmjavafx.Server.entityDB.Plate;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Класс ServiceDB работает с базой данных postgreSQL
 * и её таблицами lathe_plates и events
 * **/
public class ServiceDB
{
    //region Fields
    //  устанавливаем формат времени: "yyyy-MM-dd'T'HH:mm:ss.SSS"
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
    //endregion

    /**
     * создание таблицы lathe_plates в базе данных postgreSQL, если она не создана
     * **/
    public void createTableLathe_platesIfNotExists()
    {
        String sql = "CREATE TABLE IF NOT EXISTS lathe_plates (" +
                "id SERIAL PRIMARY KEY, " +
                "plate_number VARCHAR(255) UNIQUE NOT NULL)";

        try (Connection connection = ConfigDB.getConnection();
             Statement statement = connection.createStatement())
        {
            statement.executeUpdate(sql);
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * создание таблицы events в базе данных postgreSQL, если она не создана
     * **/
    public void createTableEventsIfNottExists()
    {
        String SQL = "CREATE TABLE IF NOT EXISTS events (" +
                "id SERIAL PRIMARY KEY, " +
                "plate_id INTEGER NOT NULL REFERENCES lathe_plates(id), " +
                "event_type VARCHAR(50) NOT NULL, " +
                "event_time TIMESTAMP NOT NULL, " +
                "event_message TEXT NOT NULL)";

        try(Connection connection = ConfigDB.getConnection();
        Statement statement = connection.createStatement();)
        {
            statement.executeUpdate(SQL);
            System.out.println("Таблица lathe_plates успешно создана или уже существует.");
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * метод обрабатывает событие startWork "начало работы станка с платой"
     *  и заносит данные в соответствуюшие таблицы в базе данных postgreSQL
     *  **/
    public int insertStartWork(Plate plate, String eventMessage)
    {
        // SQL-запрос для проверки существования записи в таблице lathe_plates (выборка id из таблицы plate_number)
        String QUERY_check_lathe_plates_exists_plate_number = "SELECT id FROM lathe_plates WHERE plate_number = ?";

        // SQL-запрос к таблице lathe_plates (вставка в таблицу lathe_plates значения plate_number и возврат значения id)
        String QUERY_insert_into_LathePlate_plate_number = "INSERT INTO lathe_plates (plate_number) VALUES (?) RETURNING id";

        // SQL-запрос к таблице events (вставка значений  втаблицу events)
        String QUARY_insert_into_events_values = "INSERT INTO events (plate_id, event_type, event_time, event_message) VALUES (?, ?, ?, ?)";

        try (Connection connection = ConfigDB.getConnection(); // создаём соединение с БД

             // создание объекта PreparedStatement, который будет использоваться для выполнения SQL-запроса с параметрами.

             // проверка на наличие такого номера платы в таблице lathe_plates
             PreparedStatement psCheckLathePlate = connection.prepareStatement(QUERY_check_lathe_plates_exists_plate_number);

             // вставка в таблицу lathe_plates номера платы
             PreparedStatement psLathePlatesInsertPlateNumber = connection.prepareStatement(QUERY_insert_into_LathePlate_plate_number);

             // вставка в таблицу events значений
             PreparedStatement psEvents = connection.prepareStatement(QUARY_insert_into_events_values))
        {

            // обрабатываем первый запрос, т.е. проверку на наличие такой платы в БД
            psCheckLathePlate.setString(1, plate.getPlateNumber()); // устанавливаем параметр plate_number из объекта Plate
            ResultSet rsCheckLathePlate = psCheckLathePlate.executeQuery(); // записываем в результат выполнения запроса в объект Resultset

            int plateId  = -1; // инициализируем переменную значением -1 (устанавливаем флаг),
                              //  чтобы указать, что запись не найдена

            if (rsCheckLathePlate.next()) // перемещаем курсор на следующую строку результата ResultSet, если строка существует, возвращает true
            {
                // устанавливаем значение plateId равное результату запроса
                plateId = rsCheckLathePlate.getInt(1);

                // Вставляем записи в таблицу events
                psEvents.setInt(1, plateId); // вставляем номер id  платы станка из таблицы lathe_plates в поле plateId  таблицы event
                psEvents.setString(2, "Начало работы с новой платой."); // записываем само событие (начало работы)
                psEvents.setTimestamp(3, convertStringToTimestampForDB(LocalDateTime.now().format(formatter)));
                psEvents.setString(4, eventMessage); // записываем само сообщение о событие

                psEvents.executeUpdate(); // выполняем вставку в таблицу events перечисленных данных
            }
            if (plateId == -1) // если запрос не нашёл такой номер платы в БД, то создаём новую запись в таблице lathe_plates  с таким номером платы
           {
               // Запись не существует, вставляем новую запись и получаем plate_id
               psLathePlatesInsertPlateNumber.setString(1, plate.getPlateNumber()); // вставляем номер платы в таблицу lath_plates
               ResultSet rsLathePlates = psLathePlatesInsertPlateNumber.executeQuery(); // выполняем вставку в таблицу lathe_plates и возвращаем объект ResultSet, т.е. результат sql запроса - id


               if (rsLathePlates.next()) // переводим курсор на первую строку таблицы результата запроса объекта ResultSet
               {
                   // получаем значение из колонки 1 текущей строки
                   plateId = rsLathePlates.getInt(1);

                   if (plateId != -1) // если запись с таким plateID  существует, то производим вставку данных в таблицу event
                   {
                       // Вставляем записи в таблицу events
                       psEvents.setInt(1, plateId); // вставляем номер id  платы станка из таблицы lathe_plates в поле plateId  таблицы event
                       psEvents.setString(2, "Начало работы с новой платой."); // записываем само событие (начало работы)
                       psEvents.setTimestamp(3, convertStringToTimestampForDB(LocalDateTime.now().format(formatter)));
                       psEvents.setString(4, eventMessage); // записываем само сообщение о событие

                       psEvents.executeUpdate(); // выполняем вставку в таблицу events перечисленных данных
                   }
                   return plateId;
               }

               rsLathePlates.close(); // закрытие ResultSet и освобождение ресурсов
           }
           rsCheckLathePlate.close();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * метод обрабатывает все остальные события:
     * по записи eventTypeMessage из таблицы events  колонки event_type
     * **/
    public void updateWorkEvent(String plateNumber, String eventMessage, String eventTypeMessage)
    {
        String sqlGetPlateId = "SELECT id FROM lathe_plates WHERE plate_number = ?";
        String sqlEvent = "INSERT INTO events (plate_id, event_type, event_time, event_message) values (?, ?, ?, ?)";
        try (Connection connection = ConfigDB.getConnection();
             PreparedStatement psGetPlateIds = connection.prepareStatement(sqlGetPlateId);
             PreparedStatement psEvents = connection.prepareStatement(sqlEvent))
        {
            psGetPlateIds.setString(1, plateNumber);
            ResultSet rsGetPlateIds = psGetPlateIds.executeQuery();

            if (rsGetPlateIds.next())
            {
                int plateId = rsGetPlateIds.getInt("id");

                psEvents.setInt(1, plateId); // устанавливаем id платы
                psEvents.setString(2, eventTypeMessage); // записываем тип события
                psEvents.setTimestamp(3, convertStringToTimestampForDB(LocalDateTime.now().format(formatter)));
                psEvents.setString(4, eventMessage); // записываем само сообщение о событии

                psEvents.executeUpdate(); // выполняем вставку в таблицу events перечисленных данных
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    // Метод для преобразования строки времени в Timestamp
    public Timestamp convertStringToTimestampForDB(String timeString)
    {
        if (timeString == null || timeString.isEmpty())
        {
            return null;
        }
        if (timeString.length() > 23) // Обрезаем строку до нужной длины
        {
            timeString = timeString.substring(0, 23);
        }
        LocalDateTime localDateTime = LocalDateTime.parse(timeString, formatter);
        return Timestamp.valueOf(localDateTime);
    }

    // конвертация строки в тип Timestamp с добавлением миллисекунд
    public Timestamp convertStringToTimestampWithMilisecond(String timePeriod)
    {
        try
        {
            DateTimeFormatter formatterWithMiliSeck = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS"); // создаём шаблон времени в заданном формате "yyyy-MM-dd HH:mm:ss.SSSSSS" (указывает как разбить строку: yyyy - год, MM - месяц, dd - день, HH - часы, mm - минуты, ss - секунды, SSSSSS - миллисекунды)
            LocalDateTime localDateTime = LocalDateTime.parse(timePeriod, formatterWithMiliSeck); // создаём объект типа LocalDataTime и преобразуем в него переданную строку timePeriod, в соответствии с заданным шаблоном
            return Timestamp.valueOf(localDateTime); // преобразует LocalDataTime в тип Timestamp
        }
        catch (DateTimeParseException e) // исключение, если задан не правильный формат времени
        {
            return null;
        }
    }
}
