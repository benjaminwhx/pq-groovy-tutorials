/**
 * User: 吴海旭
 * Date: 2017-08-25
 * Time: 下午5:39  */
// 3.2
def map = [:]
map."an identifier with a space and double quotes" = "ALLOWED"

assert map."an identifier with a space and double quotes" == "ALLOWED"

map.'single quote'
map."double quote"
// 三单引号字符串是一列被三个单引号包围的字符，是普通的java.lang.String。不支持插值。
// 三单引号字符串是多行的。你可以使字符串内容跨越行边界，不需要将字符串分割为一些片段，不需要连接，或换行转义符：
map.'''triple
string
quote'''
map."""triple double quote"""
map./slashy string/
map.$/dollar slashy string/$

def firstname = "Homer"
map."Simson-${firstname}" = "Homer Simson"
assert map.'Simson-Homer' == "Homer Simson"

// 4.2 字符串拼接
assert 'ab' == 'a' + 'b'

// 4.3
def startingAndEndingWithNewLine = '''
line one
line two
line three
'''
assert startingAndEndingWithNewLine.contains("\n")

def strippedFirstNewLine = '''\
line one
line two
line three
'''

assert !strippedFirstNewLine.startsWith("\n")

// 4.3.1
'an escaped single quote: \' needs a backslash'

// 4.4.1
def sum = "The sum of 2 and 3 equals ${2 + 3}"
assert sum.toString() == 'The sum of 2 and 3 equals 5'

def person = [name: 'Benjamin', age: 25]
assert "my name is $person.name and age is $person.age" == 'my name is Benjamin and age is 25'

//def number = 3.14
//shouldFail(MissingPropertyException) {
//    println "${number.toString}()"
//}

assert '${name}' == "\${name}"

// 4.4.2
def sParameterLessClosure = "1 + 2 == ${-> 3}"
assert sParameterLessClosure == '1 + 2 == 3'
def sOneParamClosure = "1 + 2 == ${w -> w << 3}"
assert sOneParamClosure == '1 + 2 == 3'

def number = 1
// 立即加载
def eagerGString = "value == ${number}"
// 懒加载
def lazyGString = "value == ${-> number}"

assert eagerGString == "value == 1"
assert lazyGString == "value == 1"

number = 2
assert eagerGString == "value == 1"
assert lazyGString == "value == 2"

String takeString(String message) {
    assert message instanceof String
    return message
}

def message = "The message is ${'hello'}"
assert message instanceof GString

def result = takeString(message)
assert result instanceof String
assert result == 'The message is hello'

// 4.4.4
assert "one: ${1}".hashCode() == "one: 1".hashCode() + 37

// 因为GString和String拥有不同的hashCode，所以用GString作为map的key会导致失效
def key = "a"
def m = ["${key}": "letter ${key}"]
assert m["a"] == null

// 4.5 三个双引号字符串
def name = 'Groovy'
def template = """
    Dear Mr ${name},
    
    You're the winner of the lottery!
    
    Yours sincerly,
    
    Dave
"""
assert template.toString().contains('Groovy')

// 4.6 斜杠字符串
def escapeSlash = /The character \/ is a forward slash/
assert escapeSlash == 'The character / is a forward slash'

def multilineSlashy = /one
    two
    three/
assert multilineSlashy.contains('\n')

// 下面编译都过不去
//assert '' == //

/**
 * 4.7 $/字符串，转义需要使用$字符
 * 打印/字符串，需要使用$进行转义：$/
 * 打印$/字符串，$$$/
 * 打印/$字符串，$/$$
 */
def name2 = 'Guillaume'
def date = 'April, 1st'
def dollarSlashy = $/
    Hello $name2,
    today we're ${date}.

    $ dollar sign
    $$ escaped dollar sign
    \ backslash
    / forward slash
    $/ escaped forward slash
    $$$/ escaped opening dollar slashy
    $/$$ escaped closing dollar slashy
