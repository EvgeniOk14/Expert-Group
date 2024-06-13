package org.example.programmjavafx;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import org.example.programmjavafx.LoggerLathe.models.ConfigManager;
import org.example.programmjavafx.LoggerLathe.models.MachineLogger;
import org.example.programmjavafx.LoggerLathe.service.FileCreator;
import org.example.programmjavafx.Server.MyWebSocketHandler;
import org.example.programmjavafx.Server.WebSocketServer;

/**
 * Класс контроллера, который описывает связь графических объектов с бизнес-логикой
 * **/
public class LatheController
{
    // region Fields
    private MachineLogger machineLogger;
    private WebSocketServer webSocketServer;
    private final MyWebSocketHandler myWebSocketHandler = new MyWebSocketHandler();
    // endregion

    @FXML
    private Button errorButton; // Кнопка для логирования ошибок

    @FXML
    private Button normalWorkButton; // Кнопка для логирования нормальной работы

    @FXML
    private Button startButton; // Кнопка запуска работы станка с новой платой

    @FXML
    private Button endButton; // Кнопка завершения работы

    @FXML
    private Button worningButton; // Кнопка для логирования предупреждений

    @FXML
    private void initialize()
    {
        // инициализация fileCreator
        FileCreator fileCreator = new FileCreator(new ConfigManager());

        // инициализация MachineLogger, в который передаём fileCreator
        machineLogger = new MachineLogger(fileCreator);

        // инициализация и запуск WebSocket server
        webSocketServer = new WebSocketServer();

        // запуск webSocket сервера в одтдельном потоке (что бы не мешал работе JavaFX)
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

        serverThread.setDaemon(true); //  без препятственное завершение работы
                                     //   виртуальной машины Java (JVM)
                                    //    если все пользовательские потоки завершили
                                   //     работу, то JVM завершит свою работу,
                                  //      даже если остаются запущенные демон-потоки.
        serverThread.start(); // запуск потока

        /** вешаем на кнопки графического окна разную логику:  **/

        // кнопка запуска станка с новой платой
        startButton.setOnAction(actionEvent ->
        {

            String plateNumber = myWebSocketHandler.getPlateNumber();

            if (plateNumber != null)
            {
                machineLogger.startNewPlate(plateNumber);
                // послать сообщение клиенту
                MyWebSocketHandler.broadcastMessage("Запуск новой платы: в станок загружена новая плата: " + plateNumber);
            }
            else
            {
                System.out.println("Не задан номер платы boardNumber!");
                // послать сообщение клиенту
                MyWebSocketHandler.broadcastMessage("станку незадан номер платы: " + plateNumber);
            }
        });

        // кнопка вывода и записи нормальной работы станка
        normalWorkButton.setOnAction(actionEvent ->
        {
            String plateNumber = myWebSocketHandler.getPlateNumber();
            machineLogger.logNormalOperation("Normal operation log");
            // послать сообщение клиенту
            MyWebSocketHandler.broadcastMessage("нормальная работа станка: идёт нормальная работа станк, загружена плата номер: " + plateNumber);
        });

        // кнопка вывода и записи нормальной работы станка
        worningButton.setOnAction(actionEvent ->
        {
            String plateNumber = myWebSocketHandler.getPlateNumber();
            machineLogger.logWarning("Warning log");
            // послать сообщение клиенту
            MyWebSocketHandler.broadcastMessage("предупреждение: станок работает с проблемами, загружена плата номер: " + plateNumber);

        });

        // кнопка вывода и записи ошибок работы станка
        errorButton.setOnAction(actionEvent ->
        {
            String plateNumber = myWebSocketHandler.getPlateNumber();
            machineLogger.logError("Error log");
            // послать сообщение клиенту
            MyWebSocketHandler.broadcastMessage("ошибка: произошла ошибка при работе станка, загружена плата номер: " + plateNumber);
        });

        // кнопка завершения работы станка
        endButton.setOnAction(actionEvent ->
        {
            String plateNumber = myWebSocketHandler.getPlateNumber();
            machineLogger.endPlateOperation();
            // послать сообщение клиенту
            MyWebSocketHandler.broadcastMessage("Завершение работы: произошло корректное завершение работы станка, загруженная плата номер: " + plateNumber);
        });
    }

    /**
     * метод получения webSocketServer, для доступа в других классах
     * (вызов в классе HelloApplication)
     * **/
    public WebSocketServer getWebSocketServer()
    {
        return webSocketServer;
    }
}
