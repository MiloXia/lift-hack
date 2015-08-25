package com.wellnr.examples

object ExampleApp1 {

  def hello() = println("hello")

  def main(args: Array[String]) {
    hello()
  }
}

class C {
  def hey = println("C")
  def plnStr(s: String) = println(s)
  def add(x: Int, y: Int) = x + 1
}

class B extends C {
  override def hey = println("B")
}

class A extends C {
  override def hey = println("A")
}

object Test {
  def main(args: Array[String]) {
    val a = new A
    val b = new B
    val c = new C
    c.plnStr("hello")
    a.hey
    b.hey
    c.hey
    println(c.add(1, 1))
  }
}