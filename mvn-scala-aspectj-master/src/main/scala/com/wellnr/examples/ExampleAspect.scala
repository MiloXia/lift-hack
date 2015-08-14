package com.wellnr.examples

import org.aspectj.lang.annotation.{Pointcut, Aspect, Before}
import org.aspectj.lang.JoinPoint

@Aspect
class ExampleAspect {

  @Pointcut("execution(* com.test.examples.ExampleApp.hello(..))")
  def helloPointcut() = {}

  @Before("helloPointcut()")
  def before(jp: JoinPoint) {
    val s = String.format("ENTER %s.%s%s", jp.getSignature.getDeclaringType.getName, jp.getSignature.getName, jp.getArgs.toList.mkString("(", ", ", ")"))
    println(s)
    println("hello ooo")
  }
  
}