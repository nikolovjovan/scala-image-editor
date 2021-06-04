package rs.ac.bg.etf.ms1fp.nj203078m.gui

import rs.ac.bg.etf.ms1fp.nj203078m.model.{Abs, Add, Divide, DivideBy, FillWith, Log, MaxWith, MinWith, MultiplyBy, PowerBy, Selection, SubtractBy, SubtractFrom}

import java.awt.Color
import java.io.File
import java.text.DecimalFormat
import javax.swing.ListSelectionModel
import javax.swing.filechooser.FileNameExtensionFilter
import javax.swing.table.AbstractTableModel
import scala.swing.Swing.{CompoundBorder, EmptyBorder, EmptyIcon, EtchedBorder, TitledBorder}
import scala.swing.event.{Key, KeyPressed, MouseClicked, TableRowsSelected, ValueChanged}
import scala.swing.{Action, Alignment, BorderPanel, BoxPanel, Button, ColorChooser, Component, Dimension, FileChooser, FormattedTextField, Frame, Insets, Label, MainFrame, Menu, MenuBar, MenuItem, Orientation, ScrollPane, Separator, SimpleSwingApplication, Slider, SplitPane, Table}

object Main extends SimpleSwingApplication {

  val drawing: Drawing = new Drawing

  var value: Float = 0.0f

  var fillColor: Color = Color.BLACK

  def createHorizontalSplitPane(top: Component, bottom: Component): SplitPane =
    new SplitPane(Orientation.Horizontal, top, bottom) {
      oneTouchExpandable = true
      continuousLayout = true
    }

