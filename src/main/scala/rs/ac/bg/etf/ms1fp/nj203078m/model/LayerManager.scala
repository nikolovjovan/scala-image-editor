package rs.ac.bg.etf.ms1fp.nj203078m.model

import java.util.regex.Pattern
import scala.collection.mutable.ArrayBuffer

class LayerManager {
  var layers: ArrayBuffer[Layer] = ArrayBuffer[Layer](new Layer("Layer 1"))

  def count: Int = layers.size

  def addNewLayer(): Unit = {
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
    layers.insert(0, new Layer("Layer " + nextIndex))
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

  def outputImage: Image = {
    // TODO: "Render" all layers into this image for output via Drawing
    //
    new Image()
  }
}