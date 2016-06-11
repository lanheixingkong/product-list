package controllers

import java.io.File
import javax.inject._

import play.api.data.Form
import play.api.mvc.{Action, Controller}
import views.html.helper.form


/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class FileUploadController @Inject() extends Controller {


  def upload() = Action(parse.multipartFormData) { implicit request
    => request.body.file("image").map{
      file =>
        val filename = file.filename
        file.ref.moveTo(new File(s"/Users/Lei/Pictures/temp/$filename"))
        Ok("Retrieved file %s" format filename)
  }.getOrElse(BadRequest("File missing!"))
  }

//  def ignoredUpload() = Action(parse.multipartFormData) { implicit request =>
//    val form = Form(tuple(
//      ￼￼    "description" -> text,
//    "image" -> ignored(request.body.file("image")).
//      verifying("File missing", _.isDefined)))
//    form.bindFromRequest.fold(
//      formWithErrors => {
//        Ok(views.html.fileupload.uploadform(formWithErrors))
//      },
//      value => Ok }

  def toUpload = Action {
    Ok(views.html.upload())
  }

}
