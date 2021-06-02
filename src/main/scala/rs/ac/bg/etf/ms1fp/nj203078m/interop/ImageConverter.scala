package rs.ac.bg.etf.ms1fp.nj203078m.interop

import rs.ac.bg.etf.ms1fp.nj203078m.model.{Image, Pixel}

import java.awt.image.BufferedImage

object ImageConverter {
  def imgToBufImg(img: Image): BufferedImage = {
    val width = if (img.width > 0) img.width else 1
    val height = if (img.height > 0) img.height else 1
    val buf: BufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
    if (img.width > 0 && img.height > 0)
      for (x <- 0 until width)
        for (y <- 0 until height)
          buf.setRGB(x, y, Pixel.toARGB(img.pixels(x)(y)))
    buf
  }

  def bufImgToPixelMatrix(buf: BufferedImage): Image.PixelMatrix =
    Array.tabulate(buf.getWidth, buf.getHeight)((x, y) => Pixel.fromARGB(buf.getRGB(x, y)))
}