package org.example;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import static spark.Spark.port;

public class Main
{
    public static void main(String[] args)
    {
//        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
//        System.out.println(dateFormat.format(Date.from(Instant.now())));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
        System.out.println(formatter.format(LocalDateTime.now()));

    }
}
