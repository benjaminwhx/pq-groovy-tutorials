package com.github.groovy.jwg.demo3

/**
 * User: 吴海旭
 * Date: 2017-09-02
 * Time: 下午7:41  */
class AGroovyClass {
    def useClosure(closure) {
        println "Calling closure"
        closure()
    }

    def passToClosure(int value, closure) {
        println "Simple passing $value to the given closure"
        closure(value)
    }
}
