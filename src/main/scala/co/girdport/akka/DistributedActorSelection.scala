package co.girdport.akka

import akka.actor.Actor
import akka.actor.ActorRef
import akka.actor.ActorSelection
import akka.actor.ActorContext

abstract class DistributedActorSelection {
  this: ScalaDistributedActorSelection ⇒
  protected val targets: List[ActorSelection]
  def tell(msg: Any, sender: ActorRef): Unit = for (t <- targets) t.tell(msg, sender)
  def forward(msg: Any)(implicit context: ActorContext) = tell(msg, context.sender)
}

object DistributedActorSelection {
  implicit def toScala(sel: DistributedActorSelection): ScalaDistributedActorSelection = sel.asInstanceOf[ScalaDistributedActorSelection]
  def apply(paths: List[String])(implicit context: ActorContext): DistributedActorSelection = {
    new DistributedActorSelection with ScalaDistributedActorSelection {
      override val targets = paths.map(
        systemNodeHostPort => context.system.actorSelection(systemNodeHostPort))
    }
  }
}
trait ScalaDistributedActorSelection {
  this: DistributedActorSelection ⇒

  def !(msg: Any)(implicit sender: ActorRef = Actor.noSender) = tell(msg, sender)
}