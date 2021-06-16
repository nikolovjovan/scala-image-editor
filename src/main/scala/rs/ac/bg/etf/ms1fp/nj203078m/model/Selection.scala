package rs.ac.bg.etf.ms1fp.nj203078m.model

import rs.ac.bg.etf.ms1fp.nj203078m.model.manager.ElementBase
import rs.ac.bg.etf.ms1fp.nj203078m.model.operation.Operation

import scala.collection.mutable.{ArrayBuffer, ListBuffer}

class Selection(var name: String) extends ElementBase {
  def this() = this("")

  override var id: Int = Selection.getNextId

  var rects: ArrayBuffer[Rect] = new ArrayBuffer[Rect]()

  def removeRects(selectionContains: Int => Boolean): Unit = {
    val tmp = rects
    rects = new ArrayBuffer[Rect]
    for (i <- tmp.indices)
      if (!selectionContains(i))
        rects += tmp(i)
  }
}

object Selection {
  var gblSelectionId: Int = 0

  def getNextId: Int = {
    gblSelectionId += 1
    gblSelectionId - 1
  }

  val Everything = new Selection("Everything")
}