package com.cui.service;

import com.cui.spring.CuiApplicationContext;

public class Test {

    public static void main(String[] args) {
        CuiApplicationContext cuiApplicationContext = new CuiApplicationContext(AppConfig.class);

        Object userService1 = cuiApplicationContext.getBean("userService");
        Object userService2 = cuiApplicationContext.getBean("userService");
        Object userService3 = cuiApplicationContext.getBean("userService");
        Object userService4 = cuiApplicationContext.getBean("userService");

        Object nameService1 = cuiApplicationContext.getBean("nameService");
        Object nameService2 = cuiApplicationContext.getBean("nameService");
        Object nameService3 = cuiApplicationContext.getBean("nameService");
        Object nameService4 = cuiApplicationContext.getBean("nameService");

        System.out.println(nameService1);
        System.out.println(nameService2);
        System.out.println(nameService3);
        System.out.println(nameService4);

        System.out.println(userService1);
        System.out.println(userService2);
        System.out.println(userService3);
        System.out.println(userService4);

    }

}
