package rs.ac.bg.etf.ms1fp.nj203078m.model.operation

import rs.ac.bg.etf.ms1fp.nj203078m.model.{Image, Pixel}

import java.awt.Color

abstract class PixelOperation (opName: String) extends Operation (opName) {
  override val N: Int = 0
  override def execute(image: Image, cx: Int, cy: Int): Pixel = apply(getInputPixel(image, cx, cy))
  val value: Float
  def apply(pixel: Pixel): Pixel
  override def toString: String = name
}

case class FillWith (color: Color) extends PixelOperation ("Fill with " + Pixel.colorToPixel(color)) {
  override val value: Float = 0.0f
  override def apply(pixel: Pixel): Pixel = color
}

case class Log () extends PixelOperation ("Log") {
  override val value: Float = 0.0f
  override def apply(pixel: Pixel): Pixel = pixel.log
}

case class Abs () extends PixelOperation ("Abs") {
  override val value: Float = 0.0f
  override def apply(pixel: Pixel): Pixel = pixel.abs
}

case class Add (value: Float) extends PixelOperation ("Add " + value) {
  override def apply(pixel: Pixel): Pixel = pixel + value
}

case class SubtractBy (value: Float) extends PixelOperation ("Subtract by " + value) {
  override def apply(pixel: Pixel): Pixel = pixel - value
}

case class SubtractFrom (value: Float) extends PixelOperation ("Subtract from " + value) {
  override def apply(pixel: Pixel): Pixel = value - pixel
}

case class MultiplyBy (value: Float) extends PixelOperation ("Multiply by " + value) {
  override def apply(pixel: Pixel): Pixel = pixel * value
}

case class DivideBy (value: Float) extends PixelOperation ("Divide by " + value) {
  override def apply(pixel: Pixel): Pixel = pixel / value
}

case class Divide (value: Float) extends PixelOperation ("Divide " + value) {
  override def apply(pixel: Pixel): Pixel = value / pixel
}

case class PowerBy (value: Float) extends PixelOperation ("Power by " + value) {
  override def apply(pixel: Pixel): Pixel = pixel ** value
}

case class MinWith (value: Float) extends PixelOperation ("Min with " + value) {
  override def apply(pixel: Pixel): Pixel = pixel min value
}

case class MaxWith (value: Float) extends PixelOperation ("Max with " + value) {
  override def apply(pixel: Pixel): Pixel = pixel max value
}