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