package co.girdport.akkazoo

import scala.collection.JavaConverters.asScalaBufferConverter
import org.I0Itec.zkclient.IZkChildListener
import org.I0Itec.zkclient.ZkClient
import akka.actor.Actor
import akka.actor.ActorContext
import akka.actor.ActorRef
import akka.actor.ActorSelection
import akka.actor.ActorSelection.toScala
import akka.actor.ExtendedActorSystem
import akka.actor.Props
import akka.actor.Terminated
import co.gridport.akkazoo.example.MessageGenerator
import co.gridport.akkazoo.example.ExternalMessage

class AkkazooNodeManager extends Actor {
  import context._
  val hostPort = system.asInstanceOf[ExtendedActorSystem].provider.getDefaultAddress.hostPort
  var peerz: DistributedActorSelection = null //FIXME no null
  val peerListWatcher = actorOf(Props[PeerListWatcher], name = "peer-list-watcher")

  context watch peerListWatcher

  override def preStart {
    context.actorOf(Props[MessageGenerator], name="message-generator")
  }

  def receive = {
    //user interactions
    case UserCommand(instruction) => peerz forward instruction

    //distributed instructions
    case Describe => Describe.actors()
    case ShowConfig => sender ! Info(system.settings.config.toString)
    case Shutdown => context stop peerListWatcher

    //internal messages
    case PeersUpdate(selection) => peerz = selection
    case Terminated(peerListWatcher) => system shutdown
    case _ => sender ! Info("Unprocessed message")
  }
}

case class PeersUpdate(selection: DistributedActorSelection)

class PeerListWatcher extends Actor {
  import context._
  val zkSystemPath = "/akka"
  val hostPort = system.asInstanceOf[ExtendedActorSystem].provider.getDefaultAddress.hostPort
  val zk = new ZkClient(system.settings.config.getString("zookeeper.connect"))

  override def preStart {
    if (!zk.exists(zkSystemPath)) zk.createPersistent(zkSystemPath)
    zk.subscribeChildChanges(zkSystemPath, new IZkChildListener() {
      override def handleChildChange(zkSystemPath: String, currentChilds: java.util.List[String]) {
        val paths = currentChilds.asScala.toList
          .filter(node => node.startsWith(context.system.name))
          .map(systemNodeHostPort => "akka.tcp://" + systemNodeHostPort + "/user/node-manager")
        context.parent ! PeersUpdate(DistributedActorSelection(paths))
      }
    })
    zk.createEphemeral(zkSystemPath + "/" + hostPort)
  }

  override def receive = {
    case Describe => Describe.actors()
  }

  override def postStop = zk.close

}

