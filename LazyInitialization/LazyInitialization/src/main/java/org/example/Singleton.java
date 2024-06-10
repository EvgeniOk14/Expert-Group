package org.example;

public class Singleton {
    private static Singleton instance;

    private Singleton() {
        // Приватный конструктор предотвращает создание экземпляра класса извне
    }

    public static Singleton getInstance() {
        if (instance == null) {
            instance = new Singleton();
        }
        return instance;
    }
}
