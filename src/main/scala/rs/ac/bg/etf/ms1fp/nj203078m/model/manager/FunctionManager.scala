package rs.ac.bg.etf.ms1fp.nj203078m.model.manager

import rs.ac.bg.etf.ms1fp.nj203078m.model.operation.Function

class FunctionManager extends Manager[Function]("Function", name => new Function(name)) {
  elements.addOne(Function.Inverse)
  elements.addOne(Function.Desaturate)

  def addFunction(position: Int = count): Unit = super.addElement(position)
  def removeFunctions(selectionContains: Int => Boolean): Unit =
    super.removeElements(i => if (i == 0 || i == 1) false else selectionContains(i))
}