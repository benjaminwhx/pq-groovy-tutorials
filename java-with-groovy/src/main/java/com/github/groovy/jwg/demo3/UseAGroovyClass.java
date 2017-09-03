package com.github.groovy.jwg.demo3;

/**
 * User: 吴海旭
 * Date: 2017-09-02
 * Time: 下午7:42
 */
public class UseAGroovyClass {
    public static void main(String[] args) {
        AGroovyClass instance = new AGroovyClass();
        Object result = instance.useClosure(new Object() {
            public String call() {
                return "You called from Groovy!";
            }
        });

        System.out.println("Received: " + result);

        System.out.println("Received: " + instance.passToClosure(2, new Object() {
            public String call(int value) {
                return "You called from Groovy with value " + value;
            }
        }));
    }
}
