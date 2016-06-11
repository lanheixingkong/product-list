package controllers

import javax.inject.{Inject, Singleton}

import dao.ProductDao
import models.{Person, Product}
import mytrait.WithCart
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.{I18nSupport, Messages, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller, Flash}


@Singleton
class ProductsController  @Inject() (val messagesApi: MessagesApi, val productDao: ProductDao) extends Controller with I18nSupport with WithCart{
//  implicit val cart = getCart()
//  implicit def cart(): Cart = {
//    Cart(13)
//  }

//  private val productForm: Form[Product] = Form{
//      mapping(
//        "id" -> longNumber,
//        "ean" -> longNumber.verifying("validation.ean.duplicate", productDao.getByEan(_).isEmpty),
//        "name" -> nonEmptyText,
//        "descr" -> nonEmptyText
//      )(Product.apply)(Product.unapply)
//  }

  //同时验证多个字段 product => product.descr.nonEmpty || product.name.nonEmpty
  //错误信息需要在模板中通过globalError获取
  private val productForm: Form[Product] = Form{
    mapping(
      "id" -> longNumber,
      "ean" -> longNumber.verifying("validation.ean.duplicate", productDao.getByEan(_).isEmpty),
      "name" -> text,
      "descr" -> text
    )(Product.apply)(Product.unapply).verifying("Name or Descr is non empty", product => product.descr.nonEmpty || product.name.nonEmpty)
  }

//  private val productForm2: Form[Product] = Form{
//    mapping(
//      "ean" -> longNumber.verifying("validation.ean.duplicate", Product.findByEan(_).isEmpty),
//      "name" -> nonEmptyText,
//      "descr" -> nonEmptyText
//    )((ean, name, descr) => Product(null, ean, name, descr))( product => None)
////    )((ean, name, descr) => Product(null, ean, name, descr))( product => Some[(product.ean, product.name, product.descr)])
//  }

  //Option字段验证,如果传来参数是空,可自动包装成None
  private val personForm: Form[Person] = Form {
    mapping(
      "name" -> nonEmptyText,
      "age" -> optional(number)
    )(Person.apply)(Person.unapply)
  }


  def save = Action { implicit request =>
    val newProductForm = this.productForm.bindFromRequest()

    newProductForm.fold(
        hasErrors = { form =>
          Redirect(routes.ProductsController.newProduct())
            .flashing(Flash(form.data) + ("error" -> Messages("validation.errors")))
        },
//      hasErrors = { form =>
//        Ok(views.html.editProduct(form))
//      },

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

    val html = views.html.editProduct(form)
    println(s"newProduct HTML-->$html")
    Ok(html)
  }


  def list(page: Int) = Action { implicit request =>
    println("Page:"+page)
//    val products = Product.findAll
//    val products = productDao.getAll
//    val products = productDao.getAllWithPatterns
    val products = productDao.getAllWithParser
    val html = views.html.products(products)
//    println(s"HTML-->$html")
    Ok(html).withSession(request.session + ("username" -> "lei"))
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

    productDao.getByEan(ean).map{ product =>
            Ok(views.html.productDetails(product))
          }.getOrElse(NotFound)

  }

//  def show(ean: Long) = Action { implicit request =>
//    val username = request.session.get("username")
//    val sessionID = request.session
//    println("username:" + username.getOrElse("messi") + "--sessionID:" + sessionID)
//
//    Product.findByEan(ean).map{ product =>
//      Ok(views.html.productDetails(product))
//    }.getOrElse(NotFound)
//
//  }


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
    val success = Map("status" -> "success")
    val json = Json.toJson(success)
    Ok(json)
  }


  def xml = Action {
   Ok
  }
}
