package rs.ac.bg.etf.ms1fp.nj203078m.gui

import rs.ac.bg.etf.ms1fp.nj203078m.model.{Layer, Rect, Selection}
import rs.ac.bg.etf.ms1fp.nj203078m.model.manager.{FunctionManager, LayerManager, OperationSeqManager, SelectionManager}

import java.awt.{AlphaComposite, Color, Composite}
import java.awt.image.BufferedImage
import scala.swing.event.{MouseDragged, MousePressed, MouseReleased}
import scala.swing.{Graphics2D, Point, Rectangle}

class Drawing extends ImagePreview(new LayerManager) {
  foreground = Color.WHITE
  background = Color.LIGHT_GRAY

  var selectionManager: SelectionManager = new SelectionManager(render, layerManager)
  var functionManager: FunctionManager = new FunctionManager
  var operationSeqManager: OperationSeqManager = new OperationSeqManager

  var clickedLayer: Option[Layer] = None
  var clickPoint: Point = new Point
  var moveRect: Rectangle = new Rectangle
  var dragRect: Rectangle = new Rectangle

  var highlightedSelection: Option[Selection] = None
  var highlightedSelectionImage: Option[BufferedImage] = None
  var editingSelection: Boolean = false
  var selectionRect: Option[Rect] = None

  def highlightSelection(selection: Selection): Unit = {
    highlightedSelection = Some(selection)
    highlightedSelectionImage =
      if (layerManager.outputSize.width > 0 && layerManager.outputSize.height > 0)
        Some(new BufferedImage(layerManager.outputSize.width, layerManager.outputSize.height, BufferedImage.TYPE_INT_ARGB))
      else {
        val viewSize = getViewSize
        preferredSize = viewSize
        Some(new BufferedImage(viewSize.width, viewSize.height, BufferedImage.TYPE_INT_ARGB))
      }
    val g: Graphics2D = highlightedSelectionImage.get.createGraphics()
    g.setColor(Color.BLUE)
    for (rect <- selection.rects)
      g.fillRect(rect.x, rect.y, rect.width, rect.height)
    g.dispose()
    repaint()
  }

  def stopHighlightingSelection(render: Boolean = true): Unit = {
    highlightedSelection = None
    highlightedSelectionImage = None
    if (render)
      repaint()
  }

  def editSelection(selection: Selection): Unit = {
    highlightSelection(selection)
    editingSelection = true
    selectionRect = None
  }

  def stopEditingSelection(): Unit = {
    stopHighlightingSelection(false)
    editingSelection = false
    selectionRect = None
    repaint()
  }

  listenTo(mouse.clicks, mouse.moves)
  reactions += {
    case e: MousePressed => if (e.source == this) {
      if (editingSelection) {
        selectionRect = Some(new Rect(e.point))
      } else {
        clickedLayer = layerManager.getLayerAt(e.point)
        if (clickedLayer.isDefined) {
          clickPoint = e.point
          moveRect = new Rectangle(
            clickedLayer.get.x,
            clickedLayer.get.y,
            clickedLayer.get.output.width,
            clickedLayer.get.output.height)
          repaint()
        } else {
          stopHighlightingSelection()
        }
      }
    }
    case e: MouseDragged => if (e.source == this) {
      if (editingSelection) {
        selectionRect.get.right = e.point.x
        selectionRect.get.bottom = e.point.y
        repaint()
      } else if (clickedLayer.isDefined) {
        moveRect.x = clickedLayer.get.x + e.point.x - clickPoint.x
        moveRect.y = clickedLayer.get.y + e.point.y - clickPoint.y
        repaint()
      }
    }
    case e: MouseReleased => if (e.source == this) {
      if (editingSelection) {
        selectionRect.get.right = e.point.x
        selectionRect.get.bottom = e.point.y
        val fixedRect = new Rect(
          (selectionRect.get.left min selectionRect.get.right) - 1,
          (selectionRect.get.top min selectionRect.get.bottom) - 1,
          selectionRect.get.width.abs,
          selectionRect.get.height.abs)
//        if (layerManager.outputSize.width > 0 && layerManager.outputSize.height > 0) {
//          if (fixedRect.right > layerManager.outputSize.width)
//            fixedRect.right = layerManager.outputSize.width
//          if (fixedRect.bottom > layerManager.outputSize.height)
//            fixedRect.bottom = layerManager.outputSize.height
//        }
        highlightedSelection.get.addRect(fixedRect)
        val g: Graphics2D = highlightedSelectionImage.get.createGraphics()
        g.setColor(Color.BLUE)
        g.fillRect(fixedRect.x, fixedRect.y, fixedRect.width, fixedRect.height)
        g.dispose()
        selectionRect = None
        Main.editingDialog.get.updateTable()
        render()
      } else if (clickedLayer.isDefined) {
        clickedLayer.get.x += e.point.x - clickPoint.x
        clickedLayer.get.y += e.point.y - clickPoint.y
        clickedLayer = None
        render()
      }
    }
  }

  override def paintComponent(g: Graphics2D): Unit = {
    super.paintComponent(g)
    if (clickedLayer.isDefined) {
      g.setColor(Color.RED)
      g.drawRect(moveRect.x, moveRect.y, moveRect.width, moveRect.height)
    }
    if (highlightedSelectionImage.isDefined) {
      g.setColor(Color.BLUE)
      val originalComposite: Composite = g.getComposite
      g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f))
      g.drawImage(highlightedSelectionImage.get, 1, 1, null)
      g.setComposite(originalComposite)
    }
    if (selectionRect.isDefined) {
      g.setColor(Color.BLUE)
//      val fixedRect = new Rect(
//        selectionRect.get.left min selectionRect.get.right,
//        selectionRect.get.top min selectionRect.get.bottom,
//        selectionRect.get.width.abs,
//        selectionRect.get.height.abs)
//      if (layerManager.outputSize.width > 0 && layerManager.outputSize.height > 0) {
//        if (fixedRect.right > layerManager.outputSize.width + 1)
//          fixedRect.right = layerManager.outputSize.width + 1
//        if (fixedRect.bottom > layerManager.outputSize.height + 1)
//            fixedRect.bottom = layerManager.outputSize.height + 1
//      }
//      g.fillRect(fixedRect.x, fixedRect.y, fixedRect.width, fixedRect.height)
      g.fillRect(
        selectionRect.get.left min selectionRect.get.right,
        selectionRect.get.top min selectionRect.get.bottom,
        selectionRect.get.width.abs,
        selectionRect.get.height.abs)
    }
  }
}