package rs.ac.bg.etf.ms1fp.nj203078m.model

import scala.collection.mutable.ArrayBuffer
import scala.swing.Rectangle

class Selection(var name: String) extends ElementBase {
  def this() = this("")

  override var id: Int = Selection.getNextId

  var rects: ArrayBuffer[Rectangle] = new ArrayBuffer[Rectangle]()

  var affectedLayers: ArrayBuffer[Int] = new ArrayBuffer[Int]()
}

object Selection {
  var gblSelectionId: Int = 0

  def getNextId: Int = {
    gblSelectionId += 1
    gblSelectionId - 1
  }

  val Everything = new Selection
}