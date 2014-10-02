package co.gridport.akkazoo.example

import akka.actor.Actor
import scala.util.Random
import co.girdport.akkazoo.Describe

class MessageGenerator extends Actor {

  override def receive = {
    case Describe => Describe.actors
    case ExternalMessage(x) => println(x)
  }

}

case class ExternalMessage(val data:Int)