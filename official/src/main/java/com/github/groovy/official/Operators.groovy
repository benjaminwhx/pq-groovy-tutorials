import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * User: 吴海旭
 * Date: 2017-08-26
 * Time: 下午6:40  */
// 1.3
def a = 4
a += 3
assert a == 7

def b = 5
b -= 3
assert b == 2

def c = 5
c *= 3
assert c == 15

def d = 10
d /= 2
assert d == 5

def e = 10
e %= 3
assert e == 1

def f = 3
f **= 2
assert f == 9

// 5.3 Elivis operator ?:
def user = [name: 'benjamin']
//def displayName = user.name ? user.name : 'Anonymous'
displayName = user.name ?: 'Anonymous'
assert displayName == 'benjamin'

// 6.1 安全导航操作符 ?.
class User {
    int id
    String name
}
def p = new User()
p.id = 111
p.name = '111'
def p2 = new User()
p2.id = 222
p2.name = '222'
def pList = [p, p2]
def person = pList.find{ it.id == 123 }
// ?.操作符
def name = person?.name
assert name == null

// 6.2 直接访问字段操作符 @fieldName .@
class User2 {
    public final String name
    User2(String name) {this.name = name}

    String getName() {
        "Name: $name"
    }
}
def user2 = new User2('Bob')
assert user2.name == 'Name: Bob'
assert user2.@name == 'Bob'

// 6.3 方法指针操作符 .&
def str = 'example of method reference'
def fun = str.&toUpperCase
def upper = fun()
assert upper == str.toUpperCase()

def transform(List elements, Closure action) {
    def result = []
    elements.each {
        result << action(it)
    }
    result
}
class Person {
    String name
    int age
}
String describe(Person p) {
    "$p.name is $p.age"
}
// 获取方法的指针
def action = this.&describe
def list = [new Person(name: 'Bob', age: 42), new Person(name: 'Julia', age: 35)]
assert transform(list, action) == ['Bob is 42', 'Julia is 35']

// 7.1 模式操作符 ~
def pattern = ~/foo/
assert pattern instanceof Pattern

// 7.2 查找操作符 =~
def text = "some text to match"
def m = text =~ /match/
assert m instanceof Matcher
// equals !m.find()
if (!m) {
    println 'Oops, text not found!'
} else {
    println 'find it'
}

// 7.3 匹配操作符 ==~
m = text ==~ /match/
assert m instanceof Boolean
if (m) {
    throw new RuntimeException("Should not reach that point!")
}

// 8.1 传播操作符 *. 得到一个集合下面所有对象的属性值的集合
class Car {
    String make
    String model
}
def cars = [new Car(make: 'Peugeot', model: '508'), new Car(make: 'Renault', model: 'Clio')]
def makes = cars*.make
assert makes == ['Peugeot', 'Renault']

cars = [new Car(make: 'Peugeot', model: '508'),
        null,
        new Car(make: 'Renault', model: 'Clio')]
assert cars*.make == ['Peugeot', null, 'Renault']
assert null*.make == null

// *.操作符可以作用在实现了Iterable的类上
class Component {
    Long id
    String name
}
class CompositeObject implements Iterable<Component> {
    def components = [
            new Component(id: 1, name: 'Foo'),
            new Component(id: 2, name: 'Bar')
    ]

    @Override
    Iterator<Component> iterator() {
        components.iterator()
    }
}
def composite = new CompositeObject()
assert composite*.id == [1, 2]
assert composite*.name == ['Foo', 'Bar']

// 8.1.1 传播方法参数
int function(int x, int y, int z) {
    x * y + z
}
def args = [4, 5, 6]
assert function(*args) == 26

args = [4]
assert function(*args, 5, 6) == 26

// 8.1.2 传播list元素
def items = [4, 5]
def itemList = [1, 2, 3, *items, 6]
assert itemList == [1, 2, 3, 4, 5, 6]

// 8.1.3 传播map元素
def m1 = [c: 3, d: 4]
def map = [a: 1, b: 2, *: m1]
assert map == [a: 1, b: 2, c: 3, d: 4]

