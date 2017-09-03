package com.github.groovy.jwg.demo4;

import groovy.lang.GroovyObject;

/**
 * User: 吴海旭
 * Date: 2017-09-02
 * Time: 下午7:51
 */
public class CallDynamicMethod {
    public static void main(String[] args) {
        GroovyObject instance = new DynamicGroovyClass();
        Object result1 = instance.invokeMethod("squeak", new Object[]{});
        System.out.println("Received: " + result1);

        Object result2 = instance.invokeMethod("quack", new Object[]{"like", "a", "duck"});
        System.out.println("Received: " + result2);
    }
}
