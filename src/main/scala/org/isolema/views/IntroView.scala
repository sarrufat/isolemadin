package org.isolema.views

import vaadin.scala.VerticalLayout
import vaadin.scala.Navigator
import vaadin.scala.Label
import vaadin.scala.ValoTheme
import vaadin.scala.Embedded
import vaadin.scala.server.ThemeResource
import vaadin.scala.Panel

object IntroView {
  val VIEW = "IntroView"
}

class IntroView extends Panel with Navigator.View {
  val text1 = """   
    <p>Esta es una pequeña aplicación experimental para jugar con los isomorfismos del español.</P>
      <p></p>
      <p>Esta aplicaci&oacute;n contiene una base de datos con
         m&aacute;s de 87000 palabras. Cada palabra tiene asociado un
          c&oacute;digo calculado en base a las ocurrencias de cada letra
          &nbsp;de la palabra. As&iacute; por ejemplo&nbsp;
           &lsquo;<strong>isomorfo</strong>&rsquo; tiene asociado
            el c&oacute;digo &lsquo;<strong>01232452</strong>&rsquo;.
             Entonces se cumple la propiedad que todas las palabras que comparten
             el mismo c&oacute;digo de ocurrencias son isom&oacute;rficas como por ejemplo: <em>isomorfo, aferente, creyente</em> &hellip;</p>
    """
  override def enter(event: Navigator.ViewChangeEvent) {
    val viewName = event.viewName.getOrElse("")
    //    println(s"viewName = ${viewName}, parameters = ${event.parameters}")
    // init(event.parameters, event.navigator)
  }
  caption = "Introducción"
  val label = Label(text1)
  label.contentMode = Label.ContentMode.Html
  label.styleName = ValoTheme.LabelHuge
  val layout = new VerticalLayout
  layout.margin = true
  layout.addComponent(label)
  content = layout
  
}