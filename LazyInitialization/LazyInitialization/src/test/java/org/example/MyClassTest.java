package org.example;

import static org.junit.jupiter.api.Assertions.*;

class MyClassTest {

    @org.junit.jupiter.api.Test
    void getThread() {
        MyClass myClass = new MyClass();
        System.out.println(myClass.getThread());
        System.out.println(myClass.getThread());
    }
}