package co.girdport.akka

import scala.collection.JavaConverters.asScalaBufferConverter
import com.typesafe.config.ConfigFactory
import com.typesafe.config.ConfigValueFactory
import akka.actor.ActorContext
import akka.actor.ActorRef
import akka.actor.ActorSelection
import akka.actor.ActorSelection.toScala
import akka.actor.ActorSystem
import akka.actor.actorRef2Scala
import grizzled.slf4j.Logger
import akka.actor.Props
import org.I0Itec.zkclient.ZkClient

object StandaloneNode extends StandaloneNode with App 

class StandaloneNode extends Node("localhost")

class Node(hostname: String) {
  val log = Logger[Node.this.type]
  val systemName = "HelloSystem"
  val config = ConfigFactory.load().withValue("akka.remote.netty.tcp.hostname", ConfigValueFactory.fromAnyRef(hostname))
  val system: ActorSystem = ActorSystem(systemName, config)
  system.actorOf(Props[NodeManager], name = "node-manager")
}
