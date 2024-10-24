package org.example.programmjavafx;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.controlsfx.control.CheckComboBox;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.FileChooser;
import org.example.programmjavafx.LoggerLathe.models.ConfigManager;
import org.example.programmjavafx.LoggerLathe.models.MachineLogger;
import org.example.programmjavafx.LoggerLathe.service.FileCreator;
import org.example.programmjavafx.Server.DataBase.ConfigDB;
import org.example.programmjavafx.Server.DataBase.dbService.QuarysForPostgreSQL;
import org.example.programmjavafx.Server.DataBase.dbService.ServiceDB;
import org.example.programmjavafx.Server.DataBase.dbService.ServiceGetDBDataForJavaFX;
import org.example.programmjavafx.Server.MyWebSocketHandler;
import org.example.programmjavafx.Server.QRCodeGenerator.QRCodeGenerator;
import org.example.programmjavafx.Server.WebSocketServer;
import org.example.programmjavafx.Server.entityDB.Event;
import org.example.programmjavafx.Server.entityDB.Message;
import org.example.programmjavafx.Server.interfaces.InterfaceMethods;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.Date;

/** Класс контроллер: "LatheController"
 *  отвечает за графическое окно JavaFX и отображения его элементов в нём
 *  **/
public class LatheController
{
    // region Fields
    private MachineLogger machineLogger;
    private WebSocketServer webSocketServer;
    private final MyWebSocketHandler myWebSocketHandler = new MyWebSocketHandler();
    private final InterfaceMethods.Args args = new InterfaceMethods.Args(InterfaceMethods.Args.plateNumber); // объект Args
    private final ObservableList<Message> messages = FXCollections.observableArrayList(); // специальный элемент списка сообщений, позволяет автоматически обновлять отображение данных в интерфейсе пользователя, при изменении содержимого списка
    private File selectedFile; // Выбранный файл изображения для декодирования
    private final ServiceGetDBDataForJavaFX serviceGetDBDataForJavaFX = new ServiceGetDBDataForJavaFX(); // сервис с набором методов для отображения из БД сообщений в графическое окно
    private final ServiceDB serviceDB = new ServiceDB(); // сервис для работы с БД

    @FXML
    private Button errorButton; // Кнопка для логирования ошибок
    @FXML
    private Button normalWorkButton; // Кнопка для логирования нормальной работы
    @FXML
    private Button startButton; // Кнопка запуска работы станка с новой платой
    @FXML
    private Button endButton; // Кнопка завершения работы
    @FXML
    private Button warningButton; // Кнопка для логирования предупреждений
    @FXML
    private Button selectImageButton; // Кнопка для выбора изображения
    @FXML
    private Button decodeButton; // Кнопка для декодирования изображения
    @FXML
    private TableColumn<Message, String> plateNumberColumn; // колонка для отображения номера платы
    @FXML
    private TableColumn<Message, String> messageColumn; // колонка для отображения сообщений события, типа Message
    @FXML
    private TableColumn<Message, String> typeEventColumn; // колонка для отображения сообщений события, о типе произошедшего события
    @FXML
    private TableColumn<Message, Timestamp> timeEventColumn; // колонка для отображения сообщений о времени события
    @FXML
    private TableView<Message> tableView; // окно отображения данных
    @FXML
    private CheckComboBox<String> checkComboBox; // выпадающий список, с возможностью выбора элементов
    @FXML
    private Button applySelectionButton; // кнопка для CheckComboBox подтверждения выбора режимов работы станка
    @FXML
    private TextField plateNumberInput; // Поле для ввода номера платы
    @FXML
    private Button getDataByPlateNumber; // кнопка для получения сообщений по номеру платы станка
    @FXML
    private Button clearButton; // кнопка очистки всех сообщений
    @FXML
    private Button getMessageFromPeriod; // кнопка получение сообщений по временоому промежутку от...
    @FXML
    private TextField timePeriodFromInput; // окно ввода временного периода от...
    @FXML
    private Button getMessageTillPeriod; // кнопка получения сообщений по временному периоду до...
    @FXML
    private TextField timePeriodTillInput; // окно ввода временного периода до...
    @FXML
    private Button getMessageFromTillPeriod; // кнопка получения сообщений за период от... до...
    @FXML
    private Button applayAllFiltrs; // кнопка получения данных по всем фильтрам одновременно
    @FXML
    private DatePicker dataPicerField; // поле ввода календарной даты
    @FXML
    private Spinner<Integer> hourSpinner; // поле ввода часа
    @FXML
    private Spinner<Integer> minuteSpinner; // поле ввода минут
    @FXML
    private Spinner<Integer> secondSpinner; // поле ввода секунд
    @FXML
    private Button applayDataButton; // кнопка подтверждения выбора выбранного: времени и даты
    @FXML
    private Button cancelDataButton; // кнопка скрытия окна с календарём и часами
    @FXML
    private Label textChooseTimeLable; // надпись: "выбор времени для поиска:"
    @FXML
    private Label textChooseHourLable; // надпись: "выберите час:"
    @FXML
    private Label textChooseMinuteLable; // надпись: "выберите минуты:"
    @FXML
    private Label textChooseSecondLable; // надпись: "выберите секунды:"
    @FXML
    private Label textChooseDataLable; // надпись: "выбор даты для поиска:"
    @FXML
    private Button cleanDataTimeButton; // кнопка очистки даты и времени из выбранного поля ввода
    @FXML
    private Button setCurrentTime; // кнопка установки текущей даты и текущего времени в календаре и часах
    @FXML
    private Button getMessageLastHour; // кнопка получения сообщений за последний час
    @FXML
    private Button getMessageLastDay; // кнопка получения сообщений за последний день
    @FXML
    private Button getMessageLastWeek; // кнопка получения сообщений за последнюю неделю
    @FXML
    private Button getMessageLastMonth; // кнопка получения сообщений за последний месяц
    @FXML
    private Button getMessageLastYear; // кнопка получения сообщений за последний год

    private TextField activeTextField; // поле активного ввода, т.е. через которое производится ввод данных
    // endregion

