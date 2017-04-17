package org.isolema.main

import org.isolema.domain.repository.MongoRepository
import org.isolema.domain.HashedWordService

import javax.servlet.annotation.WebServlet
import vaadin.scala._
import vaadin.scala.server.ScaladinRequest
import vaadin.scala.server.ScaladinServlet
import vaadin.scala.Grid.Column
import vaadin.scala.renderers.HtmlRenderer
import vaadin.scala.renderers.ButtonRenderer

@WebServlet(urlPatterns = Array("/*"))
class Servlet extends ScaladinServlet(
  ui = classOf[IsolemaMainUI])
class IsolemaMainUI extends UI(theme = "valo-flatdark", title = "ISOLEMA") {

  val contentLayout = new VerticalLayout { layout =>
    sizeFull()
    margin = true
  }

  val headerLayout = new VerticalLayout {
    margin = true
    spacing = true

  }
  val layout = new VerticalLayout {
    sizeFull()
  }

  override def init(request: ScaladinRequest) {
    val navigator = new Navigator(this, contentLayout) {
      addView(SearchView.VIEW1, new SearchView)
    }
    navigator_=(navigator)
    content_=(layout)
    headerLayout.add(buildApplicationMenu(navigator))
    layout.add(headerLayout)
    layout.add(contentLayout, ratio = 1)
    navigator.navigateTo(SearchView.VIEW1)
  }
  private def buildApplicationMenu(navigator: Navigator): HorizontalLayout = new HorizontalLayout {
    width = 100 pct;
    height = 25 px;
    val menuBar = new MenuBar {
      addItem("Buscar", (e: MenuBar.MenuItem) => navigator.navigateTo(SearchView.VIEW1))
    }
    addComponent(menuBar)
  }

}

object SearchView {
  val VIEW1 = "SearchView"

  def renderWord(pre: String, mid: String, suf: String): String = (<span>{ pre }<span class="v-label-colored">{ mid }</span>{ suf }</span>).toString()
  def renderOccur(word: String, mask: String): String = {
    val ret = for ((ch, idx) <- mask.zipWithIndex) yield {
      ch match {
        case '_' => word.charAt(idx).toString()
        case _   => (<span class="v-label-colored">{ word.charAt(idx) }</span>).toString()
      }
    }
    "<span>" + ret.mkString + "</span>"
  }
}

class SearchView extends VerticalLayout with Navigator.View {
  // val label = Label("Caracteres (>3)")

  def init() {
    val repo = MongoRepository
    val field = new TextField
    field.caption_=("Caracteres (>3)")
    field.immediate = true
    val grid = new Grid
    grid.caption = "Aciertos"
    grid.selectionMode = SelectionMode.None
    val col1 = grid.addColumn[String]("word")
    col1.headerCaption = "Palabra"
    col1.renderer = HtmlRenderer()
    val col2 = grid.addColumn[String]("ocurr")
    col2.headerCaption = "Ocurrencias"
    col2.renderer = HtmlRenderer()
    val col3 = grid.addColumn[Int]("isoCount")
    col3.headerCaption = "Isomorfismos"
    col3.renderer = new ButtonRenderer()
   
    
    grid.heightByRows = 11

    val layout = new VerticalLayout() {
      sizeFull()
      add(field)
      add(grid)
    }
    layout.margin = true
    add(layout)
    field.textChangeListeners += { event =>
      if (event.text.length() > 3) {
        val result = HashedWordService.getWordLike(event.text)(repo)
        grid.container.removeAllItems()
        for (res <- result; item <- res) {
          grid.addRow(item.getPreMidSuf(event.text)(SearchView.renderWord), SearchView.renderOccur(item.word, item.decomposeWordByOccur()), item.isoCount)
        }
      }
    }
  }

  override def enter(event: Navigator.ViewChangeEvent) {
    val viewName = event.viewName.getOrElse("")

  }

  init()

}