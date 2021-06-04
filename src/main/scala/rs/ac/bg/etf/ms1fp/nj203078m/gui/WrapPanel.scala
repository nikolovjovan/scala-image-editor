package rs.ac.bg.etf.ms1fp.nj203078m.gui

// modified scala.swing.FlowPanel

import javax.swing.JPanel
import scala.swing.{Component, Dimension, Panel, SequentialContainer}

object WrapPanel {
  object Alignment extends Enumeration {
    import java.awt.FlowLayout._
    val Leading : Alignment.Value = Value(LEADING)
    val Trailing: Alignment.Value = Value(TRAILING)
    val Left    : Alignment.Value = Value(LEFT)
    val Right   : Alignment.Value = Value(RIGHT)
    val Center  : Alignment.Value = Value(CENTER)
  }
}

/**
 * A panel that arranges its contents horizontally, one after the other.
 * If they don't fit, this panel will try to insert line breaks.
 *
 * @see java.awt.FlowLayout
 */
class WrapPanel(alignment: WrapPanel.Alignment.Value)(contents0: Component*) extends Panel with SequentialContainer.Wrapper {
  override lazy val peer: JPanel =
    new JPanel(new WrapLayout(alignment.id)) with SuperMixin
  def this(contents0: Component*) = this(WrapPanel.Alignment.Center)(contents0: _*)
  def this() = this(WrapPanel.Alignment.Center)()

  contents ++= contents0

  private def layoutManager: WrapLayout = peer.getLayout.asInstanceOf[WrapLayout]

  def width   : Int         = peer.getSize().width
  def width_=(n: Int): Unit = peer.setSize(new Dimension(n, 1))

  def vGap    : Int         = layoutManager.getVgap
  def vGap_=(n: Int): Unit  = layoutManager.setVgap(n)

  def hGap    : Int         = layoutManager.getHgap
  def hGap_=(n: Int): Unit  = layoutManager.setHgap(n)

  def alignOnBaseline         : Boolean         = layoutManager.getAlignOnBaseline
  def alignOnBaseline_=(value : Boolean): Unit  = layoutManager.setAlignOnBaseline(value)
}