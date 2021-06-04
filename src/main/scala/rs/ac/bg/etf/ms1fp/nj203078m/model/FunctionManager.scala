package rs.ac.bg.etf.ms1fp.nj203078m.model

class FunctionManager extends Manager[Function]("Function", name => new Function(name)) {
    def addNewFunction(): Unit = super.addNewElement()
    def removeFunctions(selectionContains: Int => Boolean): Unit = super.removeElements(selectionContains)
}
