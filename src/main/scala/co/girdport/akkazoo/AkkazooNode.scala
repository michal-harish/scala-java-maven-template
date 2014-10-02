package co.girdport.akkazoo

import akka.actor.ActorSystem
import akka.actor.Props
import grizzled.slf4j.Logger
import com.typesafe.config.ConfigFactory
import com.typesafe.config.ConfigValueFactory
import akka.actor.ActorRef

class AkkazooNode(val systemName: String, hostname: String) {
  val log = Logger[AkkazooNode.this.type]
  val config = ConfigFactory.load().withValue("akka.remote.netty.tcp.hostname", ConfigValueFactory.fromAnyRef(hostname))
  val system: ActorSystem = ActorSystem(systemName, config)
  val manager: ActorRef = system.actorOf(Props[AkkazooNodeManager], name = "node-manager")
}
