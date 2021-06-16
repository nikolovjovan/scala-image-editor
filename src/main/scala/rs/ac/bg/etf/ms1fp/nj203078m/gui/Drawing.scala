package rs.ac.bg.etf.ms1fp.nj203078m.gui

import rs.ac.bg.etf.ms1fp.nj203078m.model.Layer
import rs.ac.bg.etf.ms1fp.nj203078m.model.manager.{FunctionManager, LayerManager, OperationSeqManager, SelectionManager}

import java.awt.Color
import javax.swing.JViewport
import scala.swing.event.{MouseDragged, MousePressed, MouseReleased}
import scala.swing.{Component, Dimension, Graphics2D, Point, Rectangle}

class Drawing extends Component {
  foreground = Color.WHITE
  background = Color.LIGHT_GRAY

  var layerManager: LayerManager = new LayerManager
  var selectionManager: SelectionManager = new SelectionManager(render, layerManager)
  var functionManager: FunctionManager = new FunctionManager
  var operationSeqManager: OperationSeqManager = new OperationSeqManager

  var clickedLayer: Option[Layer] = None
  var clickPoint: Point = new Point
  var moveRect: Rectangle = new Rectangle

  def render(): Unit = {
    layerManager.render()
    repaint()
  }

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
    val viewSize =
      if (peer.getParent != null && peer.getParent.isInstanceOf[JViewport])
        peer.getParent.asInstanceOf[JViewport].getExtentSize
      else
        new Dimension
    var prefSize = new Dimension(1, 1)
    if (layerManager.output.isEmpty && layerManager.outputSize.width == 0 && layerManager.outputSize.height == 0) {
      g.setColor(background)
      g.fillRect(0, 0, viewSize.width, viewSize.height)
    } else {
      prefSize = new Dimension(
        layerManager.outputSize.width + 2,
        layerManager.outputSize.height + 2)
      g.setColor(background)
      g.fillRect(0, 0, viewSize.width max prefSize.width, viewSize.height max prefSize.height)
      g.setColor(foreground)
      g.fillRect(1, 1, layerManager.outputSize.width, layerManager.outputSize.height)
      if (!layerManager.output.isEmpty)
        g.drawImage(layerManager.frameBuffer, null, layerManager.output.x + 1, layerManager.output.y + 1)
      g.setColor(Color.BLACK)
      g.drawRect(0, 0, layerManager.outputSize.width + 1, layerManager.outputSize.height + 1)
    }
    if (clickedLayer.isDefined) {
      g.setColor(Color.RED)
      g.drawRect(moveRect.x, moveRect.y, moveRect.width, moveRect.height)
    }
    if (preferredSize != prefSize) {
      preferredSize = prefSize
      revalidate()
    }
  }
}