    // инициализация классов и параметров
    @FXML
    private void initialize()
    {
        // инициализация fileCreator
        FileCreator fileCreator = new FileCreator(new ConfigManager());

        // инициализация MachineLogger, в который передаём fileCreator
        machineLogger = new MachineLogger(fileCreator);

        // инициализация и запуск WebSocket server
        webSocketServer = new WebSocketServer();

        // инициализация и запуск webSocket сервера в отдельном потоке (что бы не мешал работе JavaFX)
        Thread serverThread = new Thread(() ->
        {
            try
            {
                webSocketServer.start(); // запуск webSocketServer
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        });

        serverThread.setDaemon(true); // беспрепятственное завершение работы
                                     // виртуальной машины Java (JVM)
                                    // если все пользовательские потоки завершили
                                   // работу, то JVM завершит свою работу,
                                  // даже если остаются запущенные демон-потоки.
        serverThread.start();    // запуск потока



        // инициализация полей при клике на них мышкой:

        // Скрываем элементы календаря и времени при загрузке
        dataPicerField.setVisible(false);
        hourSpinner.setVisible(false);
        minuteSpinner.setVisible(false);
        secondSpinner.setVisible(false);
        applayDataButton.setVisible(false);
        cancelDataButton.setVisible(false);
        textChooseTimeLable.setVisible(false);
        textChooseHourLable.setVisible(false);
        textChooseMinuteLable.setVisible(false);
        textChooseSecondLable.setVisible(false);
        textChooseDataLable.setVisible(false);
        cleanDataTimeButton.setVisible(false);
        setCurrentTime.setVisible(false);


        // Инициализируем Spinner (часы)
        initializeSpinner(hourSpinner, 0, 23, LocalTime.now().getHour()); // час
        initializeSpinner(minuteSpinner, 0, 59, LocalTime.now().getMinute()); // минута
        initializeSpinner(secondSpinner, 0, 59, LocalTime.now().getSecond()); // секунда
        //initialTime = LocalTime.now();

        // Обработчики для текстовых полей ввода, устанавливаем обработчик событий
        // на клик мышки по выбранному полю
        initializeDateTimePickers(timePeriodFromInput);
        initializeDateTimePickers(timePeriodTillInput);

        // инициализация кнопок всплывающего окна относящегося к календарю и часам
        applayDataButton.setOnAction(event -> applyDateTime()); // обрабатываем кнопку: подтверждения выбора даты и времени в календаре и часах
        cancelDataButton.setOnAction(event -> hideDateTimePicker()); // обрабатываем кнопку скрытия окна
        cleanDataTimeButton.setOnAction(event -> clearFieldInput(activeTextField)); // обрабатываем кнопку очистки полей от даты и времени
        setCurrentTime.setOnAction(actionEvent -> setCurrentTime(hourSpinner, minuteSpinner, secondSpinner)); // обрабатываем кнопку установления текущей даты и времени в календаре и часах

        // для table view: инициализируем колонки для сообщений messageColumn, plateNumberColumn, typeEventColumn, timeEventColumn
        messageColumn.setCellValueFactory(new PropertyValueFactory<>("messageColumn")); // устанавливаем в колонку сообщений в table view
        plateNumberColumn.setCellValueFactory(new PropertyValueFactory<>("plateNumberColumn"));
        typeEventColumn.setCellValueFactory(new PropertyValueFactory<>("typeEventColumn"));
        timeEventColumn.setCellValueFactory(new PropertyValueFactory<>("timeEventColumn"));
        tableView.setItems(messages);

        // инициализируем кнопку "очистки окна" от сообщений и окна ввода номера платы
        clearButton.setOnAction(actionEvent ->
        {
            messages.clear();            // очистка всех сообщений
            plateNumberInput.clear();    // очистка поля ввода номера платы
            timePeriodFromInput.clear(); // очистка поля ввода периода времени от...
            timePeriodTillInput.clear(); // очистка поля ввода периода времени до...
            tableView.getItems().clear();// очистка всех элементов из таблицы table view
        });

        // инициализация кнопки applySelectionButton для выбора отдельных событий работы станка, а также всех событий по умолчанию
        applySelectionButton.setOnAction(actionEvent ->
        {
            choseEventsByCheckComboBox(); // метод выбора событий исходя из выбора с помощью кнопки CheckComboBox

        });

        // инициализация кнопки получения информации всех фильтров
        applayAllFiltrs.setOnAction(actionEvent -> applyAllFilters());


        // инициализация кнопки получения сообщений относящихся к заданному номеру платы
        getDataByPlateNumber.setOnAction(actionEvent ->
        {
            String plateNumber = plateNumberInput.getText(); // считываем номер платы из текстового окна
            getDataByPlateNumber(plateNumber);
        });

        // инициализация кнопки получить все сообщения за ПОСЛЕДНИЙ ЧАС
        getMessageLastHour.setOnAction(actionEvent ->
        {
             String dataTimeEnd = serviceGetDBDataForJavaFX.getCurrentDataTime();
             String dataTimeStart = serviceGetDBDataForJavaFX.getDataTimeMinusHour();
             timePeriodFromInput.setText(dataTimeStart);
             timePeriodTillInput.setText(dataTimeEnd);
             applyAllFilters();

        });

        // инициализация кнопки получить все сообщения за ПОСЛЕДНИЙ ДЕНЬ
        getMessageLastDay.setOnAction(actionEvent ->
        {
            String dataTimeEnd = serviceGetDBDataForJavaFX.getCurrentDataTime();
            String dataTimeStart = serviceGetDBDataForJavaFX.getDataTimeMinusDay();
            timePeriodFromInput.setText(dataTimeStart);
            timePeriodTillInput.setText(dataTimeEnd);
            applyAllFilters();
        });

        // инициализация кнопки получить все сообщения за ПОСЛЕДНЮЮ НЕДЕЛЮ
        getMessageLastWeek.setOnAction(actionEvent ->
        {
            String dataTimeEnd = serviceGetDBDataForJavaFX.getCurrentDataTime();
            String dataTimeStart = serviceGetDBDataForJavaFX.getDataTimeMinusWeek();
            timePeriodFromInput.setText(dataTimeStart);
            timePeriodTillInput.setText(dataTimeEnd);
            applyAllFilters();
        });

        // инициализация кнопки получить все сообщения за ПОСЛЕДНИЙ МЕСЯЦ
        getMessageLastMonth.setOnAction(actionEvent ->
        {
            String dataTimeEnd = serviceGetDBDataForJavaFX.getCurrentDataTime();
            String dataTimeStart = serviceGetDBDataForJavaFX.getDataTimeMinusMonth();
            timePeriodFromInput.setText(dataTimeStart);
            timePeriodTillInput.setText(dataTimeEnd);
            applyAllFilters();
        });

        // инициализация кнопки получить все сообщения за ПОСЛЕДНИЙ ГОД
        getMessageLastYear.setOnAction(actionEvent ->
        {
            String dataTimeEnd = serviceGetDBDataForJavaFX.getCurrentDataTime();
            String dataTimeStart = serviceGetDBDataForJavaFX.getDataTimeMinusYear();
            timePeriodFromInput.setText(dataTimeStart);
            timePeriodTillInput.setText(dataTimeEnd);
            applyAllFilters();
        });

        // инициализация кнопки вывода сообщений по заданному периоду времени от...
        getMessageFromPeriod.setOnAction(actionEvent ->
        {
            String timePeriod = timePeriodFromInput.getText(); // считываем строку из поля TextField

            // метод проверяет: есть ли миллисекунды во времени и возвращает: дату и время с миллисекундами, если они отсутствовали
            timePeriod = checkContainsDataOfMillisecond(timePeriod);

            // Конвертация строки в тип Timestamp
            Timestamp timestampPeriod = serviceDB.convertStringToTimestampWithMilisecond(timePeriod);

            if (timestampPeriod != null)
            {
                List<String[]> messages = serviceGetDBDataForJavaFX.getDataByPeriod(timestampPeriod, QuarysForPostgreSQL.QUERY_GET_EVENT_TIME_FROM); // получаем данные из базы данных и помещаем их в список сообщений
                ObservableList<Message> newMessages = FXCollections.observableArrayList(); // связывание данных с пользовательским интерфейсом
                for (String[] row : messages) // идём по списку сообщений
                {
                    newMessages.add(new Message(row[0], row[1], row[2], Timestamp.valueOf(row[3]))); // добавляем их в специальный тип списка, который позволяет отслеживать изменения в его элементах и автоматически обновлять отображение данных в интерфейсе
                }
                tableView.setItems(newMessages); // устанавливает список элементов (ObservableList) в TableView
            }
            else
            {
                ObservableList<Message> errorMessages = FXCollections.observableArrayList();
                errorMessages.add(new Message("Ошибка", "Некорректный формат времени. Используйте формат 'yyyy-MM-dd HH:mm:ss[.fffffffff]'.", "Ошибка", null));
                tableView.setItems(errorMessages);
            }
        });

        // инициализация кнопки получения сообщений по заданному периоду до...
        getMessageTillPeriod.setOnAction(actionEvent ->
        {
            String timePeriod = timePeriodTillInput.getText(); // считываем строку из поля TextField

            // метод проверяет: есть ли миллисекунды во времени и возвращает: дату и время с миллисекундами, если они отсутствовали
            timePeriod = checkContainsDataOfMillisecond(timePeriod);

            // Конвертация строки в тип Timestamp
            Timestamp timestampPeriod = serviceDB.convertStringToTimestampWithMilisecond(timePeriod); // Конвертация строки в тип Timestamp

            if (timestampPeriod != null)
            {
                List<String[]> data = serviceGetDBDataForJavaFX.getDataByPeriod(timestampPeriod, QuarysForPostgreSQL.QUERY_GET_EVENT_TIME_TILL); // получаем данные из базы данных и помещаем их в список сообщений

                ObservableList<Message> newMessages = FXCollections.observableArrayList(); // связывание данных с пользовательским интерфейсом

                for (String[] row : data) // идём по списку сообщений
                {
                    newMessages.add(new Message(row[0], row[1], row[2], Timestamp.valueOf(row[3])));
                }
                tableView.setItems(newMessages); // устанавливает список элементов (ObservableList) в TableView
            }
            else
            {
                ObservableList<Message> errorMessages = FXCollections.observableArrayList();
                errorMessages.add(new Message("Ошибка", "Некорректный формат времени. Используйте формат 'yyyy-MM-dd HH:mm:ss[.fffffffff]'.", "Ошибка", null));
                tableView.setItems(errorMessages);
            }
        });

        // инициализация кнопки получения сообщений за период времени от... и до...
        getMessageFromTillPeriod.setOnAction(actionEvent ->
        {
            String timePeriodFrom = timePeriodFromInput.getText(); // берём период от заданного значения

            // метод проверяет: есть ли миллисекунды во времени и возвращает: дату и время с миллисекундами, если они отсутствовали
            timePeriodFrom = checkContainsDataOfMillisecond(timePeriodFrom);

            // Конвертация строки в тип Timestamp
            Timestamp timestampStart = serviceDB.convertStringToTimestampWithMilisecond(timePeriodFrom); // переводим в Timestamp

            String timePeriodTill = timePeriodTillInput.getText(); // берём период до заданного значения

            // метод проверяет: есть ли миллисекунды во времени и возвращает: дату и время с миллисекундами, если они отсутствовали
            timePeriodTill = checkContainsDataOfMillisecond(timePeriodTill);

            // Конвертация строки в тип Timestamp
            Timestamp timestampEnd = serviceDB.convertStringToTimestampWithMilisecond(timePeriodTill);  // переводим в Timestamp

            if (timestampStart != null && timestampEnd != null)
            {
                List<String[]> data = serviceGetDBDataForJavaFX.getDataByPeriodFromTill(timestampStart, timestampEnd, QuarysForPostgreSQL.QUERY_GET_EVENT_TIME_BEETWEN); // получаем данные из базы данных и помещаем их в список сообщений

                ObservableList<Message> newMessages = FXCollections.observableArrayList();

                for (String[] row : data)
                {
                    newMessages.add(new Message(row[0], row[1], row[2], Timestamp.valueOf(row[3])));
                }
                tableView.setItems(newMessages);
            }
            else
            {
                ObservableList<Message> errorMessages = FXCollections.observableArrayList();
                errorMessages.add(new Message("Ошибка", "Некорректный формат времени. Используйте формат 'yyyy-MM-dd HH:mm:ss[.fffffffff]'.", "ошибка", null));
                tableView.setItems(errorMessages);
            }
        });

        /**
         * вешаем на кнопки графического окна разную логику:
         * **/

        // кнопка вывода и записи начала работы станка с новой платой
        startButton.setOnAction(actionEvent ->
        {
            String plateNumber = args.getPlateNumber(); // получаем номер платы
            System.out.println("вывод номера платы plateNumber: " + plateNumber); // выводим номер платы в консоль для проверки

            if (plateNumber != null)
            {

                File foundFile = getFileByPlateNumberAndWriteNewInformation(plateNumber); // получаем файл с заданным номером платы
                if (foundFile != null)
                {
                    plateNumberInput.setText(plateNumber); // добавляем текущий номер платы, с которой работает станок, в окно поле ввода номера платы
                    machineLogger.startNewPlate(plateNumber); // запускаем метод начала работы с платой

                    MyWebSocketHandler.broadcastMessage("Запуск новой платы: в станок загружена новая плата: " + plateNumber); // делаем рассылку сообщения клиентам

                    try // Создание и сохранение QR-кода для номера платы
                    {
                        String directoryPath = "C:" + File.separator + "Expert Group" + File.separator + "картинки"; // путь к сохранённой директории
                        String filePath = directoryPath + File.separator + plateNumber + ".png"; // путь к файлу с уникальным именем

                        Path path = FileSystems.getDefault().getPath(filePath); // Проверка, существует ли файл с таким именем
                        if (Files.exists(path)) // если файл существует:
                        {
                            // Если файл существует, добавляем временную метку к имени файла
                            String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
                            filePath = directoryPath + File.separator + plateNumber + "_" + timestamp + ".png";
                        }
                        QRCodeGenerator.generateQRCodeImage(plateNumber, 350, 350, filePath);
                        System.out.println("Картинка с QR-Code успешно создана!: " + filePath);
                    }
                    catch (WriterException | IOException e)
                    {
                        e.printStackTrace();
                    }
                }
                else
                {
                    myWebSocketHandler.broadcastMessage("Файл с таким номером платы не найден в логах! Берём номер платы из сообщения от клиента!");

                    String clientPlateNumber = MyWebSocketHandler.getClientMessage(); // получаем номер платы из сообщения клиента
                    System.out.println("установили номер платы из клиентского сообщения clientPlateNumber: " + clientPlateNumber);

                    if (clientPlateNumber != null && !clientPlateNumber.isEmpty())
                    {
                        System.out.println("От Клиента получена новая плата: " + clientPlateNumber);
                        machineLogger.startNewPlate(clientPlateNumber);
                        MyWebSocketHandler.broadcastMessage("Запуск новой платы: в станок загружена новая плата: " + clientPlateNumber);


                        try  // Создать и сохранить QR-код для номера платы
                        {
                            String directoryPath = "C:" + File.separator + "Expert Group" + File.separator + "картинки"; // путь к сохранённой директории
                            String filePath = directoryPath + File.separator + clientPlateNumber + ".png";  // путь к файлу с уникальным именем
                            QRCodeGenerator.generateQRCodeImage(clientPlateNumber, 350, 350, filePath);
                            System.out.println("QR Code image created successfully: " + filePath);
                        }
                        catch (WriterException | IOException e)
                        {
                            e.printStackTrace();
                        }
                    }
                    else
                    {
                        System.out.println("Не задан номер платы boardNumber!");
                        MyWebSocketHandler.broadcastMessage("станку незадан номер платы: " + plateNumber); // делаем рассылку сообщения клиентам
                    }
                }
            }
        });

        // кнопка вывода и записи нормальной работы станка с платой
        normalWorkButton.setOnAction(actionEvent ->
        {
            if (machineLogger.isPlateInstalled()) // проверяем, установлена ли плата?
            {
                String plateNumber = args.getPlateNumber(); // получаем номер платы

                if (plateNumber != null)
                {
                    System.out.println("печатаем  plateNumber из метода normalWorkButton " + plateNumber);
                    try {
                        machineLogger.logNormalOperation(plateNumber, "Normal operation log");
                    }
                    catch (SQLException e)
                    {
                        System.out.println("plateNumber равен null");
                        throw new RuntimeException(e);
                    }
                }
                // послать сообщение клиенту
                MyWebSocketHandler.broadcastMessage("нормальная работа станка: идёт нормальная работа станка, загружена плата номер: " + plateNumber);
            }
        });

        // кнопка вывода и записи предупреждений о возможных неполадках в работе станка
        warningButton.setOnAction(actionEvent ->
        {
            if (machineLogger.isPlateInstalled()) // проверяем, установлена ли плата?
            {
                String plateNumber = args.getPlateNumber();
                machineLogger.logWarning(plateNumber, "Warning log");
                // послать сообщение клиенту
                MyWebSocketHandler.broadcastMessage("предупреждение: станок работает с проблемами, загружена плата номер: " + plateNumber);
            }
        });

        // кнопка вывода и записи ошибок, произошедших в течении работы станка с платой
        errorButton.setOnAction(actionEvent ->
        {
            if (machineLogger.isPlateInstalled()) // проверяем, установлена ли плата?
            {
                String plateNumber = args.getPlateNumber();
                machineLogger.logError(plateNumber, "Error log");
                // послать сообщение клиенту
                MyWebSocketHandler.broadcastMessage("ошибка: произошла ошибка при работе станка, загружена плата номер: " + plateNumber);
            }
        });

        // кнопка завершения работы станка с платой
        endButton.setOnAction(actionEvent ->
        {
            if (machineLogger.isPlateInstalled()) // проверяем, установлена ли плата?
            {
                String plateNumber = args.getPlateNumber();
                machineLogger.endPlateOperation("Операция по работе с платой завершена!" + " Время завершения операции с платой: ", plateNumber);
                // послать сообщение клиенту
                MyWebSocketHandler.broadcastMessage("Завершение работы: произошло корректное завершение работы станка, загруженная плата номер: " + plateNumber);
            }
        });

        // Кнопка выбора изображения (выбрать QR-code)
        selectImageButton.setOnAction(actionEvent ->
        {
            // диалоговое окно для выбора файлов из файловой системы
            FileChooser fileChooser = new FileChooser();

            // список фильтров расширений, которые применяются к диалоговому окну выбора файлов.
            fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.bmp"));

            // возвращает объект File, представляющий выбранный пользователем файл
            selectedFile = fileChooser.showOpenDialog(selectImageButton.getScene().getWindow());
        });

        // Кнопка декодирования изображения (декодировать QR-code)
        decodeButton.setOnAction(actionEvent ->
        {
            if (selectedFile != null)
            {
                try
                {
                    String decodedText = decodeQRCode(selectedFile);
                    if (decodedText != null)
                    {
                        plateNumberInput.setText(decodedText);
                        args.setPlateNumber(decodedText);
                    }
                    else
                    {
                        plateNumberInput.setText("нет QR-code в изображении");
                    }
                }
                catch (IOException e)
                {
                    plateNumberInput.setText("Не возможно декодировать QR Code: " + e.getMessage());
                }
            }
            else
            {
                plateNumberInput.setText("Ни один файл не выбран");
            }
        });

    } // <--- Закрывающая скобка для метода initialize

    /**
     * метод декодер
     * декодирует QR-code изображение в строку,
     * т.е. извлекает из изображение номер платы, с которой будет работать станок
     **/
    private String decodeQRCode(File qrCodeImage) throws IOException
    {
        //  чтение изображения из файла и создания объекта BufferedImage.
        BufferedImage bufferedImage = ImageIO.read(qrCodeImage);

        // Создается объект BufferedImageLuminanceSource, который используется для преобразования изображения в источник светимости, ufferedImageLuminanceSource преобразует цветное изображение в черно-белое, что упрощает процесс декодирования
        BufferedImageLuminanceSource source = new BufferedImageLuminanceSource(bufferedImage);

        // Создается объект BinaryBitmap, который используется библиотекой ZXing для представления бинарного изображения, В качестве параметра используется HybridBinarizer, который применяет алгоритм для улучшения качества бинарного изображения
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
        try
        {
            // Создается объект MultiFormatReader, который пытается декодировать различные форматы штрих-кодов, включая QR-коды.
            // Метод decode(BinaryBitmap) пытается декодировать QR-код и возвращает объект Result, содержащий информацию о декодированном штрих-коде.
            Result result = new MultiFormatReader().decode(bitmap);
            // из объекта Result извлекается текст с помощью result.getText() и возвращается
            return result.getText();
        }
        catch (Exception e)
        {
            return null;
        }
    }

    /**
     * метод получения webSocketServer, для доступа в других классах
     * (в частности, для вызова в классе HelloApplication)
     **/
    public WebSocketServer getWebSocketServer()
    {
        return webSocketServer;
    }

    /**
     * метод получения данных, а именно:
     * нахождения лог-файла с конкретным номером платы
     * и до записи в него новой информации
     **/
    public static File getFileByPlateNumberAndWriteNewInformation(String plateNumber)
    {
        // Путь к директории с лог-файлами
        String logDirectoryPath = "C:" + File.separator + "Expert Group" + File.separator + "JavaFX" + File.separator + "ProgrammJavaFX";
        Path logDirPath = Paths.get(logDirectoryPath);

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(logDirPath, "*_" + plateNumber + ".txt"))
        {
            for (Path entry : stream)
            {
                File foundFile = entry.toFile();
                System.out.println("Файл найден: " + foundFile);
                return foundFile; // возвращает найденный файл по заданному номеру платы
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        System.out.println("!!!!!!!!!файл не найден !!!!");
        return null;
    }

    /**
     * промежуточный метод получения данных по номеру платы
     **/
    private void getDataByPlateNumber(String plateNumber)
    {
        // Получение данных по номеру платы
        List<Event> events = serviceGetDBDataForJavaFX.getEventsByPlateNumber(plateNumber, QuarysForPostgreSQL.QUERY_GET_PLATE_NUMBER); // получение из базы данных запрашиваемой информации
        ObservableList<Message> newMessages = FXCollections.observableArrayList();
        if (events.isEmpty()) // если список событий пустой
        {
            newMessages.add(new Message("Некорректный номер платы", "Вы некорректно ввели номер платы, либо платы с таким номеров не существует в базе данных!", "ошибка ввода номера платы", new Timestamp(new Date().getTime())));
        }
        else
        {
            for (Event event : events)
            {
                newMessages.add(new Message(plateNumber, event.getEventMessage(),
                        event.getEventType(), event.getEventTime()));
            }
        }
        tableView.setItems(newMessages);
    }

    /**
     * метод для работы с CheckComboBox (по отмеченным событиям в выборке)
     **/
    private void choseEventsByCheckComboBox()
    {
        // получает наблюдаемый список выбранных элементов из комбинированного списка с флажками (CheckComboBox)
        ObservableList<String> checkedItems = checkComboBox.getCheckModel().getCheckedItems();

        if (checkedItems.isEmpty()) // Если нет выбранных элементов
        {
            // Если не выбрано ни одного элемента, добавляем все элементы по умолчанию
            selectionOfLatheOperationModes("start_work");   // Добавление события "начало работы"
            selectionOfLatheOperationModes("normal_work");  // Добавление события "нормальная работа"
            selectionOfLatheOperationModes("error_work");   // Добавление события "ошибка"
            selectionOfLatheOperationModes("warning_work"); // Добавление события "предупреждение"
            selectionOfLatheOperationModes("end_work");     // Добавление события "конец работы"
        }
        else // если список не пустой, то:
        {
            for (String item : checkedItems) // итерация по выбранным элементам списка (по событиям)
            {
                if (item != null)
                {
                    selectionOfLatheOperationModes(item); // добавление выбранного события
                }
                else
                {
                    ObservableList<Message> errorMessages = FXCollections.observableArrayList();
                    errorMessages.add(new Message("Ошибка", "Сначала очистите список кнопкой, затем делайте выбор режима работы! ", "Ошибка", null));
                    tableView.setItems(errorMessages);
                }
            }
        }
        tableView.setItems(messages); // Обновление таблицы с новыми сообщениями
    }

    /**
     * промежуточный метод получения данных о различных режимах работы станка
     * (используется в методе choseEventsByCheckComboBox())
     **/
    private void selectionOfLatheOperationModes(String item) // принимаем в качестве параметра тип работы станка
    {
        if (item == null)
        {
            throw new IllegalArgumentException("выбор не может быть равен null");
        }
        switch (item) // если параметр равен, одному из типов работы станка, то реализуем соответствующую логику получения соответствующих данных
        {
            case "start_work":
                List<String[]> startWorkEventMessages = serviceGetDBDataForJavaFX.getEventMessages("Начало работы с новой платой.", QuarysForPostgreSQL.QUERY_GET_EVENT_TYPE); // метод получения данных из базы данных, по запросу "start_work"
                for (String[] row : startWorkEventMessages)
                {
                    messages.add(new Message(row[0], row[1], row[2], Timestamp.valueOf(row[3]))); // добавляем их в специальный тип списка, который позволяет отслеживать изменения в его элементах и автоматически обновлять отображение данных в интерфейсе
                }
                break;
            case "normal_work":
                List<String[]> normalWorkEventMessages = serviceGetDBDataForJavaFX.getEventMessages("Нормальная работа станка.", QuarysForPostgreSQL.QUERY_GET_EVENT_TYPE); // метод получения данных из базы данных, по запросу "normalWork"
                for (String[] row : normalWorkEventMessages)
                {
                    messages.add(new Message(row[0], row[1], row[2], Timestamp.valueOf(row[3]))); // добавляем их в специальный тип списка, который позволяет отслеживать изменения в его элементах и автоматически обновлять отображение данных в интерфейсе
                }
                break;
            case "error_work":
                List<String[]> errorWorkEventMessages = serviceGetDBDataForJavaFX.getEventMessages("Ошибка при работе станка!", QuarysForPostgreSQL.QUERY_GET_EVENT_TYPE); // метод получения данных из базы данных, по запросу "errorWork"
                for (String[] row : errorWorkEventMessages)
                {
                    messages.add(new Message(row[0], row[1], row[2], Timestamp.valueOf(row[3]))); // добавляем их в специальный тип списка, который позволяет отслеживать изменения в его элементах и автоматически обновлять отображение данных в интерфейсе
                }
                break;
            case "warning_work":
                List<String[]> warningWorkEventMessages = serviceGetDBDataForJavaFX.getEventMessages("Предупреждение о возможной проблеме!", QuarysForPostgreSQL.QUERY_GET_EVENT_TYPE); // метод получения данных из базы данных, по запросу "warningWork"
                for (String[] row : warningWorkEventMessages)
                {
                    messages.add(new Message(row[0], row[1], row[2], Timestamp.valueOf(row[3]))); // добавляем их в специальный тип списка, который позволяет отслеживать изменения в его элементах и автоматически обновлять отображение данных в интерфейсе
                }
                break;
            case "end_work":
                List<String[]> endWorkEventMessages = serviceGetDBDataForJavaFX.getEventMessages("Завершение работы с платой.", QuarysForPostgreSQL.QUERY_GET_EVENT_TYPE); // метод получения данных из базы данных, по запросу "endWork"
                for (String[] row : endWorkEventMessages)
                {
                    messages.add(new Message(row[0], row[1], row[2], Timestamp.valueOf(row[3]))); // добавляем их в специальный тип списка, который позволяет отслеживать изменения в его элементах и автоматически обновлять отображение данных в интерфейсе
                }
                break;
        }
    }

    /**
     * Метод получения сообщений по всем фильтрам одновременно
     **/
    private void applyAllFilters()
    {
        // получение данных из полей вода:
        String plateNumber = plateNumberInput.getText(); // получение plateNumber из поля plateNumberInput
        String timePeriodFrom = timePeriodFromInput.getText(); // получение timePeriodFrom из поля timePeriodFromInput
        String timePeriodTill = timePeriodTillInput.getText(); // получение timePeriodTill из поля timePeriodTillInput
        ObservableList<String> selectedItems = checkComboBox.getCheckModel().getCheckedItems(); // получаем все выбранные элементы checkComboBox

        // вводим переменные, для метода, который преобразует строки в Timestamp
        Timestamp fromTimestamp = null; // время от...
        Timestamp tillTimestamp = null; // время до...
        boolean hasError = false; // устанавливаем флаг false для дальнейшего поиска

        StringBuilder errorMessage = new StringBuilder("Некорректно введёно: "); // список ошибок


        // проверка пустой ли список сообщений и выводим ошибку
        if (messages.isEmpty())
        {
            // формируем ответ об ошибке в колонки таблицы table view:
            ObservableList<Message> newMessages = FXCollections.observableArrayList(); // Создание новой пустой ObservableList для хранения объектов Message.
            newMessages.add(new Message("Нет данных", "Нет данных по заданному фильтру", "нет данных", new Timestamp(new Date().getTime()))); // заполняем созданный объект для отображения в окне Tableview
            tableView.setItems(newMessages); // выводим ошибку в окно tableview
        }

        // проверки временных параметров типа: String на преобразования их в тип: Timestamp
        try
        {
            // проверка timePeriodFrom на конвертацию в Timestamp
            fromTimestamp = convertToTimestamp(timePeriodFrom);
        }
        catch (IllegalArgumentException e)
        {
            hasError = true; // в случае ошибки, устанавливаем флаг равный: true
            errorMessage.append("время 'от', "); // добавление конкретной ошибки в список ошибок
        }

        try
        {
            // проверка timePeriodTill на конвертацию в Timestamp
            tillTimestamp = convertToTimestamp(timePeriodTill);
        }
        catch (IllegalArgumentException e)
        {
            hasError = true; // в случае ошибки, устанавливаем флаг равный: true
            errorMessage.append("время 'до', "); // добавление конкретной ошибки в список ошибок
        }

        // Если есть ошибка, т.е. флаг hasError = true, то вывести сообщение в tableView и завершить выполнение
        if (hasError)
        {
            ObservableList<Message> newMessages = FXCollections.observableArrayList(); // Создание новой пустой ObservableList для хранения объектов Message.
            newMessages.add(new Message("Ошибка", errorMessage.toString(), "ошибка", new Timestamp(new Date().getTime()))); // заполняем созданный объект для отображения в окне Tableview
            tableView.setItems(newMessages); // отображаем в окне информацию об ошибке
            return;
        }

        // создаём sql-запрос с помощью метода: buildQueryWithSelectedItems
        String query = buildQueryWithSelectedItems(plateNumber, fromTimestamp, tillTimestamp, selectedItems);

        // создаём список выбранных значений с помощью метода: buildParamsList
        List<Object> params = buildParamsList(plateNumber, fromTimestamp, tillTimestamp, selectedItems);

        executeQueryMethod(query, params); // устанавливаем выбранные значения и выполняем запрос к БД с помощью метода: executeQueryMethod
    }

    // метод для преобразования строки в Timestamp
    private Timestamp convertToTimestamp(String timeString)
    {
        // пытаемся преобразовать строку в тип Timestamp
        try
        {
            if (timeString != null && !timeString.isEmpty())
            {
                return Timestamp.valueOf(timeString); // если получилось преобразовать, то возвращаем преобразованное значение
            }
        }
        // если не удалось преобразовать, то выбрасываем исключение об ошибке
        catch (IllegalArgumentException e)
        {
            throw new IllegalArgumentException("Некорректный формат времени: " + timeString);
        }
        return null;
    }

    // метод конструирует SQL - запрос, подстраивая его под выбираемые параметры фильтра
    private String buildQueryWithSelectedItems(String plateNumber, Timestamp fromTimestamp, Timestamp tillTimestamp, ObservableList<String> selectedItems)
    {
        // формируем базовую часть запроса
        StringBuilder query = new StringBuilder("SELECT lp.plate_number, e.event_message, e.event_type, e.event_time FROM events e JOIN lathe_plates lp ON e.plate_id = lp.id WHERE 1=1");

        /** добавляем в базовую часть запроса необходимую часть, для получения выбранного параметра из БД **/
        // если в списке выбранных элементов есть plateNumber, то:
        if (plateNumber != null && !plateNumber.isEmpty())
        {
            query.append(" AND lp.plate_number = ?"); // добавляем поиск по номеру платы
        }
        // если в списке выбранных элементов есть fromTimestamp, то:
        if (fromTimestamp != null)
        {
            query.append(" AND e.event_time >= ?"); //добавляем поиск по временному промежутку (>=) от...
        }
        // если в списке выбранных элементов есть tillTimestamp, то:
        if (tillTimestamp != null)
        {
            query.append(" AND e.event_time <= ?"); //добавляем поиск по временному промежутку (<=) до...
        }

        // если список выбранных элементов CheckComboBox не пустой, то строим запрос по типам событий:
        if (selectedItems != null && !selectedItems.isEmpty())
        {
            query.append(" AND e.event_type IN ("); //добавляем поиск среди выбранных значений событий

            for (int i = 0; i < selectedItems.size(); i++) // итерируемся по списку выбранных элементов CheckComboBox
            {
                query.append("?"); // добавляем ?  под каждое значение из списка
                if (i < selectedItems.size() - 1) // проверяем, является ли текущий элемент не последним в списке
                {
                    query.append(", "); // добавляем запятую после каждого вопроса (?,) если элемент не последний в списке
                }
            }
            query.append(")"); // добавляем закрытие кавычек и скобки в конце для закрытия запроса

            System.out.println("итоговый запрос query: " + query); // печатаем для проверки итоговый запрос
        }
        return query.toString(); // возвращаем запрос в виде строки
    }

    // метод создаёт список с элементами, удовлетворяющие условиям фильтра, установленных в графическом окне JavaFX
    private List<Object> buildParamsList(String plateNumber, Timestamp fromTimestamp, Timestamp tillTimestamp, ObservableList<String> selectedItems)
    {

        Map<String, String> eventTypeMapping = new HashMap<>(); // создаём словарь ключ(типы событий) / значение (типа события из колонки event_type)

        // у каждого события своё сообщение о событие, ключ - событие, значение - сообщение о событие
        eventTypeMapping.put("start_work", "Начало работы с новой платой.");            //помещаем в словарь: ключ / значение
        eventTypeMapping.put("normal_work", "Нормальная работа станка.");              // помещаем в словарь: ключ / значение
        eventTypeMapping.put("error_work", "Ошибка при работе станка!");              //  помещаем в словарь: ключ / значение
        eventTypeMapping.put("warning_work", "Предупреждение о возможной проблеме!");//   помещаем в словарь: ключ / значение
        eventTypeMapping.put("end_work", "Завершение работы с платой.");            //    помещаем в словарь: ключ / значение

        // создаём список, куда будем помещать значения выбранных элементов
        List<Object> params = new ArrayList<>();

        /** добавляем параметры в список, т.е. те значения которые выбраны в фильтре **/
        // если номер платы есть в списке и не равен нулю, то:
        if (plateNumber != null && !plateNumber.isEmpty())
        {
            params.add(plateNumber); // помещаем значение plateNumber в список значений выбранных элементов
        }
        // если значение временного промежутка ОТ.. есть в списке и не равен нулю, то:
        if (fromTimestamp != null)
        {
            params.add(fromTimestamp); // помещаем значение fromTimestamp в список значений выбранных элементов
        }
        // если значение временного промежутка ДО.. есть в списке и не равен нулю, то:
        if (tillTimestamp != null)
        {
            params.add(tillTimestamp); // помещаем значение tillTimestamp в список значений выбранных элементов
        }
        // если список выбранных элементов CheckComboBox не пустой и не равен нулю, то:
        if (selectedItems != null && !selectedItems.isEmpty())
        {
            for (String item : selectedItems) // идём по списку выбранных элементов CheckComboBox
            {
                params.add(eventTypeMapping.get(item)); // добавляем значения выбранных элементов CheckComboBox в список
            }
        }
        return params; // возвращаем список с выбранными элементами
    }

    // метод выполняет SQL - запрос и устанавливает значения заданным параметрам в соответствие со списком выбранных значений
    private void executeQueryMethod(String query, List<Object> params)
    {
        try
        {
            Connection connection = ConfigDB.getConnection(); // создаём соединение с БД
            PreparedStatement pstmt = connection.prepareStatement(query); // формируем параметроризированный запрос к БД

            for (int i = 0; i < params.size(); i++) // идём по списку с параметрами
            {
                if (params.get(i) instanceof Timestamp) // если параметр имеет тип Timestamp, то:
                {
                    pstmt.setTimestamp(i + 1, (Timestamp) params.get(i)); // добавляем в запрос значение типа Timestamp
                }
                else // иначе добавляем как String
                {
                    pstmt.setString(i + 1, (String) params.get(i)); // добавляем в запрос значение типа String
                }
            }

            System.out.println("итоговый запрос pstmt: " + pstmt); // вывод для проверки правильности подстановки значений в итоговый запрос

            ResultSet rs = pstmt.executeQuery(); // формируем объект ResultSet как результат исполнения результирующего запроса к БД

            List<String[]> listOfMessages = serviceGetDBDataForJavaFX.processResultSet(rs); // с помощью метода получаем сообщения из колонок результата объекта Resultset

            addMessagesToTable(listOfMessages); // отображаем в таблице tableview результата запроса
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    // метод отображения сообщений по колонкам в соответствие с объектом класса Message
    public void addMessagesToTable(List<String[]> data)
    {
        ObservableList<Message> newMessages = FXCollections.observableArrayList(); // создаём список полученных сообщений
        for (String[] row : data) // итерируемся по списку по каждой колонке
        {
            newMessages.add(new Message(row[0], row[1], row[2], Timestamp.valueOf(row[3]))); // помещаем массивы из сообщений по соответствующим колонкам
        }
        tableView.setItems(newMessages); // отображаем сообщения в окне tableview
    }


    // метод проверяет, содержится в формате даты и времени миллисекунды, если нет то добавляет их
    private String checkContainsDataOfMillisecond(String timePeriod)
    {
        System.out.println("timePeriod до: " + timePeriod);
        // Проверяем, есть ли миллисекунды во времени т.е. точка содержится ли в тексте
        boolean hasMilliseconds = timePeriod.contains(".");

        // Если миллисекунды отсутствуют, добавляем .000000
        if (!hasMilliseconds)
        {
            timePeriod += ".000000";
        }
        System.out.println("timePeriod после: " + timePeriod);
        return timePeriod;
    }


    // инициализация часов
    private void initializeSpinner(Spinner<Integer> spinner, int min, int max, int initialValue)
    {
        spinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(min, max, initialValue));
        spinner.setEditable(true); // Для возможности ввода значения вручную
    }

    // метод принимает TextField (т.е. поле ввода данных) в качестве аргумента
    // и инициализирует для него обработчик событий для щелчка мыши
    private void initializeDateTimePickers(TextField textField)
    {
        // устанавливает обработчик событий для мыши
        textField.setOnMouseClicked(event ->
        {
            activeTextField = textField; // сохраняет ссылку на текущий textField  в переменную activeTextField
            showDateTimePicker(); // делаем видимыми календарь и часы
        });
    }

    // метод делает видимыми элементы календаря и часов
    private void showDateTimePicker()
    {
        dataPicerField.setVisible(true);
        hourSpinner.setVisible(true);
        minuteSpinner.setVisible(true);
        secondSpinner.setVisible(true);
        textChooseTimeLable.setVisible(true);
        textChooseHourLable.setVisible(true);
        textChooseMinuteLable.setVisible(true);
        textChooseSecondLable.setVisible(true);
        applayDataButton.setVisible(true);
        cancelDataButton.setVisible(true);
        textChooseDataLable.setVisible(true);
        cleanDataTimeButton.setVisible(true);
        setCurrentTime.setVisible(true);

        hourSpinner.getValueFactory().setValue(LocalTime.now().getHour());
        minuteSpinner.getValueFactory().setValue(LocalTime.now().getMinute());
        secondSpinner.getValueFactory().setValue(LocalTime.now().getSecond());
    }

    // метод подтверждения выбора для кнопки applayDataButton, подтверждает выбор времени и даты с календаря и часов
    private void applyDateTime()
    {
        if (activeTextField != null)
        {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDate selectedDate = dataPicerField.getValue(); // поле ввода календарной даты
            int selectedHour = hourSpinner.getValue(); // поле ввода часа
            int selectedMinute = minuteSpinner.getValue(); // поле ввода минут
            int selectedSecond = secondSpinner.getValue(); // поле ввода секунд
            LocalDateTime selectedDateTime = LocalDateTime.of(selectedDate, LocalTime.of(selectedHour, selectedMinute, selectedSecond)); // создаём общую дату и время
            String dateTime = selectedDateTime.format(formatter); // форматируем дату под нужный заданный шаблон
            activeTextField.setText(dateTime); // устанавливаем в поле ввода отформатированную дату и время
            hideDateTimePicker(); // прячем календарь и часы
        }
    }

    // метод делает элементы невидимыми
    private void hideDateTimePicker()
    {
        dataPicerField.setVisible(false);
        hourSpinner.setVisible(false);
        minuteSpinner.setVisible(false);
        secondSpinner.setVisible(false);
        textChooseTimeLable.setVisible(false);
        textChooseHourLable.setVisible(false);
        textChooseMinuteLable.setVisible(false);
        textChooseSecondLable.setVisible(false);
        applayDataButton.setVisible(false);
        cancelDataButton.setVisible(false);
        textChooseDataLable.setVisible(false);
        cleanDataTimeButton.setVisible(false);
        setCurrentTime.setVisible(false);
    }

    // метод очищает окно ввода
    private void clearFieldInput(TextField activeTextField)
    {
        timePeriodFromInput.clear();
        timePeriodTillInput.clear();
        activeTextField.clear();
    }

    // метод устанавливает текущую время и дату в календаре и часах
    private void setCurrentTime(Spinner<Integer> hourSpinner, Spinner<Integer> minuteSpinner, Spinner<Integer> secondSpinner)
    {
        LocalDateTime timeNow = LocalDateTime.now();
        hourSpinner.getValueFactory().setValue(timeNow.getHour());
        minuteSpinner.getValueFactory().setValue(timeNow.getMinute());
        secondSpinner.getValueFactory().setValue(timeNow.getSecond());

        LocalDate selectedDate = LocalDate.of(timeNow.getYear(), timeNow.getMonth(), timeNow.getDayOfMonth());
        dataPicerField.setValue(selectedDate);
    }

} // <-- Закрывающая скобка для класса LatheController



















