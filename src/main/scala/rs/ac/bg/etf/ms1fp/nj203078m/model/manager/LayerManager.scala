package rs.ac.bg.etf.ms1fp.nj203078m.model.manager

import rs.ac.bg.etf.ms1fp.nj203078m.model.{Image, Layer, Rect}

import scala.annotation.tailrec
import scala.collection.mutable.ArrayBuffer
import scala.swing.{Dimension, Point}

class LayerManager extends Manager[Layer]("Layer", name => new Layer(name), true) {
  var output: Image = Image.Empty
  var outputSize: Dimension = new Dimension

  var isLayerVisible: Int => Boolean = z => elements(z).visible
  var getLayerAlpha: Int => Float = z => elements(z).alpha

  def getLayerAt(point: Point): Option[Layer] = {
    class BreakLoop extends Exception

    if (output == Image.Empty)
      None
    else {
      var result: Option[Layer] = None
      try {
        for (z <- elements.indices if isLayerVisible(z) && getLayerAlpha(z) > 0.0f) {
          val layer: Layer = elements(z)
          val xrel: Int = point.x - layer.x
          val yrel: Int = point.y - layer.y
          if (xrel >= 0 && xrel < layer.width &&
              yrel >= 0 && yrel < layer.height &&
              layer.output(xrel)(yrel).alpha > 0.0f) {
            result = Some(layer)
            // Break for loop
            //
            throw new BreakLoop
          }
        }
      } catch {
        case _: BreakLoop =>
      }
      result
    }
  }

  def render(): Image = {

    @tailrec
    def doRender(x: Int, y: Int, z: Int = 0): Unit = {
      if (z < count && output(x)(y).alpha < 1.0f) {
        if (isLayerVisible(z) && getLayerAlpha(z) > 0) {
          val xrel: Int = output.x + x - elements(z).x
          val yrel: Int = output.y + y - elements(z).y
          if (xrel >= 0 && xrel < elements(z).width &&
              yrel >= 0 && yrel < elements(z).height)
            output(x)(y) = output(x)(y) over (elements(z).output(xrel)(yrel) withLayerAlpha getLayerAlpha(z))
        }
        doRender(x, y, z + 1)
      }
    }

    // Render only visible layers -> merge all layer images to one with Z-Buffer.
    //
    for (z <- elements.indices if isLayerVisible(z) && getLayerAlpha(z) > 0.0f)
      elements(z).render()

    if (count == 1) {
      if (isLayerVisible(0) && getLayerAlpha(0) > 0.0f)
        output = elements(0).output withLayerAlpha getLayerAlpha(0)
      else
        output = Image.Empty
      outputSize.width = elements(0).x + elements(0).width
      outputSize.height = elements(0).y + elements(0).height
    } else {
      val sizeRect: Rect = new Rect
      val rect: Rect = new Rect
      for (z <- elements.indices) {
        // "rect" is responsible for actual output size, this image will be converted into BufferedImage for drawing.
        //
        if (isLayerVisible(z) && getLayerAlpha(z) > 0 && elements(z).output.width > 0 && elements(z).output.height > 0)
          Image.updateImageRect(elements(z).output, rect)

        // "sizeRect" is responsible for drawing a white background because image (with all hidden layers) is this size.
        //
        Image.updateImageRect(elements(z).output, sizeRect)
      }
      if (rect.right > 0 && rect.bottom > 0) {
        output = new Image(rect.width, rect.height, rect.x, rect.y)
        for (x <- 0 until output.width)
          for (y <- 0 until output.height)
            doRender(x, y)
      } else output = Image.Empty
      outputSize.width = if (sizeRect.right > 0) sizeRect.right else 0
      outputSize.height = if (sizeRect.bottom > 0) sizeRect.bottom else 0
    }

    output
  }

  def layers: ArrayBuffer[Layer] = elements
  
  def duplicateLayer(position: Int): Unit = elements.insert(position, new Layer(elements(position)))
  def addLayer(position: Int = 0): Unit = super.addElement(position)
  def removeLayers(selectionContains: Int => Boolean): Unit = super.removeElements(selectionContains)
}