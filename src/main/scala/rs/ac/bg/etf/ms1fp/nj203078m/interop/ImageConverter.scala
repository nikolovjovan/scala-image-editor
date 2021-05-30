package rs.ac.bg.etf.ms1fp.nj203078m.interop

import rs.ac.bg.etf.ms1fp.nj203078m.model.Image

import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

object ImageConverter {

  def imgToBufImg(img: Image): BufferedImage = {
    // TODO: Implement conversion
    //
    ImageIO.read(new File("res/sample_png.png"))
  }

  def bufImgToImg(buf: BufferedImage): Image = {
    // TODO: Implement conversion
    //
    new Image
  }

}