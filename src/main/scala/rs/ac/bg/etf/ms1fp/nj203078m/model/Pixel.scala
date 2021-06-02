package rs.ac.bg.etf.ms1fp.nj203078m.model

class Pixel(val alpha: Float, val red: Float, val green: Float, val blue: Float) {
  def this() = this(0.0f, 0.0f, 0.0f, 0.0f)

  def isOpaqueBlack: Boolean = alpha >= 1.0f && red >= 1.0f && green >= 1.0f && blue >= 1.0f

  def limit(): Pixel = max(0.0f).min(1.0f)

  def withLayerAlpha(layerAlpha: Float) = new Pixel(alpha * layerAlpha, red, green, blue)

  // ref: https://en.wikipedia.org/wiki/Alpha_compositing
  //
  def over(pixel: Pixel): Pixel = {
    if (alpha <= 0.0f && pixel.alpha <= 0.0f)
      new Pixel(0.0f, 1.0f, 1.0f, 1.0f)
    else {
      val alphaOver = alpha + pixel.alpha * (1.0f - alpha)
      new Pixel(
        alphaOver,
        (red * alpha + pixel.red * pixel.alpha * (1.0f - alpha)) / alphaOver,
        (green * alpha + pixel.green * pixel.alpha * (1.0f - alpha)) / alphaOver,
        (blue * alpha + pixel.blue * pixel.alpha * (1.0f - alpha)) / alphaOver
      )
    }
  }

  def min(value: Float): Pixel = new Pixel(
    alpha,
    Math.min(red, value),
    Math.min(green, value),
    Math.min(blue, value))

  def max(value: Float): Pixel = new Pixel(
    alpha,
    Math.max(red, value),
    Math.max(green, value),
    Math.max(blue, value))
}

object Pixel {
  def fromARGB(argb: Int): Pixel =
    new Pixel(((argb >> 24) & 0xFF).toFloat / 255,
      ((argb >> 16) & 0xFF).toFloat / 255,
      ((argb >> 8) & 0xFF).toFloat / 255,
      ((argb >> 0) & 0xFF).toFloat / 255)

  def toARGB(pixel: Pixel): Int = {
    val limited = pixel.limit()
    ((limited.alpha * 255).ceil.toInt << 24) +
    ((limited.red * 255).ceil.toInt << 16) +
    ((limited.green * 255).ceil.toInt << 8) +
    ((limited.blue * 255).ceil.toInt << 0)
  }
}