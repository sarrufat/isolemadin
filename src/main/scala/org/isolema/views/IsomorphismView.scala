package org.isolema.views

import org.isolema.domain.HashedWordService
import org.isolema.domain.repository.MongoRepository
import org.isolema.main.SearchView
import org.isolema.util.HashIsomorphism

import vaadin.scala.Button
import vaadin.scala.Grid
import vaadin.scala.HorizontalLayout
import vaadin.scala.Label
import vaadin.scala.Navigator
import vaadin.scala.SelectionMode
import vaadin.scala.ValoTheme
import vaadin.scala.VerticalLayout
import vaadin.scala.Panel
import org.isolema.main.IsolemaMainUI

object IsomorphismView {
  val VIEW = "IsomorphismView"
}

class IsomorphismView extends Panel with Navigator.View {
  var nv: Option[Navigator] = None
  var capT1: Option[Label] = None
  var grid: Option[Grid] = None
  val groupButt = Button("Ver Grupos")
  groupButt.clickListeners += { event ⇒
    nv.get.navigateTo(GroupsView.VIEW)
  }
  override def enter(event: Navigator.ViewChangeEvent) {
    val viewName = event.viewName.getOrElse("")
    println(s"viewName = ${viewName}, parameters = ${event.parameters}")
    init(event.parameters, event.navigator)
  }

  def init(word: String, navigator: Navigator) {
    nv = Some(navigator)
    val code = HashIsomorphism.hashingWord(word)
    capT1.get.caption = s"${word} → ${code}"
    val repo = MongoRepository
    val result = HashedWordService.getIsomorphisms(word)(repo)
    ui.asInstanceOf[IsolemaMainUI].currentResultData = result.getOrElse(List())
    grid.get.container.removeAllItems()
    for (res ← result; item ← res) {
      grid.get.addRow(item.word, HashIsomorphism.decomposeWordByCode(item.word))
    }
    grid.get.sort("spell", vaadin.scala.SortDirection.Ascending)
  }

  private def initial() {
    val backB = new Button() {
      caption = "Volver"
      styleName = ValoTheme.ButtonTiny
      clickListeners += { event ⇒
        nv.get.navigateTo(SearchView.VIEW1)
      }
    }
    val vhH1 = new HorizontalLayout {
      val cap1 = new Label {
        caption = Some("->")
      }
      capT1 = Some(cap1)
      cap1.styleName = ValoTheme.LabelHuge
      addComponent(cap1)
      addComponent(groupButt)
      spacing = true
    }
    grid = Some(new Grid)
    grid.get.caption = "Isomorfismos"
    grid.get.selectionMode = SelectionMode.None
    val col1 = grid.get.addColumn[String]("word")
    col1.headerCaption = "Palabra"
    val col2 = grid.get.addColumn[String]("spell")
    col2.headerCaption = "Forma"
    val layout = new VerticalLayout
    layout.addComponent(vhH1)
    layout.addComponent(grid.get)
    layout.addComponent(backB)
    layout.margin = true
    content = layout

  }

  initial()
  caption = "Isomorfismos"
}