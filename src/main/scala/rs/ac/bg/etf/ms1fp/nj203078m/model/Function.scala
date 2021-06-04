package rs.ac.bg.etf.ms1fp.nj203078m.model

import scala.collection.mutable.ListBuffer

case class Function(initialName: String) extends Operation (initialName) with ElementBase {
  override val value: Float = 0.0f
  override var id: Int = Function.getNextId

  val operations: ListBuffer[Operation] = new ListBuffer[Operation]

  override def apply(pixel: Pixel): Pixel = {
    var tmp = pixel
    for (op <- operations)
      tmp = op.apply(tmp)
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
}