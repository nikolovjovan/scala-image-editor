package rs.ac.bg.etf.ms1fp.nj203078m.model.operation

import rs.ac.bg.etf.ms1fp.nj203078m.model.traits.Sequence
import rs.ac.bg.etf.ms1fp.nj203078m.model.{Image, Pixel}

import scala.collection.mutable.ArrayBuffer

class OperationSeq (initialName: String) extends Operation (initialName) with Sequence[Operation] {
  override val N: Int = -1
  override var id: Int = OperationSeq.getNextId

  def operations: ArrayBuffer[Operation] = components
  def addOperation(operation: Operation, position: Int = count): Unit = addComponent(operation, position)
  def removeOperations(selectionContains: Int => Boolean): Unit = removeComponents(selectionContains)

  // Actual execute implementation in Layer.execute.
  //
  override def execute(image: Image, cx: Int, cy: Int): Pixel = image(cx)(cy)
}

object OperationSeq {
  var gblOperationSeqId: Int = 0

  def getNextId: Int = {
    gblOperationSeqId += 1
    gblOperationSeqId - 1
  }
}