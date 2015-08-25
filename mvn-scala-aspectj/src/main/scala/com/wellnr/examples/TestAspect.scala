package com.wellnr.examples

import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.{After, DeclareMixin, Around, Before, Pointcut, Aspect}

/**
 * Created by milo on 15-8-18.
 */
@Aspect
class TestAspect {
  @Pointcut("execution(* com.wellnr.examples.ExampleApp1$.hello(..))")
  def helloPointcut() = {}

  @Before("helloPointcut()")
  def before(jp: JoinPoint) {
    val s = String.format("ENTER %s.%s%s", jp.getSignature.getDeclaringType.getName, jp.getSignature.getName, jp.getArgs.toList.mkString("(", ", ", ")"))
    println(s)
  }

  @DeclareMixin("com.wellnr.examples.C")
  def mixinHello: Hello = Hello.defaultImpl

  @Pointcut("execution(* com.wellnr.examples.C.plnStr(..)) && args(s)")
  def plnStr(s: String) = {}

  @Around("plnStr(s)")
  def aroundPlnStr(jp: JoinPoint, s: String) {
    val ss = String.format("ENTER %s.%s%s", jp.getSignature.getDeclaringType.getName, jp.getSignature.getName, jp.getArgs.toList.mkString("(", ", ", ")"))
    println(ss)
    println("arg "+s)
  }

  @Pointcut("execution(* com.wellnr.examples.C.add(..)) && args(x, y)")
  def addP(x: Int, y: Int) = {}

  @Around("addP(x, y)")
  def aroundAddP(jp: JoinPoint, x: Int, y: Int) = {
    val ss = String.format("ENTER %s.%s%s", jp.getSignature.getDeclaringType.getName, jp.getSignature.getName, jp.getArgs.toList.mkString("(", ", ", ")"))
    println(ss)
    println("arg "+ x + ", " + y)
    x + y + 1
  }

  @Pointcut("execution(* com.wellnr.examples.C.hey(..)) && this(hel)")
  def hey(hel: Hello) = {}

  @Before("hey(hel)")
  def beforeHey(jp: JoinPoint, hel: Hello) {
    val ss = String.format("ENTER %s.%s%s", jp.getSignature.getDeclaringType.getName, jp.getSignature.getName, jp.getArgs.toList.mkString("(", ", ", ")"))
    println(ss)
  }

  @After("hey(hel)")
  def afterHey(jp: JoinPoint, hel: Hello) {
    val ss = String.format("ENTER %s.%s%s", jp.getSignature.getDeclaringType.getName, jp.getSignature.getName, jp.getArgs.toList.mkString("(", ", ", ")"))
    println(ss)
    hel.say()
    val c = hel.asInstanceOf[C]
    c.plnStr("aaa")
  }
}

trait Hello {
  def say(): Unit
}

object Hello {
  def defaultImpl: Hello = new Hello {
    def say() = println("defaultImpl")
  }
}
