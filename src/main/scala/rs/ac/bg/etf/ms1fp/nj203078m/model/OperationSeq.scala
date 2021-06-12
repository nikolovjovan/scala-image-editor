package rs.ac.bg.etf.ms1fp.nj203078m.model

import scala.collection.mutable.ListBuffer

class OperationSeq (initialName: String) extends Operation (initialName) with ElementBase {
  override val N: Int = -1
  override var id: Int = OperationSeq.getNextId

  val operations: ListBuffer[Operation] = new ListBuffer[Operation]

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