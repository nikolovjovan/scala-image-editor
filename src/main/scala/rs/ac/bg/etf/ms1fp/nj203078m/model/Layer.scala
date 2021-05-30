package rs.ac.bg.etf.ms1fp.nj203078m.model

class Layer (var name: String, var image: Image, var alpha: Float) {

  var visible: Boolean = true

  def this(name: String, image: Image = new Image) = this(name, image, 0.0f)

  def show(): Unit = visible = true
  def hide(): Unit = visible = false
}