package rs.ac.bg.etf.ms1fp.nj203078m.model

class OperationSeqManager extends Manager[OperationSeq]("Operation", name => new OperationSeq(name)) {
  def addNewOperationSeq(position: Int = count): Unit = super.addNewElement(position)
  def removeOperationSeqs(selectionContains: Int => Boolean): Unit = super.removeElements(selectionContains)
}