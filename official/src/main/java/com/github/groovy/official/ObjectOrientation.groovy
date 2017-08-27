package com.github.groovy.official

import groovy.transform.AnnotationCollector
import groovy.transform.CompileStatic
import groovy.transform.SelfType
import groovy.transform.TypeCheckingMode

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

// 2.8.1 继承 traits
trait Named3 {
    String name
}
trait Polite extends Named3 {
    String introduce() {
        "Hello, I am $name"
    }
}
class Person4 implements Polite {}
def p4 = new Person4(name: 'Alice')
assert p4.introduce() == 'Hello, I am Alice'

// 2.9 trait的动态类型
trait SpeakingDuck {
    // quack方法没有，在其实现类中找methodMissing方法
    String speak() { quack(123) }
}
class Duck implements SpeakingDuck {
    String methodMissing(String name, args) {
        println "missing for $name method, args: $args"
        // 返回方法名，首字母大写
        "${name.capitalize()}!"
    }
}
def d = new Duck()
assert d.speak() == 'Quack!'

// 2.9.2 动态方法和属性
trait DynamicObject {
    private Map props = [:]
    def methodMissing(String name, args) {
        name.toUpperCase()
    }
    def propertyMissing(String prop) {
        props[prop]
    }
    void setProperty(String prop, Object value) {
        props[prop] = value
    }
}
class Dynamic implements DynamicObject {
    String existingProperty = 'ok'
    String existingMethod() {
        'ok'
    }
}
def dynamic = new Dynamic()
assert dynamic.existingProperty == 'ok'
assert dynamic.foo == null
dynamic.foo = 'bar'
assert dynamic.foo == 'bar'
assert dynamic.existingMethod() == 'ok'
assert dynamic.someMethod() == 'SOMEMETHOD'

// 多继承同方法冲突，会从最后定义的trait里调用
trait A {
    String exec() { 'A' }
}
trait B {
    String exec() { 'B' }
}
class C implements A,B {}
def c = new C()
assert c.exec() == 'B'

// 2.10.2 用户冲突解决
class D implements A,B {
    String exec() {
        A.super.exec()
    }
}
def dd = new D()
assert dd.exec() == 'A'

// 2.11 运行时实现traits
trait Extra {
    String extra() {
        "I'm an extra method"
    }
}
class Something {
    String doSomething() {
        'Something'
    }
}
// 使用as在运行时实现Extra
def s = new Something() as Extra
s.extra()
s.doSomething()

// 2.11.2 实现多个trait
trait AA {
    void methodFromAA() {}
}
trait BB {
    void methodFromBB() {}
}
class CC {}
def cc = new CC()
def abc = cc.withTraits(AA, BB)
abc.methodFromAA()
abc.methodFromBB()

interface MessageHandler {
    void on(String message, Map payload)
}
trait DefaultHandler implements MessageHandler {
    void on(String message, Map payload) {
        println "Received $message with payload $payload"
    }
}
trait LoggingHandler implements MessageHandler {
    void on(String message, Map payload) {
        println "Seeing $message with payload $payload"
        super.on(message, payload)
    }
}
class HandleWithLogger implements DefaultHandler, LoggingHandler {}
def loggingHandler = new HandleWithLogger()
// 因为相同的方法，执行最后一个，所以先执行LoggingHandler，再因为调用了super.on所以找到链中下一个方法执行也就是DefaultHandler
loggingHandler.on('test logging', [:])

trait SayHandler implements MessageHandler {
    void on(String message, Map payload) {
        if (message.startsWith("say")) {
            println "I say ${message - 'say'}!"
        } else {
            super.on(message, payload)
        }
    }
}
// 执行顺序由implements后面的顺序决定，这是一个chain
class Handler implements DefaultHandler, SayHandler, LoggingHandler {}
def h = new Handler()
h.on('foo', [:])
h.on('sayHello', [:])

// 2.12.1
trait Filtering {
    StringBuilder append(String str) {
        def subst = str.replace('o', '')
        super.append(subst)
    }
    String toString() {
        super.toString()
    }
}
def sb = new StringBuilder().withTraits(Filtering)
sb.append('Groovy')
assert sb.toString() == 'Grvy'

// 2.13.1 强制SAM类型(Single Abstract Method)
trait Greeter2 {
    String greet() {
        "Hello $name"
    }
    abstract String getName()
}

Greeter2 greeter2 = { 'Alice' }
assert greeter2.greet() == 'Hello Alice'

void greet(Greeter2 g) {
    println g.greet()
}
greet { 'Alice' }

// 2.14 mixins
class AAA {
    String methodFromA() {
        'A'
    }
}
class BBB {
    String methodFromB() {
        'B'
    }
}
AAA.metaClass.mixin(BBB)
def o = new AAA()
assert o.methodFromA() == 'A'
assert o.methodFromB() == 'B'
assert o instanceof AAA
assert !(o instanceof BBB)

// 2.15
trait TestHelper {
    public static boolean CALLED = false
    static void init() {
        CALLED = true
    }
}
// 每一个实现了trait的类都拥有一个静态变量
class Foo implements TestHelper {}
class Foo2 implements TestHelper {}
Foo.init()
assert Foo.com_github_groovy_official_TestHelper__CALLED
assert !Foo2.com_github_groovy_official_TestHelper__CALLED

// 2.16
trait IntCouple {
    int x = 1
    int y = 2
    int sum() {
        // 这地方想拿到Elem的x和y变量，必须使用getX和getY，不能使用x+y
        getX() + getY()
    }
}
class Elem implements IntCouple {
    int x = 3
    int y = 4
    int f() {
        sum()
    }
}
def elem = new Elem()
assert elem.f() == 7

// 2.71 Self types
class CommunicationService {
    static void sendMessage(String from, String to, String message) {
        println "$from sent [$message] to $to"
    }
}
class Device { String id }
// 强制声明必须继承Device
@SelfType(Device)
@CompileStatic
trait Communicating {
    void sendMessage(Device to, String message) {
        CommunicationService.sendMessage(id, to.id, message)
    }
}
class MyDevice extends Device implements Communicating {}

def bob = new MyDevice(id: 'Bob')
def alice = new MyDevice(id: 'Alice')
bob.sendMessage(alice, 'secret')

// 2.18
trait Counting {
    int x
    void inc() {
        x += 1
    }
    void dec() {
        x -= 1
    }
}
class Counter implements Counting {}
def counter = new Counter()
counter.inc()
counter.inc()
assert counter.x == 2