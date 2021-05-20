package rs.ac.bg.etf.ms1fp.nj203078m

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

      var reactLive = false

      val tabs: TabbedPane = new TabbedPane {

        import TabbedPane._

        val buttons: FlowPanel = new FlowPanel {
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
            listenTo(paintLabels, paintTicks, snapTicks, live)
            reactions += {
              case ButtonClicked(`paintLabels`) =>
                slider.paintLabels = paintLabels.selected
              case ButtonClicked(`paintTicks`) =>
                slider.paintTicks = paintTicks.selected
              case ButtonClicked(`snapTicks`) =>
                slider.snapToTicks = snapTicks.selected
              case ButtonClicked(`live`) =>
                reactLive = live.selected
            }
          }
          contents += new Button(Action("Center Frame") {
            centerOnScreen()
          })
        }
        pages += new Page("Buttons"     , buttons)

        val password: FlowPanel = new FlowPanel {
          contents += new Label("Enter your secret password here ")
          val field = new PasswordField(10)
          contents += field
          val label = new Label(field.text)
          contents += label
          listenTo(field)
          reactions += {
            case EditDone(`field`) => label.text = field.password.mkString
          }
        }

        pages += new Page("Password", password, "Password tooltip")
      }

      val list: ListView[TabbedPane.Page] = new ListView(tabs.pages) {
        selectIndices(0)
        selection.intervalMode = ListView.IntervalMode.Single
        renderer = ListView.Renderer(_.title)
      }
      val center: SplitPane = new SplitPane(Orientation.Vertical, new ScrollPane(list), tabs) {
        oneTouchExpandable = true
        continuousLayout = true
      }
      layout(center) = Center

      /*
       * This slider is used above, so we need lazy initialization semantics.
       * Objects or lazy vals are the way to go, but objects give us better
       * type inference at times.
       */
      object slider extends Slider {
        min   = 0
        value = tabs.selection.index
        max   = tabs.pages.size - 1
        majorTickSpacing = 1
      }

      layout(slider) = South

      /*
       * Establish connection between the tab pane, slider, and list view.
       */
      listenTo(slider)
      listenTo(tabs.selection)
      listenTo(list.selection)
      reactions += {
        case ValueChanged(`slider`) =>
          if (!slider.adjusting || reactLive) tabs.selection.index = slider.value
        case SelectionChanged(`tabs`) =>
          slider.value = tabs.selection.index
          list.selectIndices(tabs.selection.index)
        case SelectionChanged(`list`) =>
          if (list.selection.items.length == 1)
            tabs.selection.page = list.selection.items.head
      }
    }
  }
}