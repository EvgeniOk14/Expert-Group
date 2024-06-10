package org.example.programmjavafx;

import com.google.gson.Gson;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import org.example.programmjavafx.LoggerLathe.models.ConfigManager;
import org.example.programmjavafx.LoggerLathe.models.MachineLogger;
import org.example.programmjavafx.LoggerLathe.service.FileCreator;
import org.example.programmjavafx.Server.MyWebSocketHandler;
//import org.example.programmjavafx.Server.WebSocketManager;
import org.example.programmjavafx.Server.WebSocketServer;
import java.util.Date;

/**
 * Класс контроллера, который описывает связь графических объектов с бизнес-логикой
 * **/
public class LatheController
{
    // region Fields
    private MachineLogger machineLogger;
    private WebSocketServer webSocketServer;
    //private volatile String plateNumber; // Номер платы
    private Gson gson = new Gson(); // Для обработки JSON сообщений
    long currentTime = System.currentTimeMillis();
    private MyWebSocketHandler myWebSocketHandler;
    // endregion

    public LatheController()
    {
        this.myWebSocketHandler = new MyWebSocketHandler();
        //this.myWebSocketHandler = WebSocketManager.getWebSocketHandler();
    }

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
        System.out.println("Метод initialize() запущен в: " + new Date(currentTime));

        // инициализация fileCreator
        FileCreator fileCreator = new FileCreator(new ConfigManager());

        // инициализация MachineLogger, в который передаём fileCreator
        machineLogger = new MachineLogger(fileCreator);

        // инициализация и запуск WebSocket server
        webSocketServer = new WebSocketServer(this);

        // запуск webSocket сервера в одтдельном потоке (что бы не мешал работе JavaFX)
        Thread serverThread = new Thread(() ->
        {
            try
            {
                System.out.println("запуск потока, класс LatheController блок rty: " + "[" + new Date(currentTime) + "] ");
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
            System.out.println("в классе LatheController устанавливаем через getPlateNumber()  plateNumber: " + plateNumber);

            System.out.println("блок кнопка запуска startButton: "  + "[" + new Date(currentTime) + "] " + plateNumber);

            if (plateNumber != null)
            {
                System.out.println("печатаем plateNumber в блоке if(plateNumber != nul)" + plateNumber);
                machineLogger.startNewPlate(plateNumber);
            }
            else
            {
                System.out.println("Не задан номер платы boardNumber!");
                // послать сообщение клиенту
            }
        });

        // кнопка вывода и записи нормальной работы станка
        normalWorkButton.setOnAction(actionEvent ->
        {
            machineLogger.logNormalOperation("Normal operation log");
            // послать сообщение клиенту
        });

        // кнопка вывода и записи нормальной работы станка
        worningButton.setOnAction(actionEvent ->
        {
            machineLogger.logWarning("Warning log");
            // послать сообщение клиенту
        });

        // кнопка вывода и записи ошибок работы станка
        errorButton.setOnAction(actionEvent ->
        {
            machineLogger.logError("Error log");
            // послать сообщение клиенту
        });

        // кнопка завершения работы станка
        endButton.setOnAction(actionEvent ->
        {
            machineLogger.endPlateOperation();
            // послать сообщение клиенту
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



// Метод для обновления номера платы
//public void updatePlateNumber(String plateNumber)
//{
//    System.out.println("Updating plate number to: " + plateNumber);
//    this.plateNumber = plateNumber;
//}

//        startButton.setOnAction(actionEvent ->
//        {
//            String plateNumber = myWebSocketHandler.getPlateNumber();
//            System.out.println("в классе LatheController устанавливаем через getPlateNumber()  plateNumber: " + plateNumber);
//
//            System.out.println("блок кнопка запуска startButton: "  + "[" + new Date(currentTime) + "] " + plateNumber);
//
//            if (plateNumber != null)
//            {
//                System.out.println("печатаем plateNumber в блоке if(plateNumber != nul)" + plateNumber);
//
//                // Создаем имя файла с использованием текущей даты и времени
//                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH;mm;ss.SSS");
//                String fileName = "./psblog_" + sdf.format(new Date()) + "_" + plateNumber + ".txt";
//
//                // Записываем в файл лог
//                try {
//                    FileWriter writer = new FileWriter(fileName);
//                    writer.write("Your log content here");
//                    writer.close();
//                    System.out.println("Файл успешно обновлен: " + fileName);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//
//                machineLogger.startNewPlate(plateNumber);
//            }
//            else
//            {
//                System.out.println("Не задан номер платы boardNumber!");
//                // послать сообщение клиенту
//            }
//        });