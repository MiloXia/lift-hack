package hack.lift

import org.aspectj.lang.{ProceedingJoinPoint, JoinPoint}
import org.aspectj.lang.annotation.{Around, Before, Pointcut, Aspect}

import net.liftweb.http.provider.{HTTPResponse, HTTPRequest}

@Aspect
class ReqAspect {
  @Pointcut("execution(* service(..)) && this(net.liftweb.http.provider.HTTPProvider) && args(req, resp, f)")
  def serviceP(req: HTTPRequest, resp: HTTPResponse, f: => Unit) = {}

  @Around("serviceP(req, resp, f)")
  def arroundService(jp: JoinPoint, pjp: ProceedingJoinPoint, req: HTTPRequest, resp: HTTPResponse, f: => Unit) = {
    val s = String.format("-----ENTER %s.%s%s", jp.getSignature.getDeclaringType.getName, jp.getSignature.getName, jp.getArgs.toList.mkString("(", ", ", ")"))
    println(s)
    println(req.url)
    val startTime = System.nanoTime()
    val ret = pjp.proceed()
    val endTime = System.nanoTime()
    println("fuck lift" + " took " + (endTime-startTime))
    ret
  }
}
