package org.example.programmjavafx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.example.programmjavafx.Server.CRUDController;
import org.example.programmjavafx.Server.DataBase.dbService.ServiceDB;
import org.example.programmjavafx.Server.WebSocketServer;
import java.io.IOException;

/** запускающий класс приложения **/
public class HelloApplication extends Application
{
    //region Fields
    private WebSocketServer webSocketServer;
    //endregion

    /**
     * метод запускает приложение и является точкой входа в графическую часть приложения
     * аргумент: Stage - основной контейнер для всех визуальных элементов JavaFX приложения
     **/
    @Override
    public void start(Stage primaryStage) throws Exception
    {
        // Создаем загрузчик FXML файла с заданным путем к ресурсу
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/programmjavafx/newfile11.fxml"));

        // Устанавливаем сцену первичной стадии, загружая её из FXML файла
        primaryStage.setScene(new Scene(loader.load()));

        // Устанавливаем заголовок окна приложения
        primaryStage.setTitle("JavaFX WebSocket Example");

        // Получаем размеры экрана
        double screenWidth = Screen.getPrimary().getBounds().getWidth();
        double screenHeight = Screen.getPrimary().getBounds().getHeight();

        // Устанавливаем максимальные размеры окна
        primaryStage.setMaxWidth(screenWidth);
        primaryStage.setMaxHeight(screenHeight);

        primaryStage.show(); // Отображаем главное окно приложения


        ServiceDB serviceDB = new ServiceDB(); // Инициализация базы данных

        serviceDB.createTableLathe_platesIfNotExists(); // создаём таблицу lathe_plates

        serviceDB.createTableEventsIfNottExists(); // создаём таблицу events

        LatheController controller = loader.getController();  // Получаем контроллер из загрузчика, который связан с FXML файлом

        webSocketServer = controller.getWebSocketServer(); // Получаем экземпляр WebSocketServer из контроллера и сохраняем его в поле

        startServer(); // Запускаем сервер
    }

    /**
     * метод останавливает приложение (освобождение ресурсов или закрытие соединений)
     * **/
    @Override
    public void stop() throws Exception
    {
        if (webSocketServer != null)
        {
            webSocketServer.stop();
        }
    }

    /**
     * Метод для запуска сервера
     * **/
    private void startServer()
    {
        new Thread(() ->
        {
            // запуска метода main класса CRUDController в новом потоке (инициализации веб-сервера)
            try {
                CRUDController.main(new String[0]);
                System.out.println("Запустился сервер на порту 8098");
            }
            catch (IOException e)
            {
                throw new RuntimeException(e);
            }
            // new String[0] создает пустой массив строк, имитируя отсутствие аргументов командной строки
        }
        ).start();
    }


    /**
     * Основной метод запуска приложения
     * вызывает метод start
     * является стандартной точкой входа в приложение
     **/
    public static void main(String[] args)
    {
        launch(args); // запуск JavaFX приложения
    }
}
