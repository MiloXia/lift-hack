package demo.helloworld.snippet

import net.liftweb.common.Full
import net.liftweb.http.S

class Plain {
	def render = S.param("name") match {
	  case Full(name) =>
    	S.notice("Hello "+name)
        S.redirectTo("/plain")
      case _ =>
        <p>Hello, Milo !</p>
	}
}