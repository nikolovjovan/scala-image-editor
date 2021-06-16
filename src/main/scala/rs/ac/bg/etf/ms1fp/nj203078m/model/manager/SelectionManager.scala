package rs.ac.bg.etf.ms1fp.nj203078m.model.manager

import rs.ac.bg.etf.ms1fp.nj203078m.model.Selection
import rs.ac.bg.etf.ms1fp.nj203078m.model.operation.Operation

import scala.collection.mutable

class SelectionManager(renderDrawing: () => Unit, layerManager: LayerManager) extends Manager[Selection]("Selection", name => new Selection(name)) {
  var activeSelection: Selection = Selection.Everything

  def addNewSelection(position: Int = count): Unit = super.addNewElement(position)
  def removeSelections(selectionContains: Int => Boolean): Unit = {
    val selectionIds: mutable.HashSet[Int] = new mutable.HashSet[Int]
    for (i <- elements.indices)
      if (selectionContains(i))
        selectionIds.add(elements(i).id)
    for (i <- layerManager.indices)
      layerManager(i).removeAllImagesFromSelections(selectionIds.contains)
    super.removeElements(x => if (x != 0) selectionContains(x) else false)
    activeSelection = Selection.Everything
  }

  elements.addOne(Selection.Everything)

  def execute(layerSelectionContains: Int => Boolean, op: Operation): Unit = {
    for (i <- layerManager.indices if layerSelectionContains(i) && layerManager(i).visible) {
      if (activeSelection == Selection.Everything)
        layerManager(i).execute(activeSelection, op, layerManager.outputSize)
      else
        layerManager(i).execute(activeSelection, op)
    }
    renderDrawing()
  }
}