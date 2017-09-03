package com.github.groovy.jwg.demo6;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/**
 * User: 吴海旭
 * Date: 2017-09-03
 * Time: 上午10:34
 */
public class CallingScript {
    public static void main(String[] args) {
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("groovy");
        System.out.println("Calling script from Java");
        try {
            engine.eval("println 'Hello from Groovy'");
        } catch (ScriptException ex) {
            System.out.println(ex);
        }
    }
}
