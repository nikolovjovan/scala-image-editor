package rs.ac.bg.etf.ms1fp.nj203078m.gui

import javax.swing.table.AbstractTableModel
import scala.swing.Swing.{CompoundBorder, EmptyBorder, EtchedBorder, TitledBorder}
import scala.swing.event.MouseClicked
import scala.swing.{Action, BorderPanel, BoxPanel, Button, ButtonGroup, CheckBox, Dimension, Frame, MainFrame, Menu, MenuBar, MenuItem, Orientation, RadioButton, ScrollPane, Separator, SimpleSwingApplication, SplitPane, Swing, Table}

object Main extends SimpleSwingApplication {

  val drawing = new Drawing

  def top: Frame = new MainFrame {
    title = "Scala Image Editor"

    minimumSize = new Dimension(500, 400)

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
        action = Action("About") { println("About project!") }
      }
    }

    /*
     * The root component in this frame is a panel with a border layout.
     */
    contents = new BorderPanel {

      import BorderPanel.Position._

      val drawingView: ScrollPane = new ScrollPane(drawing) {
        preferredSize = new Dimension(400, 300)
      }

      val controlsView: BoxPanel = new BoxPanel(Orientation.Vertical) {
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

        val tblLayers: Table = new Table {
          autoResizeMode = Table.AutoResizeMode.AllColumns
          peer.setTableHeader(null)
          model = new AbstractTableModel {
            override def getRowCount: Int = drawing.layerManager.count
            override def getColumnCount: Int = 2
            override def getValueAt(rowIndex: Int, columnIndex: Int): Any = columnIndex match {
              case 0 => drawing.layerManager.layers(rowIndex).visible
              case _ => drawing.layerManager.layers(rowIndex).name
            }

            override def isCellEditable(rowIndex: Int, columnIndex: Int): Boolean = true

            override def setValueAt(aValue: Any, rowIndex: Int, columnIndex: Int): Unit = columnIndex match {
              case 0 =>
                drawing.layerManager.layers(rowIndex).visible = aValue.asInstanceOf[Boolean]
                println("Layer " + rowIndex + " visibility changed! Now: " + drawing.layerManager.layers(rowIndex).visible)
              case 1 =>
                drawing.layerManager.layers(rowIndex).name = aValue.asInstanceOf[String]
                println("Layer " + rowIndex + " name changed! Now: \"" + drawing.layerManager.layers(rowIndex).name)
              case _ =>
                println("Invalid layer " + rowIndex + " change!")
            }
          }
          peer.getColumnModel.getColumn(0).setMaxWidth(0)
        }

        listenTo(mouse.clicks)
        reactions += {
          case _: MouseClicked => tblLayers.peer.clearSelection()
        }

        contents += new ScrollPane(tblLayers) {
          minimumSize = new Dimension(80, 100)
          preferredSize = new Dimension(80, 100)
          listenTo(mouse.clicks)
          reactions += {
            case _: MouseClicked => tblLayers.peer.clearSelection()
          }
        }

        contents += new BoxPanel(Orientation.Horizontal) {
          contents += new Button(Action("Add") {
            drawing.layerManager.addNewLayer()
            tblLayers.model.asInstanceOf[AbstractTableModel].fireTableRowsInserted(
              drawing.layerManager.count - 1,
              drawing.layerManager.count - 1)
            tblLayers.peer.setRowSelectionInterval(0, 0)
            tblLayers.peer.setColumnSelectionInterval(1, 1)
          })
          contents += new Button(Action("Remove") {
            if (tblLayers.selection.rows.size > 0) {
              drawing.layerManager.removeLayers(tblLayers.selection.rows.contains)
              tblLayers.model.asInstanceOf[AbstractTableModel].fireTableDataChanged()
              tblLayers.peer.setRowSelectionInterval(0, 0)
              tblLayers.peer.setColumnSelectionInterval(1, 1)
            }
          })
        }
      }

      val center: SplitPane = new SplitPane(Orientation.Vertical, drawingView, controlsView) {
        oneTouchExpandable = true
        continuousLayout = true
        resizeWeight = 1
      }

      layout(center) = Center
    }
    pack()
  }
}
