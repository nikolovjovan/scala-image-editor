package rs.ac.bg.etf.ms1fp.nj203078m.gui

import rs.ac.bg.etf.ms1fp.nj203078m.model._
import rs.ac.bg.etf.ms1fp.nj203078m.model.operation.{Abs, Add, Divide, DivideBy, FillWith, Log, MaxWith, Median, MinWith, MultiplyBy, Operation, PixelOperation, PowerBy, SubtractBy, SubtractFrom, WeightedAverage}
import rs.ac.bg.etf.ms1fp.nj203078m.model.traits.Sequence

import java.awt.Color
import java.io.File
import java.util.Scanner
import javax.swing.event.ChangeEvent
import javax.swing.{JSpinner, ListSelectionModel, SpinnerNumberModel}
import javax.swing.filechooser.FileNameExtensionFilter
import javax.swing.table.AbstractTableModel
import scala.collection.mutable.ArrayBuffer
import scala.swing.Swing.{CompoundBorder, EmptyBorder, EmptyIcon, EtchedBorder, TitledBorder}
import scala.swing.TabbedPane.Page
import scala.swing.event.{Key, KeyPressed, KeyTyped, MouseClicked, TableRowsSelected, ValueChanged}
import scala.swing.{Action, Alignment, BorderPanel, BoxPanel, Button, ColorChooser, Component, Dialog, Dimension, FileChooser, Frame, Insets, Label, MainFrame, Menu, MenuBar, MenuItem, Orientation, ScrollPane, Separator, SimpleSwingApplication, Slider, SplitPane, TabbedPane, Table, TextArea, TextField}

object Main extends SimpleSwingApplication {

  import DialogType.DialogType

  val drawing: Drawing = new Drawing

  var value: Float = 0.0f

  var fillColor: Color = Color.BLACK

  var N: Int = 1

  def D: Int = 2 * N + 1

  var editingMode: DialogType = DialogType.Function
  var editingDialog: Option[SequenceDialog] = None

  var layerSelectionContains: Int => Boolean = _ => false

  def createHorizontalSplitPane(top: Component, bottom: Component): SplitPane =
    new SplitPane(Orientation.Horizontal, top, bottom) {
      oneTouchExpandable = true
      continuousLayout = true
    }

  def createTableScrollPane(table: Table): ScrollPane = new ScrollPane(table) {
    xLayoutAlignment = java.awt.Component.CENTER_ALIGNMENT
    minimumSize = new Dimension(80, 100)
    preferredSize = new Dimension(80, 100)
    verticalScrollBarPolicy = ScrollPane.BarPolicy.Always
    horizontalScrollBarPolicy = ScrollPane.BarPolicy.Never
    listenTo(mouse.clicks)
    reactions += {
      case _: MouseClicked => table.peer.clearSelection()
    }
  }

  def createHorizontalBoxPanel(components: Seq[Component]): BoxPanel = new BoxPanel(Orientation.Horizontal) {
    xLayoutAlignment = java.awt.Component.CENTER_ALIGNMENT
    contents ++= components
  }

  def executeOrAddOperation(operation: Operation): Unit = {
    if (editingDialog.isDefined) {
      editingMode match {
        case DialogType.Function =>
          operation match {
            case pixelOp: PixelOperation => drawing.functionManager.last.operations.addOne(pixelOp)
            case _ => Dialog.showMessage(top, "Functions can be consisted of pixel operations only!", "Warning!", Dialog.Message.Warning)
          }
        case DialogType.Operation => drawing.operationSeqManager.last.operations.addOne(operation)
      }
      editingDialog.get.updateTable()
    } else drawing.selectionManager.execute(layerSelectionContains, operation)
  }

  val toggleableButtons: ArrayBuffer[Button] = new ArrayBuffer[Button]()

  def toggleButtons(): Unit = {
    for (btn <- toggleableButtons)
      btn.enabled = !btn.enabled
  }

  def stopEditing(updateTable: Table): Unit = {
    editingDialog = None
    toggleButtons()
    updateTable.model.asInstanceOf[AbstractTableModel].fireTableDataChanged()
  }

  override def top: Frame = frame

