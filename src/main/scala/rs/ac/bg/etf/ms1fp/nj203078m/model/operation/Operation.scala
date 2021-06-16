package rs.ac.bg.etf.ms1fp.nj203078m.model.operation

import rs.ac.bg.etf.ms1fp.nj203078m.model.{Image, Pixel}

import scala.collection.immutable.ArraySeq

abstract class Operation (var name: String) {
  val N: Int
  def D: Int = 2 * N + 1

  def execute(image: Image, cx: Int, cy: Int): Pixel

  override def toString: String = name

  protected def getInputPixel(image: Image, rx: Int, ry: Int): Pixel =
    if (rx < 0 || ry < 0 || rx >= image.width || ry >= image.height)
      Pixel.Empty
    else
      image(rx)(ry)
}

case class Median (N: Int) extends Operation ("Median N = " + N) {
  require(N > 0, "Median requires at least a filter of dimension (2 * N + 1) = 3!")

  private def median(s: Seq[Float]): Float = {
    val N = s.size
    val sorted = s.sortWith(_ < _)
    if (N % 2 == 0)
      (sorted(N / 2 - 1) + sorted(N / 2)) / 2.0f
    else
      sorted(N / 2)
  }

  override def execute(image: Image, cx: Int, cy: Int): Pixel =
    new Pixel(
      median(ArraySeq.tabulate(D * D)(i => getInputPixel(image, i / D - N + cx, i % D - N + cy).alpha)),
      median(ArraySeq.tabulate(D * D)(i => getInputPixel(image, i / D - N + cx, i % D - N + cy).red)),
      median(ArraySeq.tabulate(D * D)(i => getInputPixel(image, i / D - N + cx, i % D - N + cy).green)),
      median(ArraySeq.tabulate(D * D)(i => getInputPixel(image, i / D - N + cx, i % D - N + cy).blue))
    )
}

case class WeightedMean (N: Int, weights: Image.PixelMatrix) extends Operation ("Weighted mean N = " + N + " weights: " + weights.mkString("[", ", ", "]")) {
  require(N > 0, "Weighted mean requires at least a filter of dimension (2 * N + 1) = 3!")
  require(weights.length == D && weights(0).length == D, "Weighted mean requires a weight matrix with dimension " + D + "x" + D + "!")

  override def execute(image: Image, cx: Int, cy: Int): Pixel = {
    var red, green, blue: Float = 0.0f
    for (x <- 0 until D)
      for (y <- 0 until D) {
        val input: Pixel = getInputPixel(image, x - N + cx, y - N + cy) * weights(x)(y)
        red += input.red
        green += input.green
        blue += input.blue
      }
    val size: Int = D * D
    new Pixel(image(cx)(cy).alpha, red / size, green / size, blue / size)
  }
}