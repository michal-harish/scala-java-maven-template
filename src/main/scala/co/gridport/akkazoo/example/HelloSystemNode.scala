package co.gridport.akkazoo.example

import co.girdport.akkazoo.AkkazooNode
import akka.actor.Props
import scala.util.Random

object HelloSystemNode extends HelloSystemNode with App {
  val random = new Random
  new Thread() {
    override def run {
      while (!system.isTerminated) {
        manager ! ExternalMessage(random.nextInt)
      }
    }
  } start

}

protected class HelloSystemNode extends AkkazooNode("HelloSystem", "localhost")