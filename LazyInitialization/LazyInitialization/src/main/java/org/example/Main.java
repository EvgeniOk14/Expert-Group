package org.example;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main
{
    public static void main(String[] args)
    {
        LazzyInitializationExample example = new LazzyInitializationExample();

        // Первый вызов getResource() создаст экземпляр Resource
        Resource res1 = example.getResource();
        // Второй вызов getResource() не создаст новый экземпляр Resource
        Resource res2 = example.getResource();

        // Проверка, что оба вызова вернули один и тот же экземпляр
        System.out.println(res1 == res2); //  Проверка, что оба вызова метода
                                           // getResource() вернули один и тот же экземпляр Resource
                                           // Должно вывести true

       Singleton s1 = Singleton.getInstance();
       Singleton s2 = Singleton.getInstance();
       System.out.println(s1 == s2);

    }

}