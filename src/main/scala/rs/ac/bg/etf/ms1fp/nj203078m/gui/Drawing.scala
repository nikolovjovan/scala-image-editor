package rs.ac.bg.etf.ms1fp.nj203078m.gui

import rs.ac.bg.etf.ms1fp.nj203078m.model.{FunctionManager, LayerManager, SelectionManager}

import java.awt.Color
import javax.swing.JViewport
import scala.swing.event.{MousePressed, MouseReleased}
import scala.swing.{Component, Dimension, Graphics2D}

class Drawing extends Component {
  foreground = Color.WHITE
  background = Color.LIGHT_GRAY

  var layerManager: LayerManager = new LayerManager
  var selectionManager: SelectionManager = new SelectionManager(render, layerManager)
  var functionManager: FunctionManager = new FunctionManager

  def render(): Unit = {
    layerManager.render()
    repaint()
  }

  listenTo(mouse.clicks, mouse.moves)
  reactions += {
    case e: MousePressed => if (e.source == this) {
      println("MousePressed!")
    }
    case e: MouseReleased => if (e.source == this) {
      println("MouseReleased!")
    }
  }

  override def paintComponent(g: Graphics2D): Unit = {
    super.paintComponent(g)
    val viewSize =
      if (peer.getParent != null && peer.getParent.isInstanceOf[JViewport])
        peer.getParent.asInstanceOf[JViewport].getExtentSize
      else
        new Dimension
    if (layerManager.output.isEmpty && layerManager.outputSize.width == 0 && layerManager.outputSize.height == 0) {
      g.setColor(background)
      g.fillRect(0, 0, viewSize.width, viewSize.height)
      if (preferredSize.width != 1 || preferredSize.height != 1) {
        preferredSize = new Dimension(1, 1)
        revalidate()
      }
    } else {
      val x: Int = if (layerManager.output.x > 0) layerManager.output.x else 1
      val y: Int = if (layerManager.output.y > 0) layerManager.output.y else 1
      val prefSize = new Dimension(
        x + layerManager.outputSize.width + 1,
        x + layerManager.outputSize.height + 1)
      g.setColor(background)
      g.fillRect(0, 0, viewSize.width max prefSize.width, viewSize.height max prefSize.height)
      g.setColor(Color.BLACK)
      g.drawRect(x - 1, y - 1, layerManager.outputSize.width + 1, layerManager.outputSize.height + 1)
      g.setColor(foreground)
      g.fillRect(x, y, layerManager.outputSize.width, layerManager.outputSize.height)
      if (!layerManager.output.isEmpty)
        g.drawImage(layerManager.frameBuffer, null, x, y)
      if (preferredSize != prefSize) {
        preferredSize = prefSize
        revalidate()
      }
    }
  }
}