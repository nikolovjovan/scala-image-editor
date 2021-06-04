package rs.ac.bg.etf.ms1fp.nj203078m.model

import java.awt.Color

abstract class Operation (var name: String) {
  val value: Float
  def apply(pixel: Pixel): Pixel
  override def toString: String = name
}

case class FillWith (color: Color) extends Operation ("Fill with " + Pixel.colorToPixel(color)) {
  override val value: Float = 0.0f
  override def apply(pixel: Pixel): Pixel = color
}

case class Log () extends Operation ("Log") {
  override val value: Float = 0.0f
  override def apply(pixel: Pixel): Pixel = pixel.log
}

case class Abs () extends Operation ("Abs") {
  override val value: Float = 0.0f
  override def apply(pixel: Pixel): Pixel = pixel.abs
}

case class Add (value: Float) extends Operation ("Add " + value) {
  override def apply(pixel: Pixel): Pixel = pixel + value
}

case class SubtractBy (value: Float) extends Operation ("Subtract by " + value) {
  override def apply(pixel: Pixel): Pixel = pixel - value
}

case class SubtractFrom (value: Float) extends Operation ("Subtract from " + value) {
  override def apply(pixel: Pixel): Pixel = value - pixel
}

case class MultiplyBy (value: Float) extends Operation ("Multiply by " + value) {
  override def apply(pixel: Pixel): Pixel = pixel * value
}

case class DivideBy (value: Float) extends Operation ("Divide by " + value) {
  override def apply(pixel: Pixel): Pixel = pixel / value
}

case class Divide (value: Float) extends Operation ("Divide " + value) {
  override def apply(pixel: Pixel): Pixel = value / pixel
}

case class PowerBy (value: Float) extends Operation ("Power by " + value) {
  override def apply(pixel: Pixel): Pixel = pixel ** value
}

case class MinWith (value: Float) extends Operation ("Min with " + value) {
  override def apply(pixel: Pixel): Pixel = pixel min value
}

case class MaxWith (value: Float) extends Operation ("Max with " + value) {
  override def apply(pixel: Pixel): Pixel = pixel max value
}