package org.isolema.views

import vaadin.scala.Panel
import vaadin.scala.Navigator
import vaadin.scala.VerticalLayout
import org.isolema.main.IsolemaMainUI
import org.isolema.domain.model.HWordT
import org.isolema.util.HashIsomorphism
import org.vaadin.visjs.networkDiagram.{ Node ⇒ GNode, Edge, NetworkDiagram }
import org.vaadin.visjs.networkDiagram.options.Options

object GroupsView {
  val VIEW = "GroupsView"
}

class GroupsView extends Panel with Navigator.View {

  private def getGroups(data: List[HWordT]) = data.map(item ⇒ (HashIsomorphism.decomposeWordByCode(item.word), item)).groupBy(t ⇒ t._1).map(t ⇒ (t._1 -> t._2.map(_._2)))

  var netDia = None: Option[NetworkDiagram]
  val contLayout = new VerticalLayout

  private def initNetwork(data: Map[String, List[HWordT]]) = {
    netDia.foreach(n ⇒ contLayout.p.removeComponent(n))
    val opt = new Options
    netDia = Some(new NetworkDiagram(opt))
    netDia.foreach { nd ⇒
      contLayout.p.addComponent(nd);
      nd.setSizeFull()
      val rootNodes = for {
        (key, items) ← data
        node = new GNode(key, key, GNode.Shape.box, "root")
      } yield {
        nd.addNode(node)
        for (item ← items) {
          val cnode = new GNode(item.word, item.word)
          nd.addNode(cnode)
          val edge = new Edge(node.getId, cnode.getId)
          nd.addEdge(edge)
        }
        node
      }
      rootNodes.zip(rootNodes.tail).map ( tup => new Edge(tup._1.getId, tup._2.getId)).foreach(nd.addEdge(_))
    }

  }
  override def enter(event: Navigator.ViewChangeEvent) {
    val data = getGroups(ui.asInstanceOf[IsolemaMainUI].currentResultData)
    initNetwork(data)
  }
  caption = "Grupos Morfológicos"

  contLayout.spacing = true
  //  contLayout.p.addComponent(netDia)
  contLayout.sizeFull()
  content = contLayout
  sizeFull()
}