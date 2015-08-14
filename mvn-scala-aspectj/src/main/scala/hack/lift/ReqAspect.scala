package hack.lift

import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.{Before, Pointcut, Aspect}

@Aspect
class ReqAspect {
  @Pointcut("execution(* service(..)) && this(net.liftweb.http.provider.HTTPProvider)")
  def creatReqPointcut() = {}

  @Before("creatReqPointcut()")
  def before(jp: JoinPoint) {
    val s = String.format("ENTER %s.%s%s", jp.getSignature.getDeclaringType.getName, jp.getSignature.getName, jp.getArgs.toList.mkString("(", ", ", ")"))
    println(s)
    println("fuck lift")
  }
}
