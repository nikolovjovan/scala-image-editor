package rs.ac.bg.etf.ms1fp.nj203078m.model.operation

import rs.ac.bg.etf.ms1fp.nj203078m.model.Pixel
import rs.ac.bg.etf.ms1fp.nj203078m.model.manager.ElementBase

import scala.collection.mutable.ListBuffer

case class Function (initialName: String) extends PixelOperation (initialName) with ElementBase {
  override val value: Float = 0.0f
  override var id: Int = Function.getNextId

  var operations: ListBuffer[PixelOperation] = new ListBuffer[PixelOperation]

  def removeOperations(selectionContains: Int => Boolean): Unit = {
    val tmp = operations
    operations = new ListBuffer[PixelOperation]
    for (i <- tmp.indices)
      if (!selectionContains(i))
        operations += tmp(i)
  }

  override def apply(pixel: Pixel): Pixel = {
    var tmp = pixel
    for (op <- operations)
      tmp = op(tmp)
    tmp
  }
}

object Function {
  var gblFunctionId: Int = 0

  def getNextId: Int = {
    gblFunctionId += 1
    gblFunctionId - 1
  }

  val Inverse: Function = new Function("Inverse") {
    operations += SubtractFrom(1.0f)
  }

  val Desaturate: Function = new Function("Desaturate") {
    operations += new PixelOperation("Average") {
      override val value: Float = 0.0f
      override def apply(pixel: Pixel): Pixel = {
        val avg: Float = (pixel.red + pixel.green + pixel.blue) / 3.0f
        new Pixel(pixel.alpha, avg, avg, avg)
      }
    }
  }
}