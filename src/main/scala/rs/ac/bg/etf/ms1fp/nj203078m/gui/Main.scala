package rs.ac.bg.etf.ms1fp.nj203078m.gui

import java.io.File
import javax.swing.filechooser.FileNameExtensionFilter
import javax.swing.table.AbstractTableModel
import scala.swing.Swing.{CompoundBorder, EmptyBorder, EtchedBorder, TitledBorder}
import scala.swing.event.{MouseClicked, TableRowsSelected, ValueChanged}
import scala.swing.{Action, BorderPanel, BoxPanel, Button, ButtonGroup, CheckBox, Dimension, FileChooser, Frame, MainFrame, Menu, MenuBar, MenuItem, Orientation, RadioButton, ScrollPane, Separator, SimpleSwingApplication, Slider, SplitPane, Swing, Table}

object Main extends SimpleSwingApplication {

  val drawing: Drawing = new Drawing

  def top: Frame = new MainFrame {
    title = "Scala Image Editor"

    minimumSize = new Dimension(600, 500)

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
          xLayoutAlignment = java.awt.Component.CENTER_ALIGNMENT
          val a = new RadioButton("Green Vegetables")
          val b = new RadioButton("Red Meat")
          val c = new RadioButton("White Tofu")
          val mutex = new ButtonGroup(a, b, c)
          contents ++= mutex.buttons
        }

        contents += new BoxPanel(Orientation.Vertical) {
          border = CompoundBorder(TitledBorder(EtchedBorder, "Check Boxes"), EmptyBorder(5, 5, 5, 10))
          xLayoutAlignment = java.awt.Component.CENTER_ALIGNMENT
          val paintLabels = new CheckBox("Paint Labels")
          val paintTicks = new CheckBox("Paint Ticks")
          val snapTicks = new CheckBox("Snap To Ticks")
          val live = new CheckBox("Live")
          contents ++= Seq(paintLabels, paintTicks, snapTicks, live)
        }

        val tblLayers: Table = new Table {
          autoResizeMode = Table.AutoResizeMode.AllColumns
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
                drawing.render()
              case 1 =>
                drawing.layerManager.layers(rowIndex).name = aValue.asInstanceOf[String]
              case _ =>
                println("Invalid layer " + rowIndex + " change!")
            }
          }
          peer.setTableHeader(null)
          peer.getColumnModel.getColumn(0).setMaxWidth(30)
          rowHeight = 30

          listenTo(selection)
          reactions += {
            case e: TableRowsSelected => if (e.source == this) {
              sldOpacity.enabled = selection.rows.size > 0
              btnRemoveLayer.enabled = selection.rows.size > 0
              btnLoadImage.enabled = selection.rows.size == 1
              sldOpacity.value = if (selection.rows.size == 1) (drawing.layerManager.layers(peer.getSelectedRow).alpha * 100.0f).toInt else 100
            }
          }
        }

        listenTo(mouse.clicks)
        reactions += {
          case _: MouseClicked => tblLayers.peer.clearSelection()
        }

        val sldOpacity: Slider = new Slider {
          orientation = Orientation.Horizontal
          enabled = false
          value = 100
          min = 0
          max = 100
          reactions += {
            case e: ValueChanged => if (e.source == this) {
              for (layerId <- tblLayers.peer.getSelectedRows)
                drawing.layerManager.layers(layerId).alpha = value.toFloat / 100.0f
              drawing.render()
            }
          }
        }

        contents += sldOpacity

        contents += new ScrollPane(tblLayers) {
          xLayoutAlignment = java.awt.Component.CENTER_ALIGNMENT
          minimumSize = new Dimension(80, 100)
          preferredSize = new Dimension(80, 100)
          verticalScrollBarPolicy = ScrollPane.BarPolicy.Always
          horizontalScrollBarPolicy = ScrollPane.BarPolicy.Never
          listenTo(mouse.clicks)
          reactions += {
            case _: MouseClicked => tblLayers.peer.clearSelection()
          }
        }

        val btnAddLayer = new Button(Action("Add") {
          if (tblLayers.selection.rows.size == 1)
            drawing.layerManager.addNewLayer(tblLayers.peer.getSelectedRow)
          else
            drawing.layerManager.addNewLayer()
          tblLayers.model.asInstanceOf[AbstractTableModel].fireTableRowsInserted(
            drawing.layerManager.count - 1,
            drawing.layerManager.count - 1)
          if (tblLayers.selection.rows.size == 0) {
            tblLayers.peer.setRowSelectionInterval(0, 0)
            tblLayers.peer.setColumnSelectionInterval(1, 1)
          }
        })

        val btnRemoveLayer = new Button(Action("Remove") {
          if (tblLayers.selection.rows.size > 0) {
            drawing.layerManager.removeLayers(tblLayers.selection.rows.contains)
            tblLayers.model.asInstanceOf[AbstractTableModel].fireTableDataChanged()
            tblLayers.peer.setRowSelectionInterval(0, 0)
            tblLayers.peer.setColumnSelectionInterval(1, 1)
            drawing.render()
          }
        })

        val btnLoadImage = new Button(Action("Load") {
          if (tblLayers.selection.rows.size == 1) {
            val chooser = new FileChooser(new File(System.getProperty("user.dir")))
            chooser.peer.removeChoosableFileFilter(chooser.peer.getAcceptAllFileFilter)
            chooser.fileFilter = new FileNameExtensionFilter("Image files (*.png, *.jpg, *.jpeg)", "png", "jpg", "jpeg")
            if (chooser.showOpenDialog(this) == FileChooser.Result.Approve) {
              drawing.layerManager.layers(tblLayers.selection.rows.leadIndex).loadImage(chooser.selectedFile.getAbsolutePath)
              drawing.render()
            }
          }
        })

        contents += new BoxPanel(Orientation.Horizontal) {
          xLayoutAlignment = java.awt.Component.CENTER_ALIGNMENT
          contents += btnAddLayer
          contents += btnRemoveLayer
          contents += btnLoadImage
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
