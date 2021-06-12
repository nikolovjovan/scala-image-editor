package rs.ac.bg.etf.ms1fp.nj203078m.model

import java.awt.Dimension
import scala.annotation.tailrec
import scala.collection.mutable.ArrayBuffer
import scala.swing.Rectangle

class Layer (var name: String, var alpha: Float = 1.0f) extends ElementBase {
  def this() = this("")

  override var id: Int = Layer.getNextId

  var visible: Boolean = true

  var images: ArrayBuffer[Image] = ArrayBuffer.empty
  var output: Image = Image.Empty

  var needsRender: Boolean = false

  def loadImage(fileName: String): Unit = {
    images += new Image(fileName)
    needsRender = true
  }

  def render(): Unit = if (needsRender) {

    @tailrec
    def doRender(x: Int, y: Int, z: Int = images.size - 1): Unit = {
      if (z >= 0 && output(x)(y).alpha < 1.0f) {
        val xrel: Int = output.x + x - images(z).x
        val yrel: Int = output.y + y - images(z).y
        if (xrel >= 0 && xrel < images(z).width &&
            yrel >= 0 && yrel < images(z).height)
          output(x)(y) = output(x)(y) over images(z)(xrel)(yrel)
        doRender(x, y, z - 1)
      }
    }

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
        for (y <- 0 until output.height)
          doRender(x, y)
    }
    needsRender = false
  }

  def removeAllImagesFromSelections(selectionContains: Int => Boolean): Unit = {
    val tmp = images
    images = new ArrayBuffer[Image]()
    for (image <- tmp)
      if (!selectionContains(image.selectionId))
        images.addOne(image)
      else
        needsRender = true
  }

  def execute(selection: Selection, op: Operation, size: Dimension = new Dimension): Unit = {

    def executeOp(rects: Array[Rectangle], in: Image, out: Image, operation: Operation): Image = {
      for (rect <- rects)
        for (x <- rect.x until rect.x + rect.width)
          for (y <- rect.y until rect.y + rect.height)
            if (out(x)(y).isEmpty)
              out(x)(y) = operation.execute(in, x, y)
      out
    }

    def executeSeq(rects: Array[Rectangle], in: Image, out: Image, seq: OperationSeq): Image = {
      if (seq.operations.isEmpty)
        in
      else {
        var input: Image = in
        var output: Image = out
        for (i <- seq.operations.indices) {
          seq.operations(i) match {
            case seq: OperationSeq => output = executeSeq(rects, input, output, seq)
            case op: Operation => output = executeOp(rects, input, output, op)
          }
          if (i < seq.operations.size - 1) {
            val tmp = input
            input = output
            output = tmp
            for (x <- 0 until output.width)
              for (y <- 0 until output.height)
                output(x)(y) = Pixel.Empty
          }
        }
        // Set selection id because in and out may have been swapped!
        //
        output.selectionId = out.selectionId
        output
      }
    }

    val rectCnt: Int = if (selection == Selection.Everything) 1 else selection.rects.size
    val posCorrRects: Array[Rectangle] = new Array[Rectangle](rectCnt)
    if (selection == Selection.Everything)
      posCorrRects(0) = new Rectangle(0, 0, size.width, size.height)
    else
      for (i <- selection.rects.indices) {
        val rect = selection.rects(i)
        posCorrRects(i) = new Rectangle(rect.x - output.x, rect.y - output.y, rect.width, rect.height)
      }
    val out: Image =
      if (selection == Selection.Everything)
        new Image(size.width, size.height, 0, 0, selection.id)
      else
        new Image(output.width, output.height, output.x, output.y, selection.id)
    op match {
      case seq: OperationSeq => images += executeSeq(posCorrRects, output, out, seq)
      case op: Operation => images += executeOp(posCorrRects, output, out, op)
    }
    needsRender = true
  }
}

object Layer {
  var gblLayerId: Int = 0

  def getNextId: Int = {
    gblLayerId += 1
    gblLayerId - 1
  }
}