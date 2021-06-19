package rs.ac.bg.etf.ms1fp.nj203078m.model

import rs.ac.bg.etf.ms1fp.nj203078m.model.operation.{Operation, OperationSeq}
import rs.ac.bg.etf.ms1fp.nj203078m.model.traits.ElementBase

import scala.annotation.tailrec
import scala.collection.mutable.ArrayBuffer
import scala.swing.Dimension

class Layer (var name: String, var alpha: Float = 1.0f) extends ElementBase {
  def this() = this("")

  def this(that: Layer) {
    this(that.name, that.alpha)
    visible = that.visible
    images = ArrayBuffer.tabulate(that.images.size)(i => new Image(that.images(i)))
    output = that.output
  }

  override var id: Int = Layer.getNextId

  var visible: Boolean = true

  def x: Int = output.x
  def x_=(x: Int): Unit = output.x = x

  def y: Int = output.y
  def y_=(y: Int): Unit = output.y = y

  def width: Int = output.width
  def height: Int = output.height

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
        if (images(z).selectionId == -1)
          doRender(x, y, z - 1)
      }
    }

    if (images.isEmpty) {
      output = Image.Empty
    } else if (images.size == 1) {
      output = images(0)
    } else {
      val rect: Rect = new Rect
      for (image <- images)
        Image.updateImageRect(image, rect)
      output = Image.withSize(rect)
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

    def getMaxN(operation: Operation, maxN: Int = 0): Int = {
      var N: Int = maxN
      operation match {
        case seq: OperationSeq => for (op <- seq.operations) N = N max getMaxN(op, N)
        case op: Operation => N = N max op.N
      }
      N
    }

    def executeOp(rects: ArrayBuffer[Rect], in: Image, out: Image, operation: Operation): Image = {
      for (rect <- rects)
        for (x <- rect.x until rect.x + rect.width)
          for (y <- rect.y until rect.y + rect.height)
            if (out(x - out.x)(y - out.y).isEmpty)
              out(x - out.x)(y - out.y) = operation.execute(in, x - in.x, y - in.y)
      out
    }

    def executeSeq(rects: ArrayBuffer[Rect], in: Image, out: Image, seq: OperationSeq): Image = {
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
            // We can swap input and output images if they are the same size!
            //
            if (input.x == output.x &&
                input.y == output.y &&
                input.width == output.width &&
                input.height == output.height) {
              val tmp = input
              input = output
              output = tmp
              for (x <- 0 until output.width)
                for (y <- 0 until output.height)
                  output(x)(y) = Pixel.Empty
            } else {
              // Output of current operation becomes input of the following.
              //
              input = output
              // We need to create a new output image with the same size as previous output.
              //
              output = new Image(output.width, output.height, output.x, output.y)
            }
          }
        }
        // Set selection id because in and out may have been swapped!
        //
        output.selectionId = out.selectionId
        output
      }
    }

    val imageRect: Rect = new Rect(size)
    var rects: ArrayBuffer[Rect] = selection.rects

    if (selection == Selection.Everything)
      rects = new ArrayBuffer[Rect]().addOne(imageRect)
    else {
      val tmpRect: Rect = new Rect

      for (rect <- selection.rects) {
        if (rect.x < tmpRect.left)
          tmpRect.left = rect.x
        if (rect.y < tmpRect.top)
          tmpRect.top = rect.y
        if (rect.x + rect.width > tmpRect.right)
          tmpRect.right = rect.x + rect.width
        if (rect.y + rect.height > tmpRect.bottom)
          tmpRect.bottom = rect.y + rect.height
      }

      imageRect.x = tmpRect.left
      imageRect.y = tmpRect.top
      imageRect.width = tmpRect.width
      imageRect.height = tmpRect.height
    }

    val maxN: Int = getMaxN(op)

    if (output.x - maxN < imageRect.left)
      imageRect.left = output.x - maxN
    if (output.x + output.width + maxN > imageRect.right)
      imageRect.right = output.x + output.width + maxN
    if (output.y - maxN < imageRect.top)
      imageRect.top = output.y - maxN
    if (output.y + output.height + maxN > imageRect.bottom)
      imageRect.bottom = output.y + output.height + maxN

    // Try to reuse previous sublayer if location and size matches!
    //
    if (images.last.selectionId == selection.id &&
        images.last.x == imageRect.x &&
        images.last.y == imageRect.y &&
        images.last.width == imageRect.width &&
        images.last.height == imageRect.height) {
      for (x <- 0 until images.last.width)
        for (y <- 0 until images.last.height)
          images.last(x)(y) = Pixel.Empty
      op match {
        case seq: OperationSeq => executeSeq(rects, output, images.last, seq)
        case op: Operation => executeOp(rects, output, images.last, op)
      }
    } else {
      val out: Image = new Image(imageRect.width, imageRect.height, imageRect.x, imageRect.y, selection.id)
      op match {
        case seq: OperationSeq => images += executeSeq(rects, output, out, seq)
        case op: Operation => images += executeOp(rects, output, out, op)
      }
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