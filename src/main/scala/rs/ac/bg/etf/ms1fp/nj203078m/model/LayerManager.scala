package rs.ac.bg.etf.ms1fp.nj203078m.model

import rs.ac.bg.etf.ms1fp.nj203078m.interop.ImageConverter

import java.awt.image.BufferedImage
import java.util.regex.Pattern
import scala.collection.mutable.ArrayBuffer

class LayerManager {
  var layers: ArrayBuffer[Layer] = ArrayBuffer[Layer](new Layer("Layer 1"))

  var output: Image = Image.Empty

  var frameBuffer: BufferedImage = loadNextFrame

  def loadNextFrame: BufferedImage = ImageConverter.imgToBufImg(output)

  def count: Int = layers.size

  def render(): Unit = {
    // TODO: Possibly optimize to render only changes and update final output
    // TODO: ex. 1: layer(s) becomes invisible -> only output needs changing (blending)
    // TODO: ex. 2: layer(s) opacity gets changed -> only output needs changing (blending) because layer output is the same only opacity get recalculated
    // TODO: ex. 3: layer loads image -> only that layer needs rendering and output changing (blending)
    // TODO: ex. 4: selection updates layer(s) -> only those layers + output changes...
    // TODO: this can probably be simplified using Layer class when sublayer gets added to set dirty flag...

    // Render only visible layers -> merge all layer images to one with Z-Buffer
    //
    for (layer <- layers if layer.visible && layer.alpha > 0)
      layer.render()

    if (layers.size == 1) {
      output = layers(0).output
    } else {
      val rect: Image.Rect = new Image.Rect
      for (layer <- layers if layer.visible && layer.alpha > 0 && layer.output.width > 0 && layer.output.height > 0)
        Image.updateImageRect(layer.output, rect)
      output = new Image(rect.width, rect.height, rect.x, rect.y)
      for (x <- 0 until output.width)
        for (y <- 0 until output.height) {
          var z: Int = layers.size - 1
          var break: Boolean = false
          while (z >= 0 && !break) {
            if (layers(z).visible && layers(z).alpha > 0) {
              val xrel: Int = rect.x + x - layers(z).output.x
              val yrel: Int = rect.y + y - layers(z).output.y
              if (xrel >= 0 && xrel < layers(z).output.width &&
                  yrel >= 0 && yrel < layers(z).output.height) {
                output.pixels(x)(y) =
                  layers(z).output.pixels(xrel)(yrel) withLayerAlpha layers(z).alpha over output.pixels(x)(y)
                if (output.pixels(x)(y).isOpaqueBlack) {
                  break = true
                }
              }
            }
            z -= 1
          }
        }
    }

    // Convert output image to BufferedImage for painting
    //
    frameBuffer = loadNextFrame
  }

  def addNewLayer(position: Int = 0): Unit = {
    val pattern = Pattern.compile("^Layer (\\d+)$")
    var nextIndex = 1
    for (layer <- layers) {
      val matcher = pattern.matcher(layer.name)
      if (matcher.matches()) {
        val layerIdOpt = matcher.group(1).toIntOption
        val layerId = if (layerIdOpt.isDefined) layerIdOpt.get else 0
        if (layerId >= nextIndex)
          nextIndex = layerId + 1
      }
    }
    layers.insert(position, new Layer("Layer " + nextIndex))
  }

  def removeLayers(selectionContains: Int => Boolean): Unit = {
    val tmp = layers
    layers = new ArrayBuffer[Layer]
    for (i <- tmp.indices)
      if (!selectionContains(i))
        layers += tmp(i)
    if (layers.isEmpty)
      layers += new Layer("Layer 1")
  }
}