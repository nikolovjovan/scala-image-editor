package rs.ac.bg.etf.ms1fp.nj203078m.model.traits

import scala.collection.mutable.ArrayBuffer

trait Sequence[T] extends ElementBase {
  protected var components: ArrayBuffer[T] = new ArrayBuffer[T]

  def apply(index: Int): T = if (index >= 0 && index < components.size) components(index) else throw new IndexOutOfBoundsException

  def indices: Range = components.indices

  def count: Int = components.size

  def addComponent(component: T, position: Int = count): Unit =
    if (position == count)
      components.addOne(component)
    else
      components.insert(0, component)

  def removeComponents(selectionContains: Int => Boolean): Unit = {
    val tmp = components
    components = new ArrayBuffer[T]
    for (i <- tmp.indices)
      if (!selectionContains(i))
        components += tmp(i)
  }
}