// map中存在的元素并不会被传播进来
map = [a: 1, b: 2, *: m1, d: 8]
assert map == [a: 1, b: 2, c: 3, d: 8]

// 8.2 范围操作符
def range = 0..5
assert range.collect() == [0, 1, 2, 3, 4, 5]
assert (0..<5).collect() == [0, 1, 2, 3, 4]
assert range instanceof List
assert range.size() == 6
assert ('a'..'d').collect() == ['a', 'b', 'c', 'd']

// 8.3 比较操作符 <=>代表compareTo方法
assert (1 <=> 1) == 0
assert (1 <=> 2) == -1
assert (2 <=> 1) == 1
assert ('a' <=> 'z') == -1

// 8.4 下标操作符
def subscriptList = [0, 1, 2, 3, 4]
// 相当于调用putAt方法
assert subscriptList[2] == 2
// 相当于调用getAt方法
subscriptList[2] = 4
assert subscriptList[0..2] == [0, 1, 4]
subscriptList[0..2] = [6, 6, 6]
assert subscriptList == [6, 6, 6, 3, 4]

class User3 {
    Long id
    String name
    def getAt(int i) {
        println "invoke getAt $i"
        switch (i) {
            case 0: return id
            case 1: return name
        }
        throw new IllegalAccessException("No such element $i")
    }
    def putAt(int i, def value) {
        println "invoke putAt $i value: $value"
        switch (i) {
            case 0: id = value
                return
            case 1: name = value
                return
        }
        throw new IllegalAccessException("No such element $i")
    }
}
def user3 = new User3(id: 1, name: 'Alex')
assert user3[0] == 1
assert user3[1] == 'Alex'
user3[1] = 'Bob'
assert user3.name == 'Bob'

// 8.5 成员操作符 in 相当于调用了集合的contains
def msList = ['Grace', 'Rob', 'Emmy']
assert ('Emmy' in msList)

// 8.6 恒等操作符 is()
// 在groovy中 == 不和java中一样，它表示equals，如果你想比较引用是否相等，使用is操作符
def list1 = ['Groovy 1.8', 'Groovy 2.0', 'Groovy 2.3']
def list2 = ['Groovy 1.8', 'Groovy 2.0', 'Groovy 2.3']
assert list1 == list2
assert !list1.is(list2)

// 8.7 强制转换操作符 as
Integer x = 123
//String s = (String) x
String s = x as String
assert s == '123'

// 自定义强制转换 asType方法
class Identifiable {
    String name
}
class User4 {
    Long id
    String name
    def asType(Class target) {
        if (target.is(Identifiable)) {
            return new Identifiable(name: name)
        }
        throw new ClassCastException("User cannot be coerced into $target")
    }
}
def user4 = new User4(name: 'Xavier')
def iden = user4 as Identifiable
assert iden instanceof Identifiable
assert !(iden instanceof User4)

// 10 操作符重写
class Bucket {
    int size
    Bucket (int size) {
        this.size = size
    }

    Bucket plus(Bucket other) {
        return new Bucket(this.size + other.size)
    }
}
def b1 = new Bucket(4)
def b2 = new Bucket(11)
assert (b1 + b2).size == 15

/**
 ** 下面的操作符都可以通过重写方法来实现
 *  +   a.plus(b)
 *  -   a.minus(b)
 *  *   a.multiply(b)
 *  /   a.div(b)
 *  %   a.mod(b)
 *  **  a.power(b)
 *  |   a.or(b)
 *  &   a.and(b)
 *  ^   a.xor(b)
 *  as  a.asType(b)
 *  a() a.call()
 *  a[b]    a.getAt(b)
 *  a[b] = c    a.putAt(b, c)
 *  a in b  b.isCase(a)
 *  <<  a.leftShift(b)
 *  >>  a.rightShift(b)
 *  >>> a.rightShiftUnsigned(b)
 *  ++  a.next()
 *  --  a.previous()
 *  +a  a.positive()
 *  -a  a.negative()
 *  ~a  a.bitwiseNegate()
 */