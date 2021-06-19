package rs.ac.bg.etf.ms1fp.nj203078m.model

import scala.language.implicitConversions
import scala.swing.{Dimension, Point, Rectangle}

class Rect extends Serializable {
  var left: Int = Int.MaxValue
  var top: Int = Int.MaxValue
  var right: Int = Int.MinValue
  var bottom: Int = Int.MinValue

  def this(x: Int, y: Int, width: Int, height: Int) {
    this()
    this.x = x
    this.y = y
    this.width = width
    this.height = height
  }

  def this(width: Int, height: Int) = this(0, 0, width, height)

  def this(rect: Rect) {
    this()
    this.left = rect.left
    this.top = rect.top
    this.right = rect.right
    this.bottom = rect.bottom
  }

  def this(p: Point) = this(p.x, p.y, 0, 0)
  def this(d: Dimension) = this(0, 0, d.width, d.height)
  def this(p: Point, d: Dimension) = this(p.x, p.y, d.width, d.height)
  def this(r: Rectangle) = this(r.x, r.y, r.width, r.height)

  def x: Int = left
  def x_=(x: Int): Unit = left = x

  def y: Int = top
  def y_=(y: Int): Unit = top = y

  def width: Int = right - left
  def width_=(width: Int): Unit = right = left + width

  def height: Int = bottom - top
  def height_=(height: Int): Unit = bottom = top + height

  def toRectangle: Rectangle = new Rectangle(x, y, width, height)

  override def toString: String = "[" + x + ", " + y + ", " + width + ", " + height + "]"
}

object Rect {
  def fromRectangle(rectangle: Rectangle): Rect = new Rect(rectangle)

  implicit def convertRectangleToRect(rectangle: Rectangle): Rect = fromRectangle(rectangle)
}