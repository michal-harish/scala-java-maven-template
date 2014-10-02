package co.girdport.akkazoo

import scala.io.Source
import akka.actor.ActorSelection.toScala
import akka.actor.Actor
import akka.actor.Props

trait AkkazooConsole extends AkkazooNode {
  def startConsole = {
    val cli = system.actorOf(Props[InteractorCLI])
    while (!system.isTerminated) {
      print("\n>")
      val line = Source.stdin.getLines.next
      if (!(line isEmpty)) {
        cli ! Input(line)
        Thread.sleep(250)
      }
    }
  }
}

case class Input(val line: String)

class InteractorCLI extends Actor {
  val manager = context.actorFor("/user/node-manager")
  override def receive = {
    case Input(textCommand) => manager ! new UserCommand(textCommand)
    case Info(line) => println(sender.path + ": " + line)
  }
}

