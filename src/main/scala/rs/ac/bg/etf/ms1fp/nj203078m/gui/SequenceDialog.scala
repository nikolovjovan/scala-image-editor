package rs.ac.bg.etf.ms1fp.nj203078m.gui

import rs.ac.bg.etf.ms1fp.nj203078m.gui.Main.{createHorizontalBoxPanel, createTableScrollPane}
import rs.ac.bg.etf.ms1fp.nj203078m.model.Selection
import rs.ac.bg.etf.ms1fp.nj203078m.model.operation.{Function, OperationSeq}
import rs.ac.bg.etf.ms1fp.nj203078m.model.traits.Sequence

import javax.swing.table.AbstractTableModel
import scala.swing.Swing.EmptyBorder
import scala.swing.event.{Key, KeyPressed, MouseClicked, TableRowsSelected, WindowClosing}
import scala.swing.{Action, BoxPanel, Button, Dialog, Dimension, Orientation, Table, TextField, Window}

class SequenceDialog (owner: Window, sequence: Sequence[Any], editing: Boolean = false, stopEditingFn: Unit => Unit = null) extends Dialog (owner) {
  var name: String = ""

  var stopEditing: Unit => Unit = _ => {}

  var table: Option[Table] = None

  def updateTitle(sequence: Sequence[Any], name: String): Unit = title =
    // Pattern matching does not work because of covariance issues. I tried to make it work by adding +T in
    // Sequence[T] which failed and I eventually gave up.
    //
    if (sequence.isInstanceOf[Selection])
      "Selection: \"" + name + "\""
    else if (sequence.isInstanceOf[Function])
      "Function: \"" + name + "\""
    else if (sequence.isInstanceOf[OperationSeq])
      "Operation sequence: \"" + name + "\""
    else
      "Unknown sequence: \"" + name + "\""

  def updateTable(): Unit = {
    if (table.isDefined)
      table.get.model.asInstanceOf[AbstractTableModel].fireTableDataChanged()
  }

  name = sequence.name
  updateTitle(sequence, name)

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
          updateTitle(sequence, name)
        } else if (e.key == Key.Escape)
          txtName.get.text = name
      }
    }
    stopEditing = stopEditingFn
    reactions += {
      case _: WindowClosing => if (stopEditing != null) stopEditing()
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
            btnRemove.get.enabled = selection.rows.size > 0
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
      }) {
        enabled = false
      })

      val btnSave = new Button(Action("Save") {
        sequence.name = txtName.get.text
        close()
        stopEditing()
        if (sequence.isInstanceOf[Selection])
          Main.drawing.stopEditingSelection()
      })

      contents += createHorizontalBoxPanel(Seq(btnRemove.get, btnSave))
    } else {
      contents += new Button(Action("OK") {
        close()
        if (sequence.isInstanceOf[Selection])
          Main.drawing.stopHighlightingSelection()
      })
    }
  }

  if (owner != null) {
    pack()
    setLocationRelativeTo(owner)
  }

  peer.setAlwaysOnTop(true)

  minimumSize = new Dimension(300, 300)

  reactions += {
    case _: WindowClosing =>
      if (sequence.isInstanceOf[Selection]) {
        if (editing) Main.drawing.stopEditingSelection()
        else Main.drawing.stopHighlightingSelection()
      }
  }
}