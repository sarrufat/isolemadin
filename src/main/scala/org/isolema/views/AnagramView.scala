package org.isolema.views

import vaadin.scala.Panel
import vaadin.scala.Navigator
import vaadin.scala.VerticalLayout
import org.isolema.main.IsolemaMainUI
import org.isolema.domain.model.HWordT
import org.isolema.util.HashIsomorphism
import org.vaadin.visjs.networkDiagram.{ Node ⇒ GNode, Edge, NetworkDiagram }
import org.vaadin.visjs.networkDiagram.options.Options
import vaadin.scala.Label
import vaadin.scala.ValoTheme
import vaadin.scala.Layout
import vaadin.scala.Grid
import vaadin.scala.SelectionMode
import org.isolema.domain.service.AnagramaService
import org.isolema.domain.repository.MongoRepository
import org.isolema.domain.model.AnagramModel

object AnagramView {
  val VIEW = "AnagramView"
}

class AnagramView extends Panel with Navigator.View {

  private def getGroups(data: List[HWordT]) = data.map(item ⇒ (HashIsomorphism.decomposeWordByCode(item.word), item)).sortBy(_._1).groupBy(t ⇒ t._1).map(t ⇒ (t._1 -> t._2.map(_._2)))

  val contLayout = new VerticalLayout
  val text1 = """Aquí se muestran los isomorfismos que, a su vez, también se forman por medio de anagrama. Los podríamos  llamar isomorfismos perfectos. Se muestran con el color de fondo los que son isomorfismos con ocurrencias."""

  val text2 = """Anagrama: Dislocación de los fonemas o sílabas de una o más palabras para obtener, con nueva agrupación, palabas o frases distintas."""
  val lab1 = Label(text1)
  val lab2 = Label(text2)
  lab1.styleName = ValoTheme.LabelLarge
  lab2.styleName = ValoTheme.LabelSmall
  val grid = new Grid
  grid.caption = "Anagramas Isomórficos"
  grid.selectionMode = SelectionMode.None
  val col1 = grid.addColumn[String]("anagrams")
  col1.headerCaption = "Anagramas"
  val col2 = grid.addColumn[AnagramModel]("modelObj")
  col2.hidden = true
  var dataInit = false
  override def enter(event: Navigator.ViewChangeEvent) {
    if (!dataInit) {
      grid.container.removeAllItems()
      val operation = AnagramaService.getAnamgramas()(MongoRepository)
      for (dlist ← operation; data ← dlist; anagrams = data.anagramas) {
        grid.addRow(anagrams.map(_.word).mkString(", "), data)
      }
      grid.sort("anagrams", vaadin.scala.SortDirection.Ascending)
      dataInit = true
    }

  }
  // Grid style
  grid.cellStyleGenerator = { reference ⇒
    val modelOpt = reference.item.getProperty("modelObj").value.asInstanceOf[Option[AnagramModel]]
    // Test if has occurrences
    modelOpt.map { model ⇒ model.anacode.forall(code ⇒ code == '0' || code == '1') } map (if (_) "" else "perfect")
  }
  caption = "Isormorfismos y Anagramas"
  val textLay = new VerticalLayout
  textLay.margin = Layout.Margin(true, true, true, true)
  textLay.add(lab1)
  textLay.add(lab2)
  textLay.add(grid)
  //  contLayout.spacing = true
  contLayout.add(textLay)

  contLayout.sizeFull()
  content = contLayout
  sizeFull()
}