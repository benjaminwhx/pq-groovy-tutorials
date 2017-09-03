package com.github.groovy.jwg.demo6;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/**
 * User: 吴海旭
 * Date: 2017-09-03
 * Time: 上午10:38
 */
public class ParameterPassing {
    public static void main(String[] args) {
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("groovy");
        engine.put("name", "Venkat");
        try {
            engine.eval("println \"Hello ${name} from Groovy \"; name += '!' ");
        } catch (ScriptException e) {
            e.printStackTrace();
        }
        String name = (String) engine.get("name");
        System.out.println("Back in Java: " + name);
    }
}