  val frame: Frame = new MainFrame {
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

        preferredSize = new Dimension(990, 48)
        minimumSize = preferredSize

        contents += new BoxPanel(Orientation.Horizontal) {
          contents += new Label("Value: ") {
            preferredSize = new Dimension(80, 20)
            border = EmptyBorder(0, 5, 0, 10)
          }
          val lblValue: Label = new Label(value.toString) {
            preferredSize = new Dimension(80, 20)
            border = EmptyBorder(0, 10, 0, 0)
          }
          contents += new TextField {
            preferredSize = new Dimension(100, 20)
            maximumSize = preferredSize
            text = value.toString
            verifier = _ => false
            listenTo(keys)
            reactions += {
              case e: KeyTyped => if (e.source == this) {
                if ((!e.char.isDigit && e.char != '.') || (e.char == '.' && text.contains('.')))
                  e.consume()
                try {
                  if (peer.getSelectionStart == 0 && peer.getSelectionEnd == peer.getText.length)
                    value = e.char.toString.toFloat
                  else {
                    value = (text.substring(0, caret.position) + e.char + text.substring(caret.position)).toFloat
                    if (text.length > value.toString.length)
                      text = value.toString
                  }
                  lblValue.text = value.toString
                } catch {
                  case _: Exception =>
                }
              }
              case e: KeyPressed => if (e.source == this) {
                if (e.key == Key.Enter) {
                  value = text.toFloat
                  lblValue.text = value.toString
                }
                if (e.key == Key.Enter || e.key == Key.Escape)
                  text = value.toString
              }
            }
          }
          contents += lblValue
        }

        contents += new WrapPanel(WrapPanel.Alignment.Left)() {
          hGap = 5
          contents += new Button(Action("+")(executeOrAddOperation(Add(value))))
          contents += new Button(Action("-")(executeOrAddOperation(SubtractBy(value))))
          contents += new Button(Action("-:")(executeOrAddOperation(SubtractFrom(value))))
          contents += new Button(Action("*")(executeOrAddOperation(MultiplyBy(value))))
          contents += new Button(Action("/")(executeOrAddOperation(DivideBy(value))))
          contents += new Button(Action("/:")(executeOrAddOperation(Divide(value))))
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
          contents += new Button(Action("fill")(executeOrAddOperation(FillWith(fillColor))))
        }

        contents += new WrapPanel(WrapPanel.Alignment.Left)() {
          hGap = 5
          contents += new Button(Action("pow")(executeOrAddOperation(PowerBy(value))))
          contents += new Button(Action("min")(executeOrAddOperation(MinWith(value))))
          contents += new Button(Action("max")(executeOrAddOperation(MaxWith(value))))
          contents += new Button(Action("abs")(executeOrAddOperation(Abs())))
          contents += new Button(Action("log")(executeOrAddOperation(Log())))
        }
      }

