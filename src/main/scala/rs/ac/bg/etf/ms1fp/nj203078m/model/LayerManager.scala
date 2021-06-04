package rs.ac.bg.etf.ms1fp.nj203078m.model

import rs.ac.bg.etf.ms1fp.nj203078m.interop.ImageConverter

import java.awt.Dimension
import java.awt.image.BufferedImage

class LayerManager extends Manager[Layer]("Layer", name => new Layer(name), true) {
  var output: Image = Image.Empty
  var outputSize: Dimension = new Dimension

  var frameBuffer: BufferedImage = loadNextFrame

  def loadNextFrame: BufferedImage = ImageConverter.imgToBufImg(output)

  def render(): Unit = {
    // TODO: Possibly optimize to render only changes and update final output
    // TODO: DONE ex. 1: layer(s) becomes invisible -> only output needs changing (blending)
    // TODO: DONE ex. 2: layer(s) opacity gets changed -> only output needs changing (blending) because layer output is the same only opacity get recalculated
    // TODO: DONE ex. 3: layer loads image -> only that layer needs rendering and output changing (blending)
    // TODO: ex. 4: selection updates layer(s) -> only those layers + output changes...
    // TODO: this can probably be simplified using Layer class when sublayer gets added to set dirty flag...

    // Render only visible layers -> merge all layer images to one with Z-Buffer.
    //
    for (layer <- elements if layer.visible && layer.alpha > 0)
      layer.render()

    if (count == 1) {
      if (elements(0).visible && elements(0).alpha > 0.0f)
        output = elements(0).output withLayerAlpha elements(0).alpha
      else
        output = Image.Empty
      outputSize.width = elements(0).output.width
      outputSize.height = elements(0).output.height
    } else {
      val sizeRect: Image.Rect = new Image.Rect
      val rect: Image.Rect = new Image.Rect
      for (layer <- elements) {
        // "rect" is responsible for actual output size, this image will be converted into BufferedImage for drawing.
        //
        if (layer.visible && layer.alpha > 0 && layer.output.width > 0 && layer.output.height > 0)
          Image.updateImageRect(layer.output, rect)

        // "sizeRect" is responsible for drawing a white background because image (with all hidden layers) is this size.
        //
        Image.updateImageRect(layer.output, sizeRect)
      }
      if (rect.right > 0 && rect.bottom > 0) {
        output = new Image(rect.width, rect.height, rect.x, rect.y)
        for (x <- 0 until output.width)
          for (y <- 0 until output.height) {
            var z: Int = count - 1
            var break: Boolean = false
            while (z >= 0 && !break) {
              if (elements(z).visible && elements(z).alpha > 0) {
                val xrel: Int = rect.x + x - elements(z).output.x
                val yrel: Int = rect.y + y - elements(z).output.y
                if (xrel >= 0 && xrel < elements(z).output.width &&
                    yrel >= 0 && yrel < elements(z).output.height) {
                  output.pixels(x)(y) =
                    elements(z).output.pixels(xrel)(yrel) withLayerAlpha elements(z).alpha over output.pixels(x)(y)
                  break = output.pixels(x)(y).isOpaqueBlack
                }
              }
              z -= 1
            }
          }
      } else output = Image.Empty
      outputSize.width = if (sizeRect.right > 0) sizeRect.right else 0
      outputSize.height = if (sizeRect.bottom > 0) sizeRect.bottom else 0
    }

    // Convert output image to BufferedImage for painting.
    //
    frameBuffer = loadNextFrame
  }

  def addNewLayer(position: Int = 0): Unit = super.addNewElement(position)
  def removeLayers(selectionContains: Int => Boolean): Unit = super.removeElements(selectionContains)
}