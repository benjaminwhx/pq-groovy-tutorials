package com.github.groovy.official

/**
 * User: 吴海旭
 * Date: 2017-08-27
 * Time: 下午4:25  */
// 1.2 Closures as an object
def listener = { e -> println "Clicked on $e.source"}
assert listener instanceof Closure
Closure callback = { println 'Done!' }
Closure<Boolean> isTextFile = {
    File it -> it.name.endsWith('.txt')
}

// 1.3 Calling a closure
def code = { 123 }
assert code() == 123
// 你也可以使用call方法
assert code.call() == 123

def isOdd = { int i -> i%2 != 0 }
assert isOdd(3)
assert !isOdd.call(2)

def isEven = { it % 2 == 0 }
assert !isEven(3)
assert isEven.call(2)

// 2.1 参数的定义，没有明确的参数指定默认为it
def closureWithOneArg = { str -> str.toUpperCase() }
assert closureWithOneArg('groovy') == 'GROOVY'

def closureWithOneArgAndExplicitType = { String str -> str.toUpperCase() }
assert closureWithOneArgAndExplicitType('groovy') == 'GROOVY'

def closureWithTwoArgs = { a,b -> a + b }
assert closureWithTwoArgs(1, 2) == 3

def closureWithTwoArgsAndExplicitTypes = { int a, int b -> a + b }
assert closureWithTwoArgsAndExplicitTypes(1, 2) == 3

def closureWithTwoArgsAndOptionalTypes = { a, int b -> a + b }
assert closureWithTwoArgsAndOptionalTypes(1, 2) == 3

def closureWithTwoArgAndDefaultValue = { int a, int b = 2 -> a + b }
assert closureWithTwoArgAndDefaultValue(1) == 3

def magicNumber = { -> 42 }
// 下面这句执行会失败，因为magicNumber closure 没有参数
//magicNumber(11)

// 2.3 Varargs 可变参数
def concat1 = { String... args -> args.join('') }
assert concat1('abc', 'def') == 'abcdef'

def concat2 = { String[] args -> args.join('') }
assert concat2('abc', 'def') == 'abcdef'

def multiConcat = { int n, String... args -> args.join('') * n  }
assert multiConcat(2, 'abc', 'def') == 'abcdefabcdef'

// 3.2.1 this
class Enclosing {
    void run() {
        def whatIsThisObject = {
            getThisObject()
        }
        assert whatIsThisObject() == this
        def whatIsThis = { this }
        assert whatIsThis() == this
    }
}
class EnclosedInInnerClass {
    class Inner {
        Closure cl = { this }
    }
    void run() {
        def inner = new Inner()
        assert inner.cl() == inner
    }
}
class NestedClosures {
    void run() {
        def nestedClosures = {
            def cl = { this }
            cl()
        }
        assert nestedClosures() == this
    }
}
def enclosing = new Enclosing()
enclosing.run()
def enclosedInInnerClass = new EnclosedInInnerClass()
enclosedInInnerClass.run()
def nestedClosures = new NestedClosures()
nestedClosures.run()

// 3.2.2 Owner
class EnclosingOfOwner {
    void run() {
        def whatIsOwnerMethod = {
            getOwner()
        }
        assert whatIsOwnerMethod() == this
        def whatIsOwner = { owner }
        assert whatIsOwner() == this
    }
}
class EnclosedInInnerClassOfOwner {
    class Inner {
        Closure cl = { owner }
    }
    void run() {
        def inner = new Inner()
        assert inner.cl() == inner
    }
}
class NestedClosuresOfOwner {
    void run() {
        def nestedClosures = {
            def cl = { owner }
            cl()
        }
        assert nestedClosures() == nestedClosures
    }
}

// 3.2.3 delegate
class EnclosingOfDelegate {
    void run() {
        def cl = { getDelegate() }
        def cl2 = { delegate }
        assert cl() == cl2()
        assert cl() == this
        def enclosed = {
            { -> delegate }.call()
        }
        assert enclosed() == enclosed
    }
}

class PersonOfDelegate {
    String name
}
class ThingOfDelegate {
    String name
}
def pod = new PersonOfDelegate(name: 'Norman')
def tod = new ThingOfDelegate(name: 'Teapot')
def upperCasedName = { delegate.name.toUpperCase() }
upperCasedName.delegate = pod
assert upperCasedName() == 'NORMAN'
upperCasedName.delegate = tod
assert upperCasedName() == 'TEAPOT'

// 6.1 Currying
// 6.1.1 Left currying
def nCopies = { int n, String str -> str * n }
def twice = nCopies.curry(2)
assert twice('bla') == 'blabla'
assert twice('bla') == nCopies(2, 'bla')

// 6.1.2 Right currying
def blah = nCopies.rcurry('bla')
assert blah(2) == 'blabla'
assert blah(2) == nCopies(2, 'bla')

// 6.1.3 Index based currying，指定索引
def volume = { double l, double w, double h -> l * w * h }
def fixedWidthVolume = volume.ncurry(1, 2d)
assert volume(3d, 2d, 4d) == fixedWidthVolume(3d, 4d)
def fixedWidthAndHeight = volume.ncurry(1, 2d, 4d)
assert volume(3d, 2d, 4d) == fixedWidthAndHeight(3d)

// 6.2 Memoization 缓存执行的值
def fib
fib = { long n -> n < 2 ? n : fib(n - 1) + fib(n - 2) }.memoize()
assert fib(15) == 610
assert fib(25) == 75025 // fast!

// 6.3 Composition 多个closure调用
def plus2 = { it + 2 }
def times3 = { it * 3 }
def times3plus2 = plus2 << times3
assert times3plus2(3) == 11
assert times3plus2(4) == plus2(times3(4))

def plus2times3 = times3 << plus2
assert plus2times3(3) == 15
assert plus2times3(5) == times3(plus2(5))

assert times3plus2(3) == (times3 >> plus2)(3)

// 6.4 Trampoline
def factorial
factorial = {
    int n, def accu = 1G ->
        if (n < 2) return accu
        factorial.trampoline(n - 1, n * accu)
}
factorial = factorial.trampoline()

assert factorial(1) == 1
assert factorial(3) == 1 * 2 * 3
assert factorial(1000) // == 402387260... 省略后面的2560位