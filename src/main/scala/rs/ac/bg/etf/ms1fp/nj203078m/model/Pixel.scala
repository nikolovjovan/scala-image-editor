package rs.ac.bg.etf.ms1fp.nj203078m.model

import java.awt.Color
import scala.language.implicitConversions

class Pixel(val alpha: Float, val red: Float, val green: Float, val blue: Float) {
  def this() = this(0.0f, 0.0f, 0.0f, 0.0f)

  def this(pixel: Pixel) = this(pixel.alpha, pixel.red, pixel.green, pixel.blue)

  def isEmpty: Boolean = this == Pixel.Empty

  def limit: Pixel = max(0.0f).min(1.0f)

  def withLayerAlpha(layerAlpha: Float) = new Pixel(alpha * layerAlpha, red, green, blue)

  // ref: https://en.wikipedia.org/wiki/Alpha_compositing
  //
  def over(that: Pixel): Pixel = {
    if (this.alpha <= 0.0f && that.alpha <= 0.0f)
      new Pixel(0.0f, 1.0f, 1.0f, 1.0f)
    else {
      val alphaOver = this.alpha + that.alpha * (1.0f - this.alpha)
      new Pixel(
        alphaOver,
        (this.red * this.alpha + that.red * that.alpha * (1.0f - this.alpha)) / alphaOver,
        (this.green * this.alpha + that.green * that.alpha * (1.0f - this.alpha)) / alphaOver,
        (this.blue * this.alpha + that.blue * that.alpha * (1.0f - this.alpha)) / alphaOver
      )
    }
  }

  def unary_op(op: Float => Float): Pixel =
    new Pixel(alpha, op(red), op(green), op(blue))

  def log: Pixel = unary_op(x => Math.log(x).toFloat)
  def abs: Pixel = unary_op(x => x.abs)

  def binary_op(that: Pixel)(op: (Float, Float) => Float): Pixel =
    new Pixel(this.alpha max that.alpha, op(this.red, that.red), op(this.green, that.green), op(this.blue, that.blue))

  def +(that: Pixel): Pixel = binary_op(that)((x, y) => x + y)
  def -(that: Pixel): Pixel = binary_op(that)((x, y) => x - y)
  def *(that: Pixel): Pixel = binary_op(that)((x, y) => x * y)
  def /(that: Pixel): Pixel = binary_op(that)((x, y) => if (y != 0.0f) x / y else Float.MaxValue)
  def **(that: Pixel): Pixel = binary_op(that)((x, y) => Math.pow(x, y).toFloat)

  def min(that: Pixel): Pixel = binary_op(that)((x, y) => x min y)
  def max(that: Pixel): Pixel = binary_op(that)((x, y) => x max y)

  override def toString: String = "Pixel: (" + alpha + ", " + red + ", " + green + ", " + blue + ")"
}

object Pixel {
  implicit def floatToPixel(value: Float): Pixel = new Pixel(0.0f, value, value, value)
  implicit def colorToPixel(color: Color): Pixel = fromARGB(color.getRGB)

  def fromARGB(argb: Int): Pixel =
    new Pixel(((argb >> 24) & 0xFF).toFloat / 255,
      ((argb >> 16) & 0xFF).toFloat / 255,
      ((argb >> 8) & 0xFF).toFloat / 255,
      ((argb >> 0) & 0xFF).toFloat / 255)

  def toARGB(pixel: Pixel): Int = {
    val limited = pixel.limit
    ((limited.alpha * 255).ceil.toInt << 24) +
    ((limited.red * 255).ceil.toInt << 16) +
    ((limited.green * 255).ceil.toInt << 8) +
    ((limited.blue * 255).ceil.toInt << 0)
  }

  val Empty: Pixel = new Pixel
}