package rs.ac.bg.etf.ms1fp.nj203078m.gui

import rs.ac.bg.etf.ms1fp.nj203078m.model.Layer
import rs.ac.bg.etf.ms1fp.nj203078m.model.manager.{FunctionManager, LayerManager, OperationSeqManager, SelectionManager}

import java.awt.Color
import scala.swing.event.{MouseDragged, MousePressed, MouseReleased}
import scala.swing.{Graphics2D, Point, Rectangle}

class Drawing extends ImagePreview(new LayerManager) {
  foreground = Color.WHITE
  background = Color.LIGHT_GRAY

  var selectionManager: SelectionManager = new SelectionManager(render, layerManager)
  var functionManager: FunctionManager = new FunctionManager
  var operationSeqManager: OperationSeqManager = new OperationSeqManager

  var clickedLayer: Option[Layer] = None
  var clickPoint: Point = new Point
  var moveRect: Rectangle = new Rectangle
  var dragRect: Rectangle = new Rectangle

  listenTo(mouse.clicks, mouse.moves)
  reactions += {
    case e: MousePressed => if (e.source == this) {
      clickedLayer = layerManager.getLayerAt(e.point)
      if (clickedLayer.isDefined) {
        clickPoint = e.point
        moveRect = new Rectangle(
          clickedLayer.get.x,
          clickedLayer.get.y,
          clickedLayer.get.output.width,
          clickedLayer.get.output.height)
        repaint()
      }
    }
    case e: MouseDragged => if (e.source == this) {
      if (clickedLayer.isDefined) {
        moveRect.x = clickedLayer.get.x + e.point.x - clickPoint.x
        moveRect.y = clickedLayer.get.y + e.point.y - clickPoint.y
        repaint()
      }
    }
    case e: MouseReleased => if (e.source == this) {
      if (clickedLayer.isDefined) {
        clickedLayer.get.x += e.point.x - clickPoint.x
        clickedLayer.get.y += e.point.y - clickPoint.y
        clickedLayer = None
        render()
      }
    }
  }

  override def paintComponent(g: Graphics2D): Unit = {
    super.paintComponent(g)
    if (clickedLayer.isDefined) {
      g.setColor(Color.RED)
      g.drawRect(moveRect.x, moveRect.y, moveRect.width, moveRect.height)
    }
  }
}