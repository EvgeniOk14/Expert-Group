module org.example.programmjavafx {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires spark.core;
    requires spark.template.thymeleaf;
    requires org.eclipse.jetty.websocket.api;
    requires org.eclipse.jetty.websocket.client;
    requires com.google.gson;
    requires org.eclipse.jetty.websocket.server;
    requires org.eclipse.jetty.server;
    requires org.eclipse.jetty.websocket.servlet;
    requires thymeleaf;
    requires slf4j.api;
    requires java.desktop;
    requires com.google.zxing.javase;
    requires com.google.zxing;

    requires java.sql;
    requires jfxtras.controls;
    opens org.example.programmjavafx.Server.entityDB to javafx.base;

    opens org.example.programmjavafx to javafx.fxml;
    exports org.example.programmjavafx;
    exports org.example.programmjavafx.Server;  // Изменено: теперь пакет экспортируется для всех модулей
    opens org.example.programmjavafx.Server to com.google.gson;
}