/$

assert [
        'Guillaume',
        'April, 1st',
        '$ dollar sign',
        '\\ backslash',
        '/ forward slash',
        '/ escaped forward slash',
        '$/ escaped opening dollar slashy',
        '/$ escaped closing dollar slashy'
].every { dollarSlashy.contains(it) }

// 4.9 字符，我们可以明确的指定它是一个字符
char c1 = 'A'
assert c1 instanceof Character

def c2 = 'B' as char
assert c2 instanceof Character

def c3 = (char) 'C'
assert c3 instanceof Character

// 5.1
def a = 1
def na = -1
assert a instanceof Integer
assert na instanceof Integer

// Integer.MAX_VALUE
def b = 2147483647
// Integer.MIN_VALUE
def nb = -2147483648
assert b instanceof Integer
assert nb instanceof Integer

// Integer.MAX_VALUE + 1
def c = 2147483648
// Integer.MIN_VALUE - 1
def nc = -2147483649
assert c instanceof Long
assert nc instanceof Long

// Long.MAX_VALUE
def d = 9223372036854775807
// Long.MIN_VALUE
def nd = -9223372036854775808
assert d instanceof Long
assert nd instanceof Long

// Long.MAX_VALUE + 1
def e = 9223372036854775808
// Long.MIN_VALUE - 1
def ne = -9223372036854775809
assert e instanceof BigInteger
assert ne instanceof BigInteger

// 5.4 数字类型后缀
assert 42I == new Integer('42')
assert 42i == new Integer('42')
assert 123L == new Long('123')
assert 2147483648 == new Long('2147483648')
assert 456G == new BigInteger('456')
assert 456g == new BigInteger('456')
assert 123.45 == new BigDecimal('123.45')
assert 1.200065D == new Double('1.200065')
assert 1.234F == new Float('1.234')
assert 0b1111L.class == Long
assert 0xFFi.class == Integer
assert 034G.class == BigInteger

// 7 List
def arrayList = [1, 2, 3]
assert arrayList instanceof ArrayList
def linkedList = [2, 3, 4] as LinkedList
assert linkedList instanceof LinkedList
LinkedList otherLinked = [3, 4, 5]
assert otherLinked instanceof LinkedList

def letters = ['a', 'b', 'c', 'd']
assert letters[0] == 'a'
assert letters[1] == 'b'
assert letters[-1] == 'd'
assert letters[-2] == 'c'

letters[2] = 'C'
assert letters[2] == 'C'

letters << 'e'
assert letters[4] == 'e'
assert letters[-1] == 'e'

assert letters[1, 3] == ['b', 'd']
assert letters[2..4] == ['C', 'd', 'e']

letters[0, 1] = ['A', 'B']
assert letters[0, 1] == ['A', 'B']

def multi = [[0, 1], [2, 3]]
assert multi[1][0] == 2

// 8 Arrays
String[] arrStr = ['Ananas', 'Banana', 'Kiwi']
assert arrStr instanceof String[]
assert !(arrStr instanceof List)

def numArr = [1, 2, 3] as int[]
assert numArr instanceof int[]
assert numArr.size() == 3

// 9 Maps
def colors = [red: '#FF0000', green: '#00FF00', blue: '#0000FF']
assert colors['red'] == '#FF0000'
assert colors.green == '#00FF00'

colors['pink'] = '#FF00FF'
colors.yellow = '#FFFF00'
assert colors.pink == '#FF00FF'
assert colors['yellow'] == '#FFFF00'

assert colors instanceof LinkedHashMap

def mapKey = 'name'
def person2 = [mapKey: 'Guillaume']
assert !person2.containsKey('name')
assert person2.containsKey('mapKey')

// ()来使用外部定义的key
person2 = [(mapKey): 'Guillaume']
assert person2.containsKey('name')
assert !person2.containsKey('mapKey')