      val functions: BoxPanel = new BoxPanel(Orientation.Vertical) {
        xLayoutAlignment = java.awt.Component.CENTER_ALIGNMENT

        val table: Table = new Table {
          autoResizeMode = Table.AutoResizeMode.LastColumn
          peer.setRowSelectionAllowed(true)
          peer.setSelectionMode(ListSelectionModel.SINGLE_SELECTION)
          model = new AbstractTableModel {
            override def getRowCount: Int = drawing.functionManager.count
            override def getColumnCount: Int = 1
            override def getValueAt(rowIndex: Int, columnIndex: Int): Any = drawing.functionManager(rowIndex).name
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
          listenTo(selection, mouse.clicks)
          reactions += {
            case e: TableRowsSelected => if (e.source == this) {
              btnRemove.enabled = selection.rows.size > 0 && peer.getSelectedRow != 0 && peer.getSelectedRow != 1
              btnApply.enabled = selection.rows.size > 0
            }
            case e: MouseClicked => if (e.source == this && e.modifiers == Key.Modifier.Control && selection.rows.size > 0)
              new SequenceDialog(
                top,
                DialogType.Function,
                drawing.functionManager(peer.getSelectedRow).asInstanceOf[Sequence[Any]]
              ).visible = true
          }
        }

        listenTo(mouse.clicks)
        reactions += {
          case _: MouseClicked => table.peer.clearSelection()
        }

        contents += createTableScrollPane(table)

        val btnAdd = new Button(Action("Add") {
          drawing.functionManager.addFunction()
          table.model.asInstanceOf[AbstractTableModel].fireTableRowsInserted(
            drawing.functionManager.count - 1,
            drawing.functionManager.count - 1
          )
          table.peer.setRowSelectionInterval(drawing.functionManager.count - 1, drawing.functionManager.count - 1)
          table.peer.setColumnSelectionInterval(0, 0)
          toggleButtons()
          editingMode = DialogType.Function
          editingDialog = Some(new SequenceDialog(
            top,
            DialogType.Function,
            drawing.functionManager.last.asInstanceOf[Sequence[Any]],
            true,
            _ => stopEditing(table)))
          editingDialog.get.visible = true
        })
        toggleableButtons.addOne(btnAdd)

        val btnRemove = new Button(Action("Remove") {
          if (table.selection.rows.size > 0) {
            drawing.functionManager.removeFunctions(table.selection.rows.contains)
            table.model.asInstanceOf[AbstractTableModel].fireTableDataChanged()
          }
        })
        toggleableButtons.addOne(btnRemove)

        val btnApply = new Button(Action("Apply") {
          if (table.selection.rows.size > 0)
            executeOrAddOperation(drawing.functionManager(table.peer.getSelectedRow))
        })

        contents += createHorizontalBoxPanel(Seq(btnAdd, btnRemove, btnApply))
      }

      val filters: BoxPanel = new BoxPanel(Orientation.Vertical) {
        xLayoutAlignment = java.awt.Component.CENTER_ALIGNMENT

        contents += new BoxPanel(Orientation.Horizontal) {
          border = EmptyBorder(5)
          contents += new Label("N = ") {
            preferredSize = new Dimension(30, 20)
          }
          val spinner: Component = Component.wrap(new JSpinner(new SpinnerNumberModel(N, 1, 50, 1)))
          spinner.peer.asInstanceOf[JSpinner].addChangeListener(
            (_: ChangeEvent) => spinner.publish(new ValueChanged(spinner)))
          spinner.reactions += {
            case e: ValueChanged => if (e.source == spinner) {
              N = spinner.peer.asInstanceOf[JSpinner].getValue.asInstanceOf[Int]
              lblD.text = "D = " + D
            }
          }
          contents += spinner
          val lblD: Label = new Label("D = " + D) {
            preferredSize = new Dimension(160, 20)
            border = EmptyBorder(0, 10, 0, 0)
          }
          contents += lblD
        }

        val txtWeightMatrix: TextArea = new TextArea(
          "Insert weight matrix here!\n" +
          "Separate columns with single whitespace,\n" +
          "separate rows with newline!", D, D)

        contents += new ScrollPane(txtWeightMatrix) {
          xLayoutAlignment = java.awt.Component.CENTER_ALIGNMENT
          minimumSize = new Dimension(80, 80)
          preferredSize = new Dimension(80, 80)
          verticalScrollBarPolicy = ScrollPane.BarPolicy.AsNeeded
          horizontalScrollBarPolicy = ScrollPane.BarPolicy.AsNeeded
        }

        contents += new BoxPanel(Orientation.Horizontal) {
          xLayoutAlignment = java.awt.Component.CENTER_ALIGNMENT
          contents += new Button(Action("Median")(executeOrAddOperation(Median(N))))
          contents += new Button(Action("Weighted average") {
            if (txtWeightMatrix.text.matches(".*[a-zA-Z]+.*"))
              Dialog.showMessage(this, "Invalid weight matrix!", "Error!", Dialog.Message.Error)
            val weights: Image.PixelMatrix = new Array[Image.PixelVector](D)
            for (x <- 0 until D)
              weights(x) = new Image.PixelVector(D)
            val scanner: Scanner = new Scanner(txtWeightMatrix.text)
            try {
              for (y <- 0 until D)
                for (x <- 0 until D)
                  if (!scanner.hasNextFloat) {
                    Dialog.showMessage(this, "Invalid weight matrix!", "Error!", Dialog.Message.Error)
                    throw new Exception
                  } else weights(x)(y) = scanner.nextFloat()
              drawing.selectionManager.execute(layerSelectionContains, WeightedAverage(N, weights))
            } catch {
              case _:Exception =>
            }
          })
        }
      }

      val operations: BoxPanel = new BoxPanel(Orientation.Vertical) {
        xLayoutAlignment = java.awt.Component.CENTER_ALIGNMENT

        val table: Table = new Table {
          autoResizeMode = Table.AutoResizeMode.LastColumn
          peer.setRowSelectionAllowed(true)
          peer.setSelectionMode(ListSelectionModel.SINGLE_SELECTION)
          model = new AbstractTableModel {
            override def getRowCount: Int = drawing.operationSeqManager.count
            override def getColumnCount: Int = 1
            override def getValueAt(rowIndex: Int, columnIndex: Int): Any = drawing.operationSeqManager(rowIndex).name
            override def isCellEditable(rowIndex: Int, columnIndex: Int): Boolean = true
            override def setValueAt(aValue: Any, rowIndex: Int, columnIndex: Int): Unit = columnIndex match {
              case 0 =>
                drawing.operationSeqManager(rowIndex).name = aValue.asInstanceOf[String]
              case _ =>
                println("Invalid operation sequence " + rowIndex + " change!")
            }
          }
          peer.setTableHeader(null)
          rowHeight = 30
          listenTo(selection, mouse.clicks)
          reactions += {
            case e: TableRowsSelected => if (e.source == this) {
              btnRemove.enabled = selection.rows.size > 0
              btnApply.enabled = selection.rows.size > 0
            }
            case e: MouseClicked => if (e.source == this && e.modifiers == Key.Modifier.Control && selection.rows.size > 0)
              new SequenceDialog(
                top,
                DialogType.Operation,
                drawing.operationSeqManager(peer.getSelectedRow).asInstanceOf[Sequence[Any]]
              ).visible = true
          }
        }

        listenTo(mouse.clicks)
        reactions += {
          case _: MouseClicked => table.peer.clearSelection()
        }

        contents += createTableScrollPane(table)

        val btnAdd = new Button(Action("Add") {
          drawing.operationSeqManager.addOperationSeq()
          table.model.asInstanceOf[AbstractTableModel].fireTableRowsInserted(
            drawing.operationSeqManager.count - 1,
            drawing.operationSeqManager.count - 1
          )
          table.peer.setRowSelectionInterval(drawing.operationSeqManager.count - 1, drawing.operationSeqManager.count - 1)
          table.peer.setColumnSelectionInterval(0, 0)
          toggleButtons()
          editingMode = DialogType.Operation
          editingDialog = Some(new SequenceDialog(
            top,
            DialogType.Operation,
            drawing.operationSeqManager.last.asInstanceOf[Sequence[Any]],
            true,
            _ => stopEditing(table)))
          editingDialog.get.visible = true
        })
        toggleableButtons.addOne(btnAdd)

        val btnRemove = new Button(Action("Remove") {
          if (table.selection.rows.size > 0) {
            drawing.operationSeqManager.removeOperationSeqs(table.selection.rows.contains)
            table.model.asInstanceOf[AbstractTableModel].fireTableDataChanged()
          }
        })
        toggleableButtons.addOne(btnRemove)

        val btnApply = new Button(Action("Apply") {
          if (table.selection.rows.size > 0)
            executeOrAddOperation(drawing.operationSeqManager(table.peer.getSelectedRow))
        })

        contents += createHorizontalBoxPanel(Seq(btnAdd, btnRemove, btnApply))
      }

      val tpFFO: TabbedPane = new TabbedPane() {
        pages.addOne(new Page("Functions", functions))
        pages.addOne(new Page("Filters", filters))
        pages.addOne(new Page("Operations", operations))
      }

      val selections: BoxPanel = new BoxPanel(Orientation.Vertical) {
        border = CompoundBorder(TitledBorder(EtchedBorder, "Selections"), EmptyBorder(5, 5, 5, 10))
        xLayoutAlignment = java.awt.Component.CENTER_ALIGNMENT

        val table: Table = new Table {
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
          listenTo(selection, mouse.clicks)
          reactions += {
            case e: TableRowsSelected => if (e.source == this) {
              btnRemove.enabled = selection.rows.size > 0
              if (selection.rows.size > 0)
                drawing.selectionManager.activeSelection =
                  if (selection.rows.size == 1) drawing.selectionManager(peer.getSelectedRow) else Selection.Everything
            }
            case e: MouseClicked => if (e.source == this && e.modifiers == Key.Modifier.Control && selection.rows.size > 0)
              new SequenceDialog(
                top,
                DialogType.Selection,
                drawing.selectionManager(peer.getSelectedRow).asInstanceOf[Sequence[Any]]
              ).visible = true
          }
        }

        listenTo(mouse.clicks)
        reactions += {
          case _: MouseClicked => table.peer.clearSelection()
        }

        contents += createTableScrollPane(table)

        val btnAdd = new Button(Action("Add") {
          drawing.selectionManager.addSelection()
          table.model.asInstanceOf[AbstractTableModel].fireTableRowsInserted(
            drawing.selectionManager.count - 1,
            drawing.selectionManager.count - 1
          )
          table.peer.setRowSelectionInterval(drawing.selectionManager.count - 1, drawing.selectionManager.count - 1)
          table.peer.setColumnSelectionInterval(0, 0)
          toggleButtons()
          editingMode = DialogType.Selection
          editingDialog = Some(new SequenceDialog(
            top,
            DialogType.Selection,
            drawing.selectionManager.last.asInstanceOf[Sequence[Any]],
            true,
            _ => stopEditing(table)))
          editingDialog.get.visible = true
        })
        toggleableButtons.addOne(btnAdd)

        val btnRemove = new Button(Action("Remove") {
          if (table.selection.rows.size > 0) {
            drawing.selectionManager.removeSelections(table.selection.rows.contains)
            table.model.asInstanceOf[AbstractTableModel].fireTableDataChanged()
            drawing.render()
          }
        })
        toggleableButtons.addOne(btnRemove)

        contents += createHorizontalBoxPanel(Seq(btnAdd, btnRemove))
      }

      val layers: BoxPanel = new BoxPanel(Orientation.Vertical) {
        border = CompoundBorder(TitledBorder(EtchedBorder, "Layers"), EmptyBorder(5, 5, 5, 10))
        xLayoutAlignment = java.awt.Component.CENTER_ALIGNMENT

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
              for (layerId <- table.peer.getSelectedRows)
                drawing.layerManager(layerId).alpha = value.toFloat / 100.0f
              drawing.render()
            }
          }
        }

        contents += createHorizontalBoxPanel(Seq(sldOpacity, lblOpacity))

        val table: Table = new Table {
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
              btnRemove.enabled = selection.rows.size > 0
              btnLoad.enabled = selection.rows.size == 1
              sldOpacity.value = if (selection.rows.size == 1) (drawing.layerManager(peer.getSelectedRow).alpha * 100.0f).toInt else 100
            }
          }
          layerSelectionContains = selection.rows.contains
        }

        listenTo(mouse.clicks)
        reactions += {
          case _: MouseClicked => table.peer.clearSelection()
        }

        contents += createTableScrollPane(table)

        val btnAdd = new Button(Action("Add") {
          if (table.selection.rows.size == 1)
            drawing.layerManager.addLayer(table.peer.getSelectedRow)
          else
            drawing.layerManager.addLayer()
          table.model.asInstanceOf[AbstractTableModel].fireTableRowsInserted(0, 0)
          if (table.selection.rows.size == 1) {
            table.peer.setRowSelectionInterval(table.peer.getSelectedRow - 1, table.peer.getSelectedRow - 1)
            table.peer.setColumnSelectionInterval(1, 1)
          } else {
            table.peer.setRowSelectionInterval(0, 0)
            table.peer.setColumnSelectionInterval(1, 1)
          }
        })
        toggleableButtons.addOne(btnAdd)

        val btnRemove = new Button(Action("Remove") {
          if (table.selection.rows.size > 0) {
            drawing.layerManager.removeLayers(table.selection.rows.contains)
            table.model.asInstanceOf[AbstractTableModel].fireTableDataChanged()
            table.peer.setRowSelectionInterval(0, 0)
            table.peer.setColumnSelectionInterval(1, 1)
            drawing.render()
          }
        })
        toggleableButtons.addOne(btnRemove)

        val btnLoad = new Button(Action("Load") {
          if (table.selection.rows.size == 1) {
            val chooser = new FileChooser(new File(System.getProperty("user.dir")))
            chooser.peer.removeChoosableFileFilter(chooser.peer.getAcceptAllFileFilter)
            chooser.fileFilter = new FileNameExtensionFilter("Image files (*.png, *.jpg, *.jpeg)", "png", "jpg", "jpeg")
            if (chooser.showOpenDialog(this) == FileChooser.Result.Approve) {
              drawing.layerManager(table.selection.rows.leadIndex).loadImage(chooser.selectedFile.getAbsolutePath)
              drawing.render()
            }
          }
        })
        toggleableButtons.addOne(btnLoad)

        contents += createHorizontalBoxPanel(Seq(btnAdd, btnRemove, btnLoad))
      }

      val sideView: SplitPane = createHorizontalSplitPane(tpFFO, createHorizontalSplitPane(selections, layers))

      val mainView: SplitPane = new SplitPane(Orientation.Vertical, drawingView, sideView) {
        oneTouchExpandable = true
        continuousLayout = true
        resizeWeight = 1
      }

      val center: SplitPane = createHorizontalSplitPane(toolsView, mainView)

      layout(center) = BorderPanel.Position.Center
    }

    pack()
    minimumSize = size
  }
}