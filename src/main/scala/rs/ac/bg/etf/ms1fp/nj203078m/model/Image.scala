package rs.ac.bg.etf.ms1fp.nj203078m.model

import rs.ac.bg.etf.ms1fp.nj203078m.interop.ImageConverter

import java.io.File
import javax.imageio.ImageIO
import scala.util.{Failure, Success, Try}

class Image (var fileName: String) {
  def this() = this("")

  def this(width: Int, height: Int, x: Int = 0, y: Int = 0) = {
    this()
    pixels = Array.tabulate(width, height)((_, _) => new Pixel())
    this.x = x
    this.y = y
  }

  var pixels: Image.PixelMatrix = Array.empty

  var x: Int = 0
  var y: Int = 0

  def width: Int = pixels.length
  def height: Int = if (pixels.isEmpty) 0 else pixels(0).length

  if (fileName.nonEmpty) readPixelMatrix(fileName)

  def readPixelMatrix(fileName: String): Unit = Try(ImageIO.read(new File(fileName))) match {
    case Success(image) =>
      pixels = ImageConverter.bufImgToPixelMatrix(image)
      this.fileName = fileName
    case Failure(f) => println(f)
  }
}

object Image {
  type PixelVector = Array[Pixel]
  type PixelMatrix = Array[PixelVector]

  val Empty = new Image

  class Rect {
    var left: Int = Int.MaxValue
    var top: Int = Int.MaxValue
    var right: Int = Int.MinValue
    var bottom: Int = Int.MinValue

    def x: Int = left
    def y: Int = top
    def width: Int = right - left
    def height: Int = bottom - top
  }

  def updateImageRect(img: Image, rect: => Rect): Unit = {
    if (img.x < rect.left)
      rect.left = img.x
    if (img.x + img.width > rect.right)
      rect.right = img.x + img.width
    if (img.y < rect.top)
      rect.top = img.y
    if (img.y + img.height > rect.bottom)
      rect.bottom = img.y + img.height
  }
}