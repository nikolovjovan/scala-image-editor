package rs.ac.bg.etf.ms1fp.nj203078m.model

class SelectionManager(renderDrawing: () => Unit, layerManager: LayerManager) extends Manager[Selection]("Selection", name => new Selection(name)) {
  var activeSelection: Selection = Selection.Everything

  def addNewSelection(): Unit = super.addNewElement()
  def removeSelections(selectionContains: Int => Boolean): Unit = {
    super.removeElements(selectionContains)
    activeSelection = Selection.Everything
  }

  def execute(op: Operation): Unit = {
    // TODO: Execute function using current selection...
    //
    renderDrawing()
  }
}