package com.kotlin.hotfix;

/**
 * create by max at 2020/11/24 12:39
 */

public class Util {

    public static void test() {

//        System.out.println("patch ok======");
        throw new IllegalStateException("main error ");
    }
}
