package co.girdport.akka

import scala.io.Source
import akka.actor.ActorSelection.toScala
import akka.actor.Actor
import akka.actor.Props

object Console extends Node("localhost") with App {

  new StandaloneNode
  new StandaloneNode
  new StandaloneNode
  new StandaloneNode

  val cli = system.actorOf(Props[InteractorCLI])

  val stdInReader = new Thread {
    override def run() {
      val i = Source.stdin.getLines
      while (!system.isTerminated) {
        print("\n>")
        val line = i.next
        if (!(line isEmpty)) {
          cli ! Input(line)
          Thread.sleep(500)
        }
        print(">")
      }
    }
  } start
}

case class Input(val line: String)

class InteractorCLI extends Actor {
  val manager = context.actorFor("/user/node-manager")
  override def receive = {
    case Input(textCommand) => manager ! new UserCommand(textCommand)
    case Info(line) => println(sender.path + ": " + line)
  }
}

