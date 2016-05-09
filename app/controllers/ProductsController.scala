package controllers

import javax.inject.{Inject, Singleton}

import models.{Product, ProductDao}
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.{I18nSupport, Messages, MessagesApi}
import play.api.mvc.{Action, Controller, Flash}

@Singleton
class ProductsController  @Inject() (val messagesApi: MessagesApi, val productDao: ProductDao) extends Controller with I18nSupport {

  private val productForm: Form[Product] = Form{
      mapping(
        "id" -> longNumber,
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
//          Product.add(newProduct)
          productDao.insert(newProduct)
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
//    val products = Product.findAll
//    val products = productDao.getAll
//    val products = productDao.getAllWithPatterns
    val products = productDao.getAllWithParser

    Ok(views.html.products(products)).withSession(request.session + ("username" -> "lei"))
  }

  def listWithStockItem(page: Int) = Action { implicit request =>
    val productMap = productDao.getAllProductWithStockItem

    Ok(views.html.productsWithStockItem(productMap)).withSession(request.session + ("username" -> "lei"))
  }

  def delete(id: Long) = Action {implicit request =>
    productDao.delete(id)
    Redirect(routes.ProductsController.list()).flashing(("success" -> "Product deleted!!!"))
  }
//  def delete(ean: Long) = Action {implicit request =>
//    Product.delete(ean)
//    Redirect(routes.ProductsController.list()).flashing(("success" -> "Product deleted!!!"))
//  }

  def show(ean: Long) = Action { implicit request =>
    val username = request.session.get("username")
    val sessionID = request.session
    println("username:" + username.getOrElse("messi") + "--sessionID:" + sessionID)

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

//  返回JSON格式的两种方式：1.转成Json字符串，直接返回(会设置请求头Content-Type:application/json) 2.使用模板返回（类似Html页面）
  def json = Action {
    import play.api.libs.json.Json
    val success = Map("status" -> "success")
    val json = Json.toJson(success)
    Ok(json)
  }


  def xml = Action {
    import scala.xml.NodeSeq
   Ok
  }
}
