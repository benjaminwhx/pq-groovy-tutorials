/**
 * User: 吴海旭
 * Date: 2017-08-26
 * Time: 下午10:16  */

import org.codehaus.groovy.runtime.InvokerHelper

import static java.lang.Math.*

assert sin(0) == 0.0
assert cos(0) == 1.0

// 3.7 使用别名导入
import java.sql.Date as SQLDate

Date utilDate = new Date(1000L)
SQLDate sqlDate = new SQLDate(1000L)
assert utilDate instanceof Date
assert sqlDate instanceof java.sql.Date

// 3.2 脚本类
class Main extends Script {

    int power(int n) {
        2 ** n
    }

    @Override
    Object run() {
        println 'Groovy world!'
        println "2 ^ 6 == ${power(6)}"
    }

    static void main(String[] args) {
        InvokerHelper.runScript(Main, args)
    }
}