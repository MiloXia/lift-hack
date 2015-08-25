package demo.helloworld.snippet

import scala.xml.NodeSeq

import net.liftweb.http.SHtml
import net.liftweb.util.Helpers._

class HelloWorld {
  def howdy = {
    println("hello")
    <span>Welcome to helloworld {new _root_.java.util.Date}</span>
  }
  def greet (xhtml : NodeSeq) : NodeSeq = State.greet(xhtml)
}

object State {
  def greet (xhtml : NodeSeq) : NodeSeq = {
    var name = ""
    def process() = {
      println(name)
    }
    bind("form", xhtml,
      "name" -> SHtml.text(name, name = _),
      "greet" -> SHtml.submit("Greet", process))
  }
}