  def top: Frame = new MainFrame {
    title = "Scala Image Editor"

    menuBar = new MenuBar {
      contents += new Menu("File") {
        contents += new MenuItem(Action("New Project") {
          // TODO: Call new project!
          println("New project!")
        })
        contents += new MenuItem(Action("Load Project") {
          // TODO: Call load project!
          println("Load project!")
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
        contents += new MenuItem(Action("Export") {
          // TODO: Call export project!
          println("Export project!")
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

    contents = new BorderPanel {
      val drawingView: ScrollPane = new ScrollPane(drawing) {
        preferredSize = new Dimension(600, 400)
        minimumSize = preferredSize
      }

      val toolsView: WrapPanel = new WrapPanel(WrapPanel.Alignment.Left)() {
        vGap = 5
        hGap = 5
        yLayoutAlignment = java.awt.Component.CENTER_ALIGNMENT

        preferredSize = new Dimension(912, 48)
        minimumSize = preferredSize

        contents += new BoxPanel(Orientation.Horizontal) {
          contents += new Label("Value: ") {
            preferredSize = new Dimension(80, 20)
            border = EmptyBorder(0, 5, 0, 10)
          }
          contents += new FormattedTextField(new DecimalFormat("#.######")) {
            preferredSize = new Dimension(100, 20)
            maximumSize = preferredSize
            listenTo(keys)
            peer.setValue(value)
            reactions += {
              case e: KeyPressed => if (e.source == this) {
                if (e.key == Key.Enter) {
                  commitEdit()
                  value = peer.getValue.asInstanceOf[Double].toFloat
                }
                if (e.key == Key.Enter || e.key == Key.Escape)
                  text = value.toString
              }
            }
          }
        }

        contents += new WrapPanel(WrapPanel.Alignment.Left)() {
          hGap = 5
          contents += new Button(Action("+") {
            drawing.selectionManager.execute(Add(value))
          })
          contents += new Button(Action("-") {
            drawing.selectionManager.execute(SubtractBy(value))
          })
          contents += new Button(Action("-:") {
            drawing.selectionManager.execute(SubtractFrom(value))
          })
          contents += new Button(Action("*") {
            drawing.selectionManager.execute(MultiplyBy(value))
          })
          contents += new Button(Action("/") {
            drawing.selectionManager.execute(DivideBy(value))
          })
          contents += new Button(Action("/:") {
            drawing.selectionManager.execute(Divide(value))
          })
        }

        contents += new WrapPanel(WrapPanel.Alignment.Left)() {
          hGap = 5
          val btnChooseColor: Button = new Button(Action("████") {
            ColorChooser.showDialog(btnChooseColor, "Fill color", fillColor) match {
              case Some(value) =>
                fillColor = value
                btnChooseColor.foreground = value
                btnChooseColor.repaint()
            }
          }) {
            foreground = fillColor
            margin = new Insets(0, 2, 4, 3)
          }
          contents += btnChooseColor
          contents += new Button(Action("fill") {
            drawing.selectionManager.execute(FillWith(fillColor))
          })
        }

        contents += new WrapPanel(WrapPanel.Alignment.Left)() {
          hGap = 5
          contents += new Button(Action("pow") {
            drawing.selectionManager.execute(PowerBy(value))
          })
          contents += new Button(Action("min") {
            drawing.selectionManager.execute(MinWith(value))
          })
          contents += new Button(Action("max") {
            drawing.selectionManager.execute(MaxWith(value))
          })
          contents += new Button(Action("abs") {
            drawing.selectionManager.execute(Abs())
          })
          contents += new Button(Action("log") {
            drawing.selectionManager.execute(Log())
          })
        }
      }

      val functions: BoxPanel = new BoxPanel(Orientation.Vertical) {
        border = CompoundBorder(TitledBorder(EtchedBorder, "Functions"), EmptyBorder(5, 5, 5, 10))
        xLayoutAlignment = java.awt.Component.CENTER_ALIGNMENT

        listenTo(mouse.clicks)
        reactions += {
          case _: MouseClicked => tblFunctions.peer.clearSelection()
        }

        val tblFunctions: Table = new Table {
          autoResizeMode = Table.AutoResizeMode.AllColumns
          peer.setRowSelectionAllowed(true)
          peer.setSelectionMode(ListSelectionModel.SINGLE_SELECTION)
          model = new AbstractTableModel {
            override def getRowCount: Int = drawing.functionManager.count
            override def getColumnCount: Int = 1
            override def getValueAt(rowIndex: Int, columnIndex: Int): Any = columnIndex match {
              case _ => drawing.functionManager(rowIndex).name
            }

            override def isCellEditable(rowIndex: Int, columnIndex: Int): Boolean = true

            override def setValueAt(aValue: Any, rowIndex: Int, columnIndex: Int): Unit = columnIndex match {
              case 0 =>
                drawing.functionManager(rowIndex).name = aValue.asInstanceOf[String]
              case _ =>
                println("Invalid function " + rowIndex + " change!")
            }
          }
          peer.setTableHeader(null)
          rowHeight = 30
        }

        contents += new ScrollPane(tblFunctions) {
          xLayoutAlignment = java.awt.Component.CENTER_ALIGNMENT
          minimumSize = new Dimension(80, 100)
          preferredSize = new Dimension(80, 100)
          verticalScrollBarPolicy = ScrollPane.BarPolicy.Always
          horizontalScrollBarPolicy = ScrollPane.BarPolicy.Never
          listenTo(mouse.clicks)
          reactions += {
            case _: MouseClicked => tblFunctions.peer.clearSelection()
          }
        }

        val btnAddFunction = new Button(Action("Add") {
          drawing.functionManager.addNewFunction()
          tblFunctions.model.asInstanceOf[AbstractTableModel].fireTableRowsInserted(
            drawing.functionManager.count - 1,
            drawing.functionManager.count - 1
          )
          if (tblFunctions.selection.rows.size == 0) {
            tblFunctions.peer.setRowSelectionInterval(0, 0)
            tblFunctions.peer.setColumnSelectionInterval(0, 0)
            tblFunctions.peer.setEditingRow(0)
            tblFunctions.peer.setEditingColumn(0)
          }
        })

        val btnRemoveFunction = new Button(Action("Remove") {
          if (tblFunctions.selection.rows.size > 0) {
            drawing.functionManager.removeFunctions(tblFunctions.selection.rows.contains)
            tblFunctions.model.asInstanceOf[AbstractTableModel].fireTableDataChanged()
          }
        })

        val btnApplyFunction = new Button(Action("Apply") {
          if (tblFunctions.selection.rows.size > 0) {
            drawing.selectionManager.execute(drawing.functionManager(tblFunctions.peer.getSelectedRow))
          }
        })

        contents += new BoxPanel(Orientation.Horizontal) {
          xLayoutAlignment = java.awt.Component.CENTER_ALIGNMENT
          contents += btnAddFunction
          contents += btnRemoveFunction
          contents += btnApplyFunction
        }
      }

      val selections: BoxPanel = new BoxPanel(Orientation.Vertical) {
        border = CompoundBorder(TitledBorder(EtchedBorder, "Selections"), EmptyBorder(5, 5, 5, 10))
        xLayoutAlignment = java.awt.Component.CENTER_ALIGNMENT

        listenTo(mouse.clicks)
        reactions += {
          case _: MouseClicked => tblSelections.peer.clearSelection()
        }

        val tblSelections: Table = new Table {
          autoResizeMode = Table.AutoResizeMode.AllColumns
          peer.setRowSelectionAllowed(true)
          peer.setSelectionMode(ListSelectionModel.SINGLE_SELECTION)
          model = new AbstractTableModel {
            override def getRowCount: Int = drawing.selectionManager.count
            override def getColumnCount: Int = 1
            override def getValueAt(rowIndex: Int, columnIndex: Int): Any = columnIndex match {
              case _ => drawing.selectionManager(rowIndex).name
            }

            override def isCellEditable(rowIndex: Int, columnIndex: Int): Boolean = true

            override def setValueAt(aValue: Any, rowIndex: Int, columnIndex: Int): Unit = columnIndex match {
              case 0 =>
                drawing.selectionManager(rowIndex).name = aValue.asInstanceOf[String]
              case _ =>
                println("Invalid selection " + rowIndex + " change!")
            }
          }
          peer.setTableHeader(null)
          rowHeight = 30

          listenTo(selection)
          reactions += {
            case e: TableRowsSelected => if (e.source == this)
              if (selection.rows.size > 0)
                drawing.selectionManager.activeSelection =
                  if (selection.rows.size == 1) drawing.selectionManager(peer.getSelectedRow) else Selection.Everything
          }
        }

        contents += new ScrollPane(tblSelections) {
          xLayoutAlignment = java.awt.Component.CENTER_ALIGNMENT
          minimumSize = new Dimension(80, 100)
          preferredSize = new Dimension(80, 100)
          verticalScrollBarPolicy = ScrollPane.BarPolicy.Always
          horizontalScrollBarPolicy = ScrollPane.BarPolicy.Never
          listenTo(mouse.clicks)
          reactions += {
            case _: MouseClicked => tblSelections.peer.clearSelection()
          }
        }

        val btnAddSelection = new Button(Action("Add") {
          drawing.selectionManager.addNewSelection()
          tblSelections.model.asInstanceOf[AbstractTableModel].fireTableRowsInserted(
            drawing.selectionManager.count - 1,
            drawing.selectionManager.count - 1
          )
          if (tblSelections.selection.rows.size == 0) {
            tblSelections.peer.setRowSelectionInterval(0, 0)
            tblSelections.peer.setColumnSelectionInterval(0, 0)
          }
        })

        val btnRemoveSelection = new Button(Action("Remove") {
          if (tblSelections.selection.rows.size > 0) {
            drawing.selectionManager.removeSelections(tblSelections.selection.rows.contains)
            tblSelections.model.asInstanceOf[AbstractTableModel].fireTableDataChanged()
            drawing.render()
          }
        })

        contents += new BoxPanel(Orientation.Horizontal) {
          xLayoutAlignment = java.awt.Component.CENTER_ALIGNMENT
          contents += btnAddSelection
          contents += btnRemoveSelection
        }
      }

      val layers: BoxPanel = new BoxPanel(Orientation.Vertical) {
        border = CompoundBorder(TitledBorder(EtchedBorder, "Layers"), EmptyBorder(5, 5, 5, 10))
        xLayoutAlignment = java.awt.Component.CENTER_ALIGNMENT

        listenTo(mouse.clicks)
        reactions += {
          case _: MouseClicked => tblLayers.peer.clearSelection()
        }

        val lblOpacity: Label = new Label("100 %", EmptyIcon, Alignment.Right) {
          preferredSize = new Dimension(40, 20)
          enabled = false
        }

        val sldOpacity: Slider = new Slider {
          orientation = Orientation.Horizontal
          enabled = false
          value = 100
          min = 0
          max = 100
          reactions += {
            case e: ValueChanged => if (e.source == this) {
              lblOpacity.text = value + " %"
              for (layerId <- tblLayers.peer.getSelectedRows)
                drawing.layerManager(layerId).alpha = value.toFloat / 100.0f
              drawing.render()
            }
          }
        }

        contents += new BoxPanel(Orientation.Horizontal) {
          contents += sldOpacity
          contents += lblOpacity
        }

        val tblLayers: Table = new Table {
          autoResizeMode = Table.AutoResizeMode.AllColumns
          model = new AbstractTableModel {
            override def getRowCount: Int = drawing.layerManager.count
            override def getColumnCount: Int = 2
            override def getValueAt(rowIndex: Int, columnIndex: Int): Any = columnIndex match {
              case 0 => drawing.layerManager(rowIndex).visible
              case _ => drawing.layerManager(rowIndex).name
            }

            override def isCellEditable(rowIndex: Int, columnIndex: Int): Boolean = true

            override def setValueAt(aValue: Any, rowIndex: Int, columnIndex: Int): Unit = columnIndex match {
              case 0 =>
                drawing.layerManager(rowIndex).visible = aValue.asInstanceOf[Boolean]
                drawing.render()
              case 1 =>
                drawing.layerManager(rowIndex).name = aValue.asInstanceOf[String]
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
              lblOpacity.enabled = selection.rows.size > 0
              btnRemoveLayer.enabled = selection.rows.size > 0
              btnLoadImage.enabled = selection.rows.size == 1
              sldOpacity.value = if (selection.rows.size == 1) (drawing.layerManager(peer.getSelectedRow).alpha * 100.0f).toInt else 100
            }
          }
        }

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
              drawing.layerManager(tblLayers.selection.rows.leadIndex).loadImage(chooser.selectedFile.getAbsolutePath)
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

      val sideView: SplitPane = createHorizontalSplitPane(functions, createHorizontalSplitPane(selections, layers))

      val mainView: SplitPane = new SplitPane(Orientation.Vertical, drawingView, sideView) {
        oneTouchExpandable = true
        continuousLayout = true
        resizeWeight = 1
      }

      val center: SplitPane = createHorizontalSplitPane(toolsView, mainView)

      layout(center) = BorderPanel.Position.Center
    }
    pack()
//    minimumSize = new Dimension(size.width + 50, size.height)
    minimumSize = size
  }
}