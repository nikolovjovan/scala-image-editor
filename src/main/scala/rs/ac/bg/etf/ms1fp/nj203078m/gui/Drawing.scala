package rs.ac.bg.etf.ms1fp.nj203078m.gui

import rs.ac.bg.etf.ms1fp.nj203078m.interop.ImageConverter
import rs.ac.bg.etf.ms1fp.nj203078m.model.LayerManager

import java.awt.image.BufferedImage
import java.awt.{AlphaComposite, Color}
import java.io.File
import javax.imageio.ImageIO
import scala.swing.{BoxPanel, Dimension, Graphics2D, Orientation}

class Drawing extends BoxPanel(Orientation.Vertical) {
  // add layers as array of buffered images to be rendered on paintcomponent?!?
  // alpha transparency inside bufferedimage, see how to load images from disk into buffered image
  // possibly create abstractions for layers to make selection application easier
  // all changes get applied to layers individually, then rendered in this drawing class...

  var layerManager: LayerManager = new LayerManager

  private val OVER_HALF = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f)

  background = Color.WHITE

  private val image: BufferedImage = ImageIO.read(new File("res/sample_png.png"))

  private val image2: BufferedImage = ImageIO.read(new File("res/sample_jpg.jpg"))

  override def paintComponent(g: Graphics2D): Unit = {
    super.paintComponent(g)
    val frameBuffer = ImageConverter.imgToBufImg(layerManager.outputImage)
    g.drawImage(ImageConverter.imgToBufImg(layerManager.outputImage), null, 0, 0)
    preferredSize = new Dimension(frameBuffer.getWidth, frameBuffer.getHeight)
//    g.setRenderingHint(
//      RenderingHints.KEY_ANTIALIASING,
//      RenderingHints.VALUE_ANTIALIAS_ON)
//    val line1 = new Line2D.Double(INSET, INSET, preferredSize.getWidth - INSET, preferredSize.getHeight - INSET)
//    val line2 = new Line2D.Double(preferredSize.getWidth - INSET, INSET, INSET, preferredSize.getHeight - INSET)
//    g.setStroke(new BasicStroke(64, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL))
//    g.setComposite(OVER_HALF)
//    g.setColor(Color.red)
//    g.draw(line1)
//    if (src) g.setComposite(AlphaComposite.Src)
//    g.setColor(Color.blue)
//    g.draw(line2)
  }
}