package com.github.groovy.jwg.demo4

/**
 * User: 吴海旭
 * Date: 2017-09-02
 * Time: 下午7:50  */
class DynamicGroovyClass {
    def methodMissing(String name, args) {
        println "You called $name with ${args.join(', ')}."
        args.size()
    }
}
