package rs.ac.bg.etf.ms1fp.nj203078m.gui

import rs.ac.bg.etf.ms1fp.nj203078m.gui.Main.{createHorizontalBoxPanel, createTableScrollPane, createVerticalSplitPane}
import rs.ac.bg.etf.ms1fp.nj203078m.model.manager.LayerManager

import java.io.File
import javax.imageio.ImageIO
import javax.imageio.stream.FileImageOutputStream
import javax.swing.filechooser.FileNameExtensionFilter
import javax.swing.table.AbstractTableModel
import scala.collection.mutable.ArrayBuffer
import scala.swing.Swing.{CompoundBorder, EmptyBorder, EmptyIcon, EtchedBorder, TitledBorder}
import scala.swing.event.{MouseClicked, TableRowsSelected, ValueChanged, WindowClosing}
import scala.swing.{Action, Alignment, BorderPanel, BoxPanel, Button, Dialog, Dimension, FileChooser, Label, Orientation, ScrollPane, Slider, SplitPane, Table, TextField, Window}

class ExportImageDialog (owner: Window, layerManager: LayerManager) extends Dialog (owner) {
  case class LayerAttributes (var visible: Boolean, var alpha: Float)

  var isLayerVisible: Int => Boolean = null
  var getLayerAlpha: Int => Float = null

  var layerAttributes: ArrayBuffer[LayerAttributes] = new ArrayBuffer[LayerAttributes]()

  var exportPath: String = ""

  def restoreLayerAccessors(): Unit = {
    layerManager.isLayerVisible = isLayerVisible
    layerManager.getLayerAlpha = getLayerAlpha
  }

  for (layer <- layerManager.layers)
    layerAttributes.addOne(LayerAttributes(layer.visible, layer.alpha))

  isLayerVisible = layerManager.isLayerVisible
  layerManager.isLayerVisible = z => layerAttributes(z).visible
  getLayerAlpha = layerManager.getLayerAlpha
  layerManager.getLayerAlpha = z => layerAttributes(z).alpha

  val preview: ImagePreview = new ImagePreview(layerManager)

  contents = new BorderPanel {
    val previewView: ScrollPane = new ScrollPane(preview) {
      preferredSize = new Dimension(400, 400)
      minimumSize = preferredSize
    }

    val sideView: BoxPanel = new BoxPanel(Orientation.Vertical) {
      contents += new BoxPanel(Orientation.Vertical) {
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
                layerAttributes(layerId).alpha = value.toFloat / 100.0f
              preview.render()
            }
          }
        }

        contents += createHorizontalBoxPanel(Seq(sldOpacity, lblOpacity))

        val table: Table = new Table {
          autoResizeMode = Table.AutoResizeMode.AllColumns
          model = new AbstractTableModel {
            override def getRowCount: Int = preview.layerManager.count
            override def getColumnCount: Int = 2
            override def getValueAt(rowIndex: Int, columnIndex: Int): Any = columnIndex match {
              case 0 => layerAttributes(rowIndex).visible
              case _ => preview.layerManager(rowIndex).name
            }
            override def isCellEditable(rowIndex: Int, columnIndex: Int): Boolean = columnIndex match {
              case 0 => true
              case _ => false
            }
            override def setValueAt(aValue: Any, rowIndex: Int, columnIndex: Int): Unit = columnIndex match {
              case 0 =>
                layerAttributes(rowIndex).visible = aValue.asInstanceOf[Boolean]
                preview.render()
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
              sldOpacity.value = if (selection.rows.size == 1) (layerAttributes(peer.getSelectedRow).alpha * 100.0f).toInt else 100
            }
          }
        }

        listenTo(mouse.clicks)
        reactions += {
          case _: MouseClicked => table.peer.clearSelection()
        }

        contents += createTableScrollPane(table)
      }

      contents += new BoxPanel(Orientation.Vertical) {
        border = CompoundBorder(TitledBorder(EtchedBorder, "Export"), EmptyBorder(5, 5, 5, 10))
        xLayoutAlignment = java.awt.Component.CENTER_ALIGNMENT

        contents += new BoxPanel(Orientation.Horizontal) {
          val txtExportPath: TextField = new TextField {
            text = exportPath
            maximumSize = new Dimension(1000, 26)
          }
          contents += txtExportPath
          contents += new Button(Action("...") {
            val chooser = new FileChooser(new File(System.getProperty("user.dir")))
            chooser.peer.addChoosableFileFilter(new FileNameExtensionFilter("PNG image (*.png)", "png"))
            chooser.peer.addChoosableFileFilter(new FileNameExtensionFilter("JPEG image (*.jpg, *.jpeg)", "jpg", "jpeg"))
            if (chooser.showSaveDialog(this) == FileChooser.Result.Approve) {
              exportPath = chooser.selectedFile.getAbsolutePath
              if (!chooser.peer.getFileFilter.isInstanceOf[FileNameExtensionFilter]) {
                // All files filter
                //
                if (!exportPath.endsWith(".png") && !exportPath.endsWith(".jpg") && !exportPath.endsWith(".jpeg")) {
                  exportPath += ".png" // assume PNG format by default
                }
              } else {
                // PNG or JPEG file filter
                //
                val fileFilter = chooser.peer.getFileFilter.asInstanceOf[FileNameExtensionFilter]
                var extGood: Boolean = false
                try {
                  for (ext <- fileFilter.getExtensions)
                    if (exportPath.endsWith(ext)) {
                      extGood = true
                      throw new Exception
                    }
                } catch {
                  case _: Exception =>
                }
                if (!extGood) {
                  exportPath += "." + fileFilter.getExtensions.array(0)
                }
              }
              txtExportPath.text = exportPath
            }
          })
        }

        contents += new Button(Action("Export") {
          if (exportPath.isEmpty) {
            Dialog.showMessage(this, "Output path not selected! Please choose output path.", "Error!", Dialog.Message.Error)
          } else {
            try {
              ImageIO.write(
                preview.frameBuffer,
                exportPath.substring(exportPath.lastIndexOf('.') + 1).toLowerCase,
                new FileImageOutputStream(new File(exportPath)))
              close()
              restoreLayerAccessors()
            } catch {
              case ex: Exception => Dialog.showMessage(this, "Failed to export image. Error: " + ex, "Error!", Dialog.Message.Error)
            }
          }
        })
      }
    }

    val center: SplitPane = createVerticalSplitPane(previewView, sideView)

    layout(center) = BorderPanel.Position.Center
  }

  if (owner != null) {
    pack()
    setLocationRelativeTo(owner)
  }

  title = "Scala Image Editor - Export Image"
  modal = true
  peer.setAlwaysOnTop(true)

  pack()
  minimumSize = size

  reactions += {
    case _: WindowClosing => restoreLayerAccessors()
  }
}