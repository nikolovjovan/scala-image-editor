package rs.ac.bg.etf.ms1fp.nj203078m.gui

import rs.ac.bg.etf.ms1fp.nj203078m.interop.ImageConverter
import rs.ac.bg.etf.ms1fp.nj203078m.model.manager.LayerManager

import java.awt.Color
import java.awt.image.BufferedImage
import javax.swing.JViewport
import scala.swing.{Component, Dimension, Graphics2D}

class ImagePreview (var layerManager: LayerManager) extends Component {
  foreground = Color.WHITE
  background = Color.LIGHT_GRAY

  var frameBuffer: BufferedImage = ImageConverter.imgToBufImg(layerManager.output)

  def render(): Unit = {
    frameBuffer = ImageConverter.imgToBufImg(layerManager.render())
    repaint()
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
        g.drawImage(frameBuffer, null, layerManager.output.x + 1, layerManager.output.y + 1)
      g.setColor(Color.BLACK)
      g.drawRect(0, 0, layerManager.outputSize.width + 1, layerManager.outputSize.height + 1)
    }
    if (preferredSize != prefSize) {
      preferredSize = prefSize
      revalidate()
    }
  }
}