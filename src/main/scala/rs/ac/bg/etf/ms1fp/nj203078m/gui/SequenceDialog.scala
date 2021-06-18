package rs.ac.bg.etf.ms1fp.nj203078m.gui

import rs.ac.bg.etf.ms1fp.nj203078m.gui.Main.{createHorizontalBoxPanel, createTableScrollPane}
import rs.ac.bg.etf.ms1fp.nj203078m.model.traits.Sequence

import javax.swing.table.AbstractTableModel
import scala.swing.Swing.EmptyBorder
import scala.swing.event.{Key, KeyPressed, MouseClicked, TableRowsSelected, WindowClosing}
import scala.swing.{Action, BoxPanel, Button, Dialog, Dimension, Orientation, Table, TextField, Window}

object DialogType extends Enumeration {
  type DialogType = Value
  val Function, Operation, Selection = Value
}

class SequenceDialog private (owner: Window) extends Dialog (owner) {
  var name: String = ""

  var stopEditing: Unit => Unit = _ => {}

  var table: Option[Table] = None

  def updateTitle(dialogType: DialogType.DialogType, name: String): Unit = {
    title = dialogType match {
      case DialogType.Function => "Function: \"" + name + "\""
      case DialogType.Operation => "Operation: \"" + name + "\""
      case DialogType.Selection => "Selection: \"" + name + "\""
    }
  }

  def updateTable(): Unit = {
    if (table.isDefined)
      table.get.model.asInstanceOf[AbstractTableModel].fireTableDataChanged()
  }

  def this(owner: Window,
           dialogType: DialogType.DialogType,
           sequence: Sequence[Any],
           editing: Boolean = false,
           stopEditing: Unit => Unit = null) {
    this(owner)

    name = sequence.name
    updateTitle(dialogType, name)

    var txtName: Option[TextField] = None

    if (editing) {
      txtName = Some(new TextField {
        maximumSize = new Dimension(1000, 18)
      })
      txtName.get.text = name
      listenTo(txtName.get.keys)
      reactions += {
        case e: KeyPressed => if (e.source == txtName.get) {
          if (e.key == Key.Enter) {
            name = txtName.get.text
            sequence.name = name
            updateTitle(dialogType, name)
          } else if (e.key == Key.Escape)
            txtName.get.text = name
        }
      }
      this.stopEditing = stopEditing
      reactions += {
        case _: WindowClosing => if (stopEditing != null) this.stopEditing()
      }
    }

    contents = new BoxPanel(Orientation.Vertical) {
      xLayoutAlignment = java.awt.Component.CENTER_ALIGNMENT
      border = EmptyBorder(8)

      if (editing)
        contents += txtName.get

      var btnRemove: Option[Button] = None

      table = Some(new Table {
        autoResizeMode = Table.AutoResizeMode.LastColumn
        peer.setRowSelectionAllowed(true)
        model = new AbstractTableModel {
          override def getRowCount: Int = sequence.count
          override def getColumnCount: Int = 1
          override def getValueAt(rowIndex: Int, columnIndex: Int): Any = sequence(rowIndex).toString
          override def isCellEditable(rowIndex: Int, columnIndex: Int): Boolean = false
          override def setValueAt(aValue: Any, rowIndex: Int, columnIndex: Int): Unit = {}
        }
        peer.setTableHeader(null)
        rowHeight = 30
        if (editing) {
          listenTo(selection)
          reactions += {
            case e: TableRowsSelected => if (e.source == this) {
              btnRemove.get.enabled = selection.rows.size > 0 && peer.getSelectedRow != 0 && peer.getSelectedRow != 1
            }
          }
        }
      })

      listenTo(mouse.clicks)
      reactions += {
        case _: MouseClicked => table.get.peer.clearSelection()
      }

      contents += createTableScrollPane(table.get)

      if (editing) {
        btnRemove = Some(new Button(Action("Remove") {
          if (table.get.selection.rows.size > 0) {
            sequence.removeComponents(table.get.selection.rows.contains)
            table.get.model.asInstanceOf[AbstractTableModel].fireTableDataChanged()
          }
        }))

        val btnSave = new Button(Action("Save") {
          sequence.name = txtName.get.text
          close()
          stopEditing()
        })

        contents += createHorizontalBoxPanel(Seq(btnRemove.get, btnSave))
      } else {
        contents += new Button(Action("OK") {
          close()
        })
      }
    }

    if (owner != null) {
      pack()
      setLocationRelativeTo(owner)
    }

    peer.setAlwaysOnTop(true)

    minimumSize = new Dimension(300, 300)
  }
}