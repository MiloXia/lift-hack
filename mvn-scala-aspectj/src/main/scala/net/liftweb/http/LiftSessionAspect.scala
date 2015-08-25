package net.liftweb.http

import java.io.{FileOutputStream, ObjectOutputStream}

import net.rubyeye.xmemcached.{MemcachedClient, XMemcachedClientBuilder}
import net.rubyeye.xmemcached.command.BinaryCommandFactory
import net.rubyeye.xmemcached.impl.KetamaMemcachedSessionLocator
import net.rubyeye.xmemcached.utils.AddrUtil
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.{After, Aspect, Before, DeclareMixin, Pointcut}

import net.liftweb.common.{Full, Box}

@Aspect
class LiftSessionAspect {
  @DeclareMixin("net.liftweb.http.LiftSession")
  def mixinLiftSession: Serializable = null

  @DeclareMixin("net.liftweb.http.provider.servlet.HTTPServletSession")
  def mixinHTTPServletSession: Serializable = null

  //SessionMaster
  @Pointcut("execution(* net.liftweb.http.SessionMaster$.getSession(..))")
  def getSessionP() = {}

  @Before("getSessionP()")
  def beforeGetSession(jp: JoinPoint): Unit = {
    val s = String.format("-----ENTER %s.%s%s", jp.getSignature.getDeclaringType.getName, jp.getSignature.getName, jp.getArgs.toList.mkString("(", ", ", ")"))
    println(s)
  }

  @Pointcut("execution(* net.liftweb.http.SessionMaster$.addSession(..)) && args(liftSession, req, userAgent, ipAddress)")
  def addSessionP(liftSession: LiftSession, req: Req,
                  userAgent: Box[String], ipAddress: Box[String]) = {}

  @After("addSessionP(liftSession, req, userAgent, ipAddress)")
  def beforeAddSession(jp: JoinPoint, liftSession: LiftSession, req: Req,
                       userAgent: Box[String], ipAddress: Box[String]): Unit = {
    val s = String.format("-----ENTER %s.%s%s", jp.getSignature.getDeclaringType.getName, jp.getSignature.getName, jp.getArgs.toList.mkString("(", ", ", ")"))
    println(s)
//    Cache.add(liftSession.uniqueId, liftSession)
  }

  @Pointcut("execution(* net.liftweb.http.SessionMaster$.sendMsg(..)) && args(in)")
  def sendMsgP(in: Any) = {}

  @Before("sendMsgP(in)")
  def beforeSendMsg(jp: JoinPoint, in: Any): Unit = {
    val s = String.format("-----ENTER %s.%s%s", jp.getSignature.getDeclaringType.getName, jp.getSignature.getName, jp.getArgs.toList.mkString("(", ", ", ")"))
    println(s)
    in match {
      case RemoveSession(sessionId) =>
        println("removeSession")
    }
  }

  //LiftSession
  private val msgCallbackSync = new Object

  @Pointcut("execution(* net.liftweb.http.LiftSession.updateFunctionMap(..)) && args(funcs, uniqueId, when)")
  def updateFunctionMapP(funcs: Map[String, S.AFuncHolder], uniqueId: String, when: Long) = {}

  @After("updateFunctionMapP(funcs, uniqueId, when)")
  def afterUpdateFunctionMap(jp: JoinPoint, funcs: Map[String, S.AFuncHolder], uniqueId: String, when: Long) {
    val s = String.format("-----ENTER %s.%s%s", jp.getSignature.getDeclaringType.getName, jp.getSignature.getName, jp.getArgs.toList.mkString("(", ", ", ")"))
    println(s)
    msgCallbackSync.synchronized {
      funcs.foreach {
        case (name, func) =>
          Cache.add(name, if (func.owner == Full(uniqueId)) func else func.duplicate(uniqueId))
          val f = Cache.get[S.AFuncHolder](name)
          println(f)
      }
    }
  }

}

object Cache {
  lazy val memcached: MemcachedClient = {
    val builder = new XMemcachedClientBuilder(AddrUtil.getAddresses("localhost:11211"))
    builder.setFailureMode(false)
    builder.setCommandFactory(new BinaryCommandFactory())
    builder.setConnectionPoolSize(1)
    builder.setSessionLocator(new KetamaMemcachedSessionLocator())
    builder.build()
  }
  def add(key: String, expiry: Int, value: Object) = memcached.add(key, expiry, value)
  def add(key: String, value: Object) = memcached.add(key, 0, value)

  def get[T](key: String): T = memcached.get[T](key)

  def delete(key: String) = memcached.delete(key)

  def test(o: Any) = {
    val out  = new ObjectOutputStream(new FileOutputStream("seria"))
    try {
      out.writeObject(o)
      println("object has been written..")
    } catch {
      case e: Exception => e.printStackTrace()
    } finally {
      out.close()
    }
  }
}