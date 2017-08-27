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

