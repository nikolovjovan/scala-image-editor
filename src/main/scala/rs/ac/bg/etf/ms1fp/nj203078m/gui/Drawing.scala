package rs.ac.bg.etf.ms1fp.nj203078m.gui

import java.awt.geom.Line2D
import java.awt.image.{BufferedImage, BufferedImageOp, RescaleOp}
import java.awt.{AlphaComposite, BasicStroke, Color, RenderingHints}
import java.io.File
import javax.imageio.ImageIO
import scala.runtime.Nothing$
import scala.swing.{BoxPanel, Dimension, Graphics2D, Orientation}

class Drawing (src: Boolean) extends BoxPanel(Orientation.Vertical) {
  // add layers as array of buffered images to be rendered on paintcomponent?!?
  // alpha transparency inside bufferedimage, see how to load images from disk into buffered image
  // possibly create abstractions for layers to make selection application easier
  // all changes get applied to layers individually, then rendered in this drawing class...

  private val SIZE = 500
  private val INSET = 64
  private val OVER_HALF = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f)

  background = Color.GRAY

  private val image: BufferedImage = ImageIO.read(new File("res/sample_png.png"))

  private val image2: BufferedImage = ImageIO.read(new File("res/sample_jpg.jpg"))

  override def preferredSize: Dimension = new Dimension(SIZE, SIZE)

  override def paintComponent(g: Graphics2D): Unit = {
    super.paintComponent(g)
    g.drawImage(image, null, 0, 0)
    g.drawImage(image2, null, 50, 50)
    preferredSize = new Dimension(50 + image2.getWidth, 50 + image2.getHeight)
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