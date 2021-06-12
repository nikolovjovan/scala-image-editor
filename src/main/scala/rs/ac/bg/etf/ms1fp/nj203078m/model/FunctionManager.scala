package rs.ac.bg.etf.ms1fp.nj203078m.model

class FunctionManager extends Manager[Function]("Function", name => new Function(name)) {
  elements.addOne(Function.Inverse)
  elements.addOne(Function.Desaturate)

  def addNewFunction(position: Int = count): Unit = super.addNewElement(position)
  def removeFunctions(selectionContains: Int => Boolean): Unit =
    super.removeElements(i => if (i == 0 || i == 1) false else selectionContains(i))
}