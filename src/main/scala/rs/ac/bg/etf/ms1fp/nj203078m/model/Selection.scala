package rs.ac.bg.etf.ms1fp.nj203078m.model

import rs.ac.bg.etf.ms1fp.nj203078m.model.traits.Sequence

import scala.collection.mutable.ArrayBuffer

class Selection(var name: String) extends Sequence[Rect] {
  override var id: Int = Selection.getNextId

  override def equals(obj: Any): Boolean = obj match {
    case that: Selection =>
      // Loading a project would have a different instance of Selection.Everything!
      // Therefore only compare selection id in this specific case!
      //
      if (this.id == 0 && that.id == 0) true
      else super.equals(obj)
    case _ => super.equals(obj)
  }

  def rects: ArrayBuffer[Rect] = components
  def addRect(rect: Rect, position: Int = count): Unit = addComponent(rect, position)
  def removeRects(selectionContains: Int => Boolean): Unit = removeComponents(selectionContains)
}

object Selection {
  var gblSelectionId: Int = 0

  def getNextId: Int = {
    gblSelectionId += 1
    gblSelectionId - 1
  }

  val Everything = new Selection("Everything")
}