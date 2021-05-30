package rs.ac.bg.etf.ms1fp.nj203078m.model

import java.io.File
import javax.imageio.ImageIO
import scala.util.{Failure, Success, Try}

class Image (val fileName: String) {
  type PixelMatrix = Array[Array[Pixel]]

  var pixels: PixelMatrix = Array.empty

  def this() = this("")

  def width: Int = if (pixels.isEmpty) 0 else pixels(0).length
  def height: Int = pixels.length

  if (fileName.nonEmpty) readPixelMatrix(fileName)

  def readPixelMatrix(fileName: String): Unit = Try(ImageIO.read(new File(fileName))) match {
    case Success(image) => pixels = Array.tabulate(image.getWidth, image.getHeight)((x, y) => Pixel.fromARGB(image.getRGB(x, y)))
    case Failure(f) => println(f)
  }
}