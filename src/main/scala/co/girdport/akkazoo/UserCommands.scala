package co.girdport.akkazoo

import akka.actor.ActorContext
import akka.actor.ExtendedActorSystem
import akka.actor.ActorRef

case class UserCommand(val instruction: Instruction) {
  def this(command: String) {
    this(command match {
      case "down" => Shutdown
      case "config" => ShowConfig
      case "describe" => Describe
      case _ => UnknownCommand
    })
  }
}

trait Instruction
case object UnknownCommand extends Instruction
case object Shutdown extends Instruction
case object ShowConfig extends Instruction
case object Describe extends Instruction {
  def actors()(implicit context: ActorContext, sender: ActorRef) = {
    context.children.foreach(child => child.forward(Describe)(context))
    context.sender ! Info("children: " + context.children.size)
  }
}

case class Info(val line: String) 
