package org.example;

public class MyClass {
    private Thread thread;

    public Thread getThread() {
        System.out.println(thread);

        if (thread == null) {
            thread = new Thread("My class thread for test");
        }
        return thread;
    }
}
