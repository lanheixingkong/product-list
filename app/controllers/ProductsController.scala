package controllers

import javax.inject.{Inject, Singleton}

import models.Product
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.{I18nSupport, Messages, MessagesApi}
import play.api.mvc.{Action, Controller, Flash}

@Singleton
class ProductsController  @Inject() (val messagesApi: MessagesApi) extends Controller with I18nSupport {

  private val productForm: Form[Product] = Form{
      mapping(
        "ean" -> longNumber.verifying("validation.ean.duplicate", Product.findByEan(_).isEmpty),
        "name" -> nonEmptyText,
        "descr" -> nonEmptyText
      )(Product.apply)(Product.unapply)
  }

  def save = Action { implicit request =>
    val newProductForm = this.productForm.bindFromRequest()

    newProductForm.fold(
        hasErrors = { form =>
          Redirect(routes.ProductsController.newProduct())
            .flashing(Flash(form.data) + ("error" -> Messages("validation.errors")))
        },

        success = { newProduct =>
          Product.add(newProduct)
          val message = Messages("products.new.success", newProduct.name)
          Redirect(routes.ProductsController.show(newProduct.ean)).flashing("success" -> message)
        }
    )
  }

  def newProduct = Action { implicit request =>
    val form = if (request.flash.get("error").isDefined)
      this.productForm.bind(request.flash.data)
    else
      this.productForm

    Ok(views.html.editProduct(form))
  }


  def list(page: Int) = Action { implicit request =>
    println("Page:"+page)
    val products = Product.findAll
    Ok(views.html.products(products))
  }

  def remove(ean: Long) = Action {implicit request =>
    Product.remove(ean)
    Redirect(routes.ProductsController.list())
  }

  def show(ean: Long) = Action { implicit request =>

    Product.findByEan(ean).map{ product =>
      Ok(views.html.productDetails(product))
    }.getOrElse(NotFound)

  }

//  def list =
//    Authenticated {
//      Cached {
//        Action {
//          // Process request…
//        }
//      }
//    }
}
