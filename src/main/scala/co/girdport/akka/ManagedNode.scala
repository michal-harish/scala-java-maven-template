package co.girdport.akka

import scala.collection.JavaConverters.asScalaBufferConverter

import org.I0Itec.zkclient.IZkChildListener
import org.I0Itec.zkclient.ZkClient

import com.typesafe.config.ConfigFactory
import com.typesafe.config.ConfigValueFactory

import akka.actor.Actor
import akka.actor.ActorContext
import akka.actor.ActorRef
import akka.actor.ActorSelection
import akka.actor.ActorSelection.toScala
import akka.actor.ActorSystem
import akka.actor.Props
import akka.actor.actorRef2Scala
import grizzled.slf4j.Logger

object HelloSystemNodeStandalone extends ManagedNode with App {
  init("localhost")
}

trait ManagedNode {
  val log = Logger[this.type]
  val systemName = "HelloSystem"
  val zk = new ZkClient("bl-mharis-d02:2181")
  if (!zk.exists("/akka")) zk.createPersistent("/akka")
  val zkSystemPath = "/akka/" + systemName
  if (!zk.exists(zkSystemPath)) zk.createPersistent(zkSystemPath)

  var system: ActorSystem = null
  var port = 4711
  var manager: ActorRef = null
  var peers: List[ActorSelection] = List()

  zk.subscribeChildChanges(zkSystemPath, new IZkChildListener() {
    override def handleChildChange(zkSystemPath: String, currentChilds: java.util.List[String]) {
      this.synchronized {
        peers = currentChilds.asScala.toList.map(
          a => system.actorSelection("akka.tcp://" + systemName + "@" + a + "/user/node-manager"))
        manager ! new PeerList(peers)
      }
    }
  })

  def init(hostname: String): ManagedNode = {
    var success = false
    while (!success) {
      if (!zk.exists(zkSystemPath + "/" + hostname + ":" + port)) {
        try {
          val config = ConfigFactory.load()
            .withValue("akka.remote.netty.tcp.hostname", ConfigValueFactory.fromAnyRef(hostname))
            .withValue("akka.remote.netty.tcp.port", ConfigValueFactory.fromAnyRef(port))
          system = ActorSystem(systemName, config)
          success = true
        } catch {
          case e: Exception => println(e.getMessage)
        }
      }
      if (!success) port = port + 1
    }

    //FIXME remove this and do it by catching shutdown event
    new Thread {
      override def run() {
        system.awaitTermination
        zk.close
      }
    } start

    manager = system.actorOf(Props[NodeManager], name = "node-manager")

    zk.createEphemeral(zkSystemPath + "/" + hostname + ":" + port)

    this
  }

}

class NodeManager extends Actor {
  var peers: List[ActorSelection] = List()
  def receive = {
    case msg: PeerList => peers = msg.peers
    case Describe => Describe.actors()
    case ShowConfig => sender ! context.system.settings.config.toString
    case Shutdown => context.system.shutdown
    case msg: UserCommand => for (p <- peers) { p ! msg.instruction }
    case info: String => println(info)
    case _ => sender ! "Unprocessed message"
  }
}

case class PeerList(peers: List[ActorSelection])

class UserCommand(val instruction: Instruction)
trait Instruction
case object UnknownCommand extends Instruction
case object Shutdown extends Instruction
case object ShowConfig extends Instruction
case object Describe extends Instruction {
  def actors()(implicit context: ActorContext) = {
    context.children.foreach(child => child.forward(Describe)(context))
    val config = context.system.settings.config
    val address = config.getAnyRef("akka.remote.netty.tcp.hostname") + ":" + config.getAnyRef("akka.remote.netty.tcp.port")
    val path: String = context.self.path.toString.replace(context.self.path.address.toString, context.self.path.address.toString + "@" + address)
    context.sender ! path
  }
}

