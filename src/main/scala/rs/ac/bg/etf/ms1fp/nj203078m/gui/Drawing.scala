package rs.ac.bg.etf.ms1fp.nj203078m.gui

import rs.ac.bg.etf.ms1fp.nj203078m.model.LayerManager

import java.awt.Color
import javax.swing.JViewport
import scala.swing.{Component, Dimension, Graphics2D}

class Drawing extends Component {
  background = Color.WHITE

  var layerManager: LayerManager = new LayerManager()

  def render(): Unit = {
    layerManager.render()
    repaint()
  }

  override def paintComponent(g: Graphics2D): Unit = {
    super.paintComponent(g)
    var width = layerManager.frameBuffer.getWidth
    var height = layerManager.frameBuffer.getHeight
    val viewSize =
      if (peer.getParent != null && peer.getParent.isInstanceOf[JViewport])
        peer.getParent.asInstanceOf[JViewport].getViewSize
      else
        new Dimension
    if (viewSize.width > width)
      width = viewSize.width
    if (viewSize.height > height)
      height = viewSize.height
    g.setColor(background)
    g.fillRect(0, 0, width, height)
    g.drawImage(layerManager.frameBuffer, null, layerManager.output.x, layerManager.output.y)
    if (preferredSize.width != width || preferredSize.height != height) {
      preferredSize = new Dimension(width, height)
      revalidate()
    }
//    g.setColor(background)
//    val rect: Image.Rect = new Image.Rect
//    for (layer <- layerManager.layers if layer.output.width > 0 && layer.output.height > 0) {
//      Image.updateImageRect(layer.output, rect)
//    }
//    if (rect.right <= 0 || rect.bottom <= 0)
//      g.fillRect(0, 0, this.size.width, this.size.height)
//    else
//      g.fillRect(0, 0, rect.right, rect.bottom)
//    for (layer <- layerManager.layers.reverse if layer.output.width > 0 && layer.output.height > 0) {
//      g.drawImage(ImageConverter.imgToBufImg(layer.output), null, 0, 0)
//    }
//    if (rect.right > 0 && rect.bottom > 0) {
//      preferredSize = new Dimension(rect.right, rect.bottom)
//      revalidate()
//    }
    //    g.drawImage(layerManager.frameBuffer, null, 0, 0)
//    preferredSize = new Dimension(layerManager.frameBuffer.getWidth, layerManager.frameBuffer.getHeight)
  }
}