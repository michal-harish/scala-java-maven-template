package co.gridport.scala

import org.scalatest._
import co.gridport.scala._

class EchoServiceTest extends FlatSpec with Matchers {

  info("Example test with FlatSpec")

  "EchoService echo hello" should "return hello" in {
    EchoService("hello") should equal("hello")
  }
}