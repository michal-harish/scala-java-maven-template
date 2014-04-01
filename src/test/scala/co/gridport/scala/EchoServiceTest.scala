package co.gridport.scala

import org.scalatest._
import co.gridport.scala._

class EchoServiceTest extends FlatSpec  {

  info("Example test with FlatSpec")

  "EchoService echo hello" should "return hello" in {
    assert(EchoService("hello") === "hello")
  }
}