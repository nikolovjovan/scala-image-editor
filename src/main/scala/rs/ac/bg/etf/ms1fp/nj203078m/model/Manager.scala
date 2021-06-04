package rs.ac.bg.etf.ms1fp.nj203078m.model

import java.util.regex.Pattern
import scala.collection.mutable.ArrayBuffer

trait ElementBase {
  var name: String
  var id: Int
}

// TODO: Look into TypeTag...
//
abstract class Manager[T <: ElementBase](nameTemplate: String, new_T: String => T, neverEmpty: Boolean = false) {
  protected var elements: ArrayBuffer[T] =
    if (neverEmpty) ArrayBuffer[T](new_T(nameTemplate + " 1")) else new ArrayBuffer[T]

  def apply(index: Int): T = if (index >= 0 && index < elements.size) elements(index) else throw new IndexOutOfBoundsException

  def count: Int = elements.size

  protected def addNewElement(position: Int = 0): Unit = {
    val pattern = Pattern.compile("^" + nameTemplate + " (\\d+)$")
    var nextIndex = 1
    for (element <- elements) {
      val matcher = pattern.matcher(element.name)
      if (matcher.matches()) {
        val idOpt = matcher.group(1).toIntOption
        val id = if (idOpt.isDefined) idOpt.get else 0
        if (id >= nextIndex)
          nextIndex = id + 1
      }
    }
    elements.insert(position, new_T(nameTemplate + " " + nextIndex))
  }

  protected def removeElements(selectionContains: Int => Boolean): Unit = {
    val tmp = elements
    elements = new ArrayBuffer[T]
    for (i <- tmp.indices)
      if (!selectionContains(i))
        elements += tmp(i)
    if (neverEmpty && elements.isEmpty)
      elements += new_T(nameTemplate + " 1")
  }
}