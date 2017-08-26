package com.github.groovy.official

import groovy.transform.AnnotationCollector
import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode
import javafx.concurrent.Task

import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.reflect.Modifier

/**
 * User: 吴海旭
 * Date: 2017-08-26
 * Time: 下午10:31  */
// 1.3 Interface
interface Greeter {
    void greet(String name)
}
class DefaultGreeter {
    void greet(String name) {
        println "Hello"
    }
}
def greeter = new DefaultGreeter()
// 强制转换类为Greeter类型
def coerced = greeter as Greeter
assert coerced instanceof Greeter

// 1.4.1 根据位置来选择构造器
class PersonConstructor {
    String name
    Integer age

    PersonConstructor(name, age) {
        this.name = name
        this.age = age
    }
}
def person1 = new PersonConstructor('Marie', 1)
def person2 = ['Marie', 2] as PersonConstructor
PersonConstructor person3 = ['Marie', 3]

// 1.4.2 命名参数构造器
class PersonWOConstructor {
    String name
    Integer age
}
def person4 = new PersonWOConstructor()
def person5 = new PersonWOConstructor(name: 'Marie')
def person6 = new PersonWOConstructor(age: 1)
def person7 = new PersonWOConstructor(name: 'Marie', age: 2)

// 1.5 Method
// 1.5.2 命名参数
def foo(Map args) {
    "${args.name}: ${args.age}"
}
println foo(name: 'Marie', age: 1)

// 1.5.3 默认参数
def foo2(String par1, Integer par2 = 1) {
    [name: par1, age: par2]
}
assert foo2('Marie').age == 1

// 1.5.4 可变参数
def foo3(Object... args) {
    1
}
def foo3(Object x) {
    2
}
assert foo3() == 1
assert foo3(1) == 2
assert foo3(1, 2) == 1

// 1.6.2 properties
class Person {
    String name
    int age
}
def p = new Person()
assert p.properties.keySet().containsAll(['name', 'age'])

class PseudoProperties {
    void setName(String name) {}
    String getName() {}

    int getAge() { 42 }

    void setGroovy(boolean groovy) {}
}
def pp = new PseudoProperties()
pp.name = 'Foo'
assert pp.age == 42
pp.groovy = true

// 1.7 Annotation
@interface Page {
    String value()
    int statusCode() default 200
}

@Page(value = '/home')
void home() {
}

@Page(value = '/users')
void userList() {
}

@Page(value = 'error', statusCode = 404)
void notFound() {
}


// 1.7.5 闭包注解参数

@Retention(RetentionPolicy.RUNTIME)
@interface OnlyIf {
    Class value()
}

class Tasks {
    Set result = []
    void alwaysExecuted() {
        result << 1
    }

    @OnlyIf({ jdk >= 6 })
    void supportedOnlyInJDK6() {
        result << 'JDK 6'
    }

    @OnlyIf({ jdk >= 7 && windows })
    void requiresJDK7AndWindows() {
        result << 'JDK 7 Windows'
    }
}

class Runner {
    static <T> T run(Class<T> taskClass) {
        def tasks = taskClass.newInstance()
        def params = [jdk: 6, windows: false]
        tasks.class.declaredMethods.each {m ->
            if (Modifier.isPublic(m.modifiers) && m.parameterTypes.length == 0) {
                println "methodName: $m.name, annotation: $m.annotations"
                def onlyIf = m.getAnnotation(OnlyIf)
                if (onlyIf) {
                    Closure cl = onlyIf.value().newInstance(Closure, tasks)
                    cl.delegate = params
                    if (cl()) {
                        m.invoke(tasks)
                    }
                } else {
                    m.invoke(tasks)
                }
            }
        }
        tasks
    }
}
def taskResult = Runner.run(Tasks)
assert taskResult.result == [1, 'JDK 6'] as Set

// 1.7.6 元注解 使用@AnnotationCollector组合多个注解为一个
@Retention(RetentionPolicy.RUNTIME)
@interface Service {
    String value()
}
@Retention(RetentionPolicy.RUNTIME)
@interface Transactional {
    String value()
}

@Service(value = '/s')
@Transactional(value = '/t')
class MyTransactionalService{
}

@Service
@Transactional
@AnnotationCollector
@interface TransactionalService {
}

@TransactionalService(value = '/ts')
class MyNewTransactionalService {
}

def annotations = MyNewTransactionalService.annotations*.annotationType()
assert (Service in annotations)
assert (Transactional in annotations)
assert MyNewTransactionalService.getAnnotation(Service).value() == '/ts'
assert MyNewTransactionalService.getAnnotation(Transactional).value() == '/ts'

@CompileStatic(TypeCheckingMode.SKIP)
@AnnotationCollector
public @interface CompileDynamic {}

@AnnotationCollector(processor = "org.codehaus.groovy.transform.CompileDynamicProcessor")
public @interface CompileDynamic2 {}

// 2 Trait特性
// 可以当做接口有默认实现，可以有抽象方法，可以有私有方法
trait FlyingAbility {
    String fly() {
        "I'm flying!"
    }
}
class Bird implements FlyingAbility {
}
def b = new Bird()
assert b.fly() == "I'm flying!"

// 2.3 接口
interface Named {
    String name()
}
trait Greetable implements Named {
    String greeting() {
        "Hello, ${name()}!"
    }
}
class Person2 implements Greetable {

    @Override
    String name() {
        'Bob'
    }
}
def p2 = new Person2()
assert p2.greeting() == 'Hello, Bob!'
assert p2 instanceof Named
assert p2 instanceof Greetable

// 2.5.2 public 字段
trait Named2 {
    public String name
}
class Person3 implements Named2 {}
def p3 = new Person3()
p3.com_github_groovy_official_Named2__name = 'Bob'