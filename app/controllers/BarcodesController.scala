package controllers



import javax.inject.{Inject, Singleton}
import play.api.mvc.{Action, Controller}

/**
  * Created by Lei on 2016/4/7.
  */
@Singleton
class BarcodesController @Inject() extends Controller {
  val ImageResolution = 144

  def barcode(ean: Long) = Action {
    import java.lang.IllegalArgumentException

    val MimeType = "image/png"
    try{
      val imageData = ean13BarCode(ean, MimeType)
      Ok(imageData).as(MimeType)
    }catch {
      case e: IllegalArgumentException =>
        BadRequest("Couldn't generate bar code. Error: " + e.getMessage)
    }

  }

  def ean13BarCode(ean: Long, mimeType: String) : Array[Byte] = {
    import java.io.ByteArrayOutputStream
    import java.awt.image.BufferedImage
    import org.krysalis.barcode4j.output.bitmap.BitmapCanvasProvider
    import org.krysalis.barcode4j.impl.upcean.EAN13Bean
    var output: ByteArrayOutputStream = new ByteArrayOutputStream
    var canvas: BitmapCanvasProvider =
      new BitmapCanvasProvider(output, mimeType, ImageResolution,
        BufferedImage.TYPE_BYTE_BINARY, false, 0)

    val barCode = new EAN13Bean()
    barCode.generateBarcode(canvas, String valueOf ean)
    canvas.finish
    output.toByteArray
  }
}
