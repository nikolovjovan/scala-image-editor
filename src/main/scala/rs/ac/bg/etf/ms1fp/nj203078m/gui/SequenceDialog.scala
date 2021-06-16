package rs.ac.bg.etf.ms1fp.nj203078m.gui

import rs.ac.bg.etf.ms1fp.nj203078m.gui.Main.{createHorizontalBoxPanel, createTableScrollPane}

import javax.swing.table.{AbstractTableModel, TableModel}
import scala.swing.Swing.EmptyBorder
import scala.swing.event.{Key, KeyPressed, MouseClicked, TableRowsSelected, WindowClosing}
import scala.swing.{Action, BoxPanel, Button, Dialog, Dimension, Orientation, Table, TextField, Window}

object DialogType extends Enumeration {
  type DialogType = Value
  val Function, Operation, Selection = Value
}

class SequenceDialog (owner: Window) extends Dialog (owner) {

  import DialogType._

  var name: String = ""

  var stopEditing: Unit => Unit = _ => {}

  var table: Option[Table] = None

  def updateTitle(dialogType: DialogType, name: String): Unit = {
    title = dialogType match {
      case Function => "Function: \"" + name + "\""
      case Operation => "Operation: \"" + name + "\""
      case Selection => "Selection: \"" + name + "\""
    }
  }

  def updateTable(): Unit = {
    if (table.isDefined)
      table.get.model.asInstanceOf[AbstractTableModel].fireTableDataChanged()
  }

  def this(owner: Window,
           dialogType: DialogType.DialogType,
           name: String,
           tableModel: TableModel,
           editing: Boolean = false,
           changeName: String => Unit = null,
           removeElements: (Int => Boolean) => Unit = null,
           stopEditing: Unit => Unit = null) {
    this(owner)

    this.name = name
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
            this.name = txtName.get.text
            changeName(this.name)
            updateTitle(dialogType, this.name)
          } else if (e.key == Key.Escape)
            txtName.get.text = this.name
        }
      }
      this.stopEditing = stopEditing
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
        model = tableModel
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
            removeElements(table.get.selection.rows.contains)
            table.get.model.asInstanceOf[AbstractTableModel].fireTableDataChanged()
          }
        }))

        val btnSave = new Button(Action("Save") {
          changeName(txtName.get.text)
          close()
        })

        contents += createHorizontalBoxPanel(Seq(btnRemove.get, btnSave))
      } else {
        contents += new Button(Action("OK") {
          close()
        })
      }
    }
  }

  reactions += {
    case _: WindowClosing => stopEditing()
  }

  minimumSize = new Dimension(300, 300)
}