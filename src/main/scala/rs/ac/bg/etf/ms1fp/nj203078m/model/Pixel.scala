package rs.ac.bg.etf.ms1fp.nj203078m.model

class Pixel(val alpha: Float, val red: Float, val green: Float, val blue: Float) {

  def this() = this(0.0f, 0.0f, 0.0f, 0.0f)
}

object Pixel {
  def fromARGB(argb: Int): Pixel =
    new Pixel(((argb >> 24) & 0xFF).toFloat / 255,
      ((argb >> 16) & 0xFF).toFloat / 255,
      ((argb >> 8) & 0xFF).toFloat / 255,
      ((argb >> 0) & 0xFF).toFloat / 255)

  def toARGB(pixel: Pixel): Int = {

    ((pixel.alpha * 255).ceil.toInt << 24) +
    ((pixel.red * 255).ceil.toInt << 16) +
    ((pixel.green * 255).ceil.toInt << 8) +
    ((pixel.blue * 255).ceil.toInt << 0)
  }
}