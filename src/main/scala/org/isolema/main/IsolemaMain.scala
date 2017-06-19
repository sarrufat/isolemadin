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
import org.isolema.views.IsomorphismView
import vaadin.scala.renderers.ClickableRenderer
import org.isolema.views.IntroView
import org.isolema.views.GroupsView
import org.isolema.domain.model.HWordT

@WebServlet(urlPatterns = Array("/*"))
class Servlet extends ScaladinServlet(
  ui = classOf[IsolemaMainUI])
class IsolemaMainUI extends UI(theme = "valo-flatdark", title = "ISOLEMA") {

  var currentResultData: List[HWordT] = List()
  val contentLayout = new VerticalLayout { layout ⇒
    sizeFull()
    margin = true
  }

  val headerLayout = new VerticalLayout {
    margin = true
    spacing = true

  }
  val layout = new VerticalLayout {
    sizeFull()
    styleName = "bookBackimage";
  }

  override def init(request: ScaladinRequest) {
    val navigator = new Navigator(this, contentLayout) {
      addView(SearchView.VIEW1, new SearchView)
      addView(IsomorphismView.VIEW, new IsomorphismView)
      addView(IntroView.VIEW, new IntroView)
      addView(GroupsView.VIEW, new GroupsView)
    }
    navigator_=(navigator)
    content_=(layout)
    headerLayout.add(buildApplicationMenu(navigator))
    layout.add(headerLayout)
    layout.add(contentLayout, ratio = 1)
    navigator.navigateTo(IntroView.VIEW)
  }
  private def buildApplicationMenu(navigator: Navigator): HorizontalLayout = new HorizontalLayout {
    width = 100 pct;
    height = 25 px;
    val menuBar = new MenuBar {
      addItem("Buscar", (e: MenuBar.MenuItem) ⇒ navigator.navigateTo(SearchView.VIEW1))
      addItem("Intro", (e: MenuBar.MenuItem) ⇒ navigator.navigateTo(IntroView.VIEW))
    }
    addComponent(menuBar)
  }

}

object SearchView {
  val VIEW1 = "SearchView"

  def renderWord(pre: String, mid: String, suf: String): String = (<span>{ pre }<span class="v-label-colored">{ mid }</span>{ suf }</span>).toString()
  def renderOccur(word: String, mask: String): String = {
    val ret = for ((ch, idx) ← mask.zipWithIndex) yield {
      ch match {
        case '_' ⇒ word.charAt(idx).toString()
        case _   ⇒ (<span class="v-label-colored">{ word.charAt(idx) }</span>).toString()
      }
    }
    "<span>" + ret.mkString + "</span>"
  }
}

class SearchView extends Panel with Navigator.View {
  // val label = Label("Caracteres (>3)")
  var navigator: Navigator = null
  def init() {
    val repo = MongoRepository
    val searchField = new TextField
    searchField.caption_=("Caracteres (>3)")
    searchField.immediate = true
    val occurCheck = new CheckBox()
    occurCheck.caption = "Sólo con ocurrencias"
    occurCheck.value = true
    val grid = new Grid
    grid.caption = "Aciertos"
    grid.selectionMode = SelectionMode.None
    val colId = grid.addColumn[String]("word")
    colId.hidden = true
    val col1 = grid.addColumn[String]("matchWord")
    col1.headerCaption = "Palabra"
    col1.renderer = HtmlRenderer()
    val col2 = grid.addColumn[String]("ocurr")
    col2.headerCaption = "Ocurrencias"
    col2.renderer = HtmlRenderer()
    val col3 = grid.addColumn[Int]("isoCount")
    col3.headerCaption = "Isomorfismos"
    col3.renderer = ButtonRenderer((clickListener: ClickableRenderer.RendererClickEvent) ⇒ {
      val word = clickListener.grid.container.getItem(clickListener.itemId).getProperty("word").value
      word.foreach { w ⇒
        val word = w.asInstanceOf[String].toLowerCase()
        navigator.navigateTo(org.isolema.views.IsomorphismView.VIEW + "/" + word)
      }
    })

    grid.heightByRows = 11

    val layout = new VerticalLayout() {

      sizeFull()
      val hlay = new HorizontalLayout() {
        add(searchField)
        add(occurCheck)
        setAlignment(occurCheck, Alignment.BottomCenter)
        spacing = true
      }
      add(hlay)
      add(grid)
    }
    layout.margin = true
    content = layout
    // search funtion
    def viewAction(text: String, withoutOccur: Boolean) = {
      val result = HashedWordService.getWordLike(text.toLowerCase(), withoutOccur)(repo)
      grid.container.removeAllItems()
      for (res ← result; item ← res) {
        grid.addRow(item.word, item.getPreMidSuf(text)(SearchView.renderWord), SearchView.renderOccur(item.word, item.decomposeWordByOccur()), item.isoCount)
      }
    }
    searchField.textChangeListeners += { event ⇒
      if (event.text.length() > 3)
        viewAction(event.text, occurCheck.booleanValue)
    }

    occurCheck.valueChangeListeners += { event ⇒
      if (searchField.value.getOrElse("").length() > 3)
        viewAction(searchField.value.get, occurCheck.booleanValue)
    }
  }

  override def enter(event: Navigator.ViewChangeEvent) {
    val viewName = event.viewName.getOrElse("")
    navigator = event.navigator
  }

  init()
  caption = "Buscar palabras"

}