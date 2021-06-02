package rs.ac.bg.etf.ms1fp.nj203078m.model

import scala.collection.mutable.ArrayBuffer

class Layer (var name: String, var alpha: Float = 1.0f) {

  var visible: Boolean = true

  var images: ArrayBuffer[Image] = ArrayBuffer.empty
  var output: Image = Image.Empty

  var needsRender: Boolean = false

  def loadImage(fileName: String): Unit = {
    images += new Image(fileName)
    needsRender = true
  }

  def render(): Unit = if (needsRender) {
    if (images.isEmpty) {
      output = Image.Empty
    } else if (images.size == 1) {
      output = images(0)
    } else {
      val rect: Image.Rect = new Image.Rect
      for (image <- images)
        Image.updateImageRect(image, rect)
      output = new Image(rect.width, rect.height, rect.x, rect.y)
      for (x <- 0 until output.width)
        for (y <- 0 until output.height) {
          var z: Int = 0
          var break: Boolean = false
          while (z < images.size && !break) {
            val xrel: Int = rect.x + x - images(z).x
            val yrel: Int = rect.y + y - images(z).y
            if (xrel >= 0 && xrel < images(z).width &&
                yrel >= 0 && yrel < images(z).height) {
              output.pixels(x)(y) = images(z).pixels(xrel)(yrel) over output.pixels(x)(y)
              if (output.pixels(x)(y).isOpaqueBlack) {
                break = true
              }
            }
            z += 1
          }
        }
    }
    needsRender = false
  }
}