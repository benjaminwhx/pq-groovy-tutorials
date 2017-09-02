package com.github.groovy.official

/**
 * User: 吴海旭
 * Date: 2017-08-27
 * Time: 下午9:52  */
// 1.2.1 Multiple assignment
def (a, b, c) = [10, 20, 'foo']
assert a == 10 && b == 20 && c == 'foo'

def (int i, String j) = [10, 'foo']
assert i == 10 && j == 'foo'

def (_, month, year) = "18th June 2009".split()
assert "In $month of $year" == 'In June of 2009'

// 1.2.2 Overflow and Underflow
//没有的cc置空
def (aa, bb, cc) = [1, 2]
assert aa == 1 && bb == 2 && cc == null

// 省略3
def (aaa, bbb) = [1, 2, 3]
assert  aaa == 1 && bbb == 2

// 1.2.3
class Cordinates {
    double latitude
    double longitude

    double getAt(int idx) {
        if (idx == 0) latitude
        else if (idx == 1) longitude
        else throw new Exception("Wrong cordinate index, use 0 or 1")
    }
}
def cordinates = new Cordinates(latitude: 42.44, longitude: 3.44)
def (la, lo) = cordinates
assert la == 42.44
assert lo == 3.44

// 1.3 loop
// iterate over a range
def x = 0
for (ii in 0..9) {
    x += ii
}
assert x == 45

// iterate over a list
x = 0
for (ii in [0, 1, 2, 3, 4]) {
    x += ii
}
assert x == 10

// iterate over an array
def array = (0..4).toArray()
x = 0
for (ii in array) {
    x += ii
}
assert x == 10

// iterate over a map
def map = ['abc':1, 'def':2, 'xyz':3]
x = 0
for (e in map) {
    x += e.value
}
assert x == 6

// iterate over values in a map
x = 0
for (v in map.values()) {
    x += v
}
assert x == 6

// iterate over the characters in a string
def text = "abc"
def list = []
for (t in text) {
    list.add(t)
}
assert list == ["a", "b", "c"]

// 1.3.4 try/catch/finally
try {
    'moo'.toLong()
    assert false
} catch ( e ) {
    assert e in NumberFormatException
}

// 2.1.3 GPath for XML navigation
def xmlText = """
              | <root>
              |   <level>
              |      <sublevel id='1'>
              |        <keyVal>
              |          <key>mykey</key>
              |          <value>value 123</value>
              |        </keyVal>
              |      </sublevel>
              |      <sublevel id='2'>
              |        <keyVal>
              |          <key>anotherKey</key>
              |          <value>42</value>
              |        </keyVal>
              |        <keyVal>
              |          <key>mykey</key>
              |          <value>fizzbuzz</value>
              |        </keyVal>
              |      </sublevel>
              |   </level>
              | </root>
              """
def root = new XmlSlurper().parseText(xmlText.stripMargin())
assert root.level.size() == 1
assert root.level.sublevel.size() == 2
assert root.level.sublevel.findAll { it.@id == 1 }.size() == 1
assert root.level.sublevel[1].keyVal[0].key.text() == 'anotherKey'

// 3.2 Closure to type coercion
interface Predicate<T> {
    boolean accept(T obj)
}
Predicate filter = { it.contains 'G' }
assert filter.accept("Groovy") == true

abstract class Greeters {
    abstract String getName()
    void greet() {
        println "Hello, $name"
    }
}
Greeters greeters = { 'Greeter' }
greeters.greet()
