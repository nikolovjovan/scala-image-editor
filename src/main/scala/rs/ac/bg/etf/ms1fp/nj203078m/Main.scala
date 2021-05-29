package rs.ac.bg.etf.ms1fp.nj203078m

import rs.ac.bg.etf.ms1fp.nj203078m.gui.Drawing

import java.awt.image.BufferedImage
import javax.swing.{Icon, JLabel}
import scala.swing._
import scala.swing.Swing._
import scala.swing.event._
import scala.swing.ListView._

object Main extends SimpleSwingApplication {
  def top: Frame = new MainFrame {
    title = "Scala Image Editor"

    /*
     * Create a menu bar with a couple of menus and menu items and
     * set the result as this frame's menu bar.
     */
    menuBar = new MenuBar {
      contents += new Menu("File") {
        contents += new MenuItem(Action("New Project") {
          // TODO: Call new project!
          println("New project!")
        })
        contents += new MenuItem(Action("Load Project") {
          // TODO: Call open project!
          println("Open project!")
        })
        contents += new Separator
        contents += new MenuItem(Action("Save Project") {
          // TODO: Call save project!
          println("Save project!")
        })
        contents += new MenuItem(Action("Save Project As") {
          // TODO: Call save project as!
          println("Save project as!")
        })
        contents += new Separator
        contents += new MenuItem(Action("Exit") {
          quit()
        })
      }
      contents += new Menu("About") {
        println("About project!")
      }
    }

    /*
     * The root component in this frame is a panel with a border layout.
     */
    contents = new BorderPanel {
      import BorderPanel.Position._

      val drawing: ScrollPane = new ScrollPane(new Drawing(true)) {
        preferredSize = new Dimension(200, 200)
      }

      val controls: BoxPanel = new BoxPanel(Orientation.Vertical) {
        border = Swing.EmptyBorder(5, 5, 5, 5)

        contents += new BoxPanel(Orientation.Vertical) {
          border = CompoundBorder(TitledBorder(EtchedBorder, "Radio Buttons"), EmptyBorder(5, 5, 5, 10))
          val a = new RadioButton("Green Vegetables")
          val b = new RadioButton("Red Meat")
          val c = new RadioButton("White Tofu")
          val mutex = new ButtonGroup(a, b, c)
          contents ++= mutex.buttons
        }
        contents += new BoxPanel(Orientation.Vertical) {
          border = CompoundBorder(TitledBorder(EtchedBorder, "Check Boxes"), EmptyBorder(5, 5, 5, 10))
          val paintLabels = new CheckBox("Paint Labels")
          val paintTicks = new CheckBox("Paint Ticks")
          val snapTicks = new CheckBox("Snap To Ticks")
          val live = new CheckBox("Live")
          contents ++= Seq(paintLabels, paintTicks, snapTicks, live)
        }
        contents += new Button(Action("Center Frame") {
          centerOnScreen()
        })
      }

      val center: SplitPane = new SplitPane(Orientation.Vertical, drawing, controls) {
        oneTouchExpandable = true
        continuousLayout = true
        resizeWeight = 1
      }

      layout(center) = Center
    }
    pack()
  }
}