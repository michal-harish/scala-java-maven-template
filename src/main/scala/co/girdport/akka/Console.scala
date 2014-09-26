package co.girdport.akka

import scala.io.Source
import akka.actor.ActorSelection.toScala
import akka.actor.Actor
import akka.actor.Props

object Console extends App with ManagedNode {

  init("localhost")
  Thread.sleep(1000)
  val inputProcessor = system.actorOf(Props[UserInputProcessor])

  val stdInReader = new Thread {
    override def run() {
      val i = Source.stdin.getLines
      print("\n>")
      while (!system.isTerminated && i.hasNext) {
        val line = i.next
        if (!(line isEmpty)) {
            inputProcessor ! line
            Thread.sleep(250)
            println("\n")
        }
        print(">")
      }
    }
  } start
}

class UserInputProcessor extends Actor {
  val manager = context.actorFor("/user/node-manager")
  override def receive = {
      case "exit" => manager ! new UserCommand(Shutdown)
      case "config" => manager ! new UserCommand(ShowConfig)
      case "describe" => manager ! new UserCommand(Describe)
      case _ => manager ! new UserCommand(UnknownCommand)
  }
}

