package rs.ac.bg.etf.ms1fp.nj203078m.model.manager

import rs.ac.bg.etf.ms1fp.nj203078m.model.operation.OperationSeq

class OperationSeqManager extends Manager[OperationSeq]("Operation", name => new OperationSeq(name)) {
  def addOperationSeq(position: Int = count): Unit = super.addElement(position)
  def removeOperationSeqs(selectionContains: Int => Boolean): Unit = super.removeElements(selectionContains)
}