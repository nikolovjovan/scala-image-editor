package rs.ac.bg.etf.ms1fp.nj203078m.model.operation

import rs.ac.bg.etf.ms1fp.nj203078m.model.Pixel
import rs.ac.bg.etf.ms1fp.nj203078m.model.traits.Sequence

import scala.collection.mutable.ArrayBuffer

case class Function (initialName: String) extends PixelOperation (initialName) with Sequence[PixelOperation] {
  override val value: Float = 0.0f
  override var id: Int = Function.getNextId

  def operations: ArrayBuffer[PixelOperation] = components
  def addPixelOperation(pixelOp: PixelOperation, position: Int = count): Unit = addComponent(pixelOp, position)
  def removePixelOperations(selectionContains: Int => Boolean): Unit = removeComponents(selectionContains)

  override def apply(pixel: Pixel): Pixel = {
    var tmp = pixel
    for (op <- components)
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
    addComponent(SubtractFrom(1.0f))
  }

  val Desaturate: Function = new Function("Desaturate") {
    addComponent(new PixelOperation("Average") {
      override val value: Float = 0.0f
      override def apply(pixel: Pixel): Pixel = {
        val avg: Float = (pixel.red + pixel.green + pixel.blue) / 3.0f
        new Pixel(pixel.alpha, avg, avg, avg)
      }
    })
  }
}