import models.Product
import org.joda.time.LocalDate
import play.api.data.format.Formatter
import play.api.data.{FormError, Form, Forms}
import scala.util.control.Exception


/**
  * Created by Lei on 16/5/17.
  */
object MappingScript {
  val data = Map("id" -> "1234546754d", "ean" -> "123434289", "name" -> "Big Boss", "descr" -> "Big Boss is dead...")

  val tupleMapping = Forms.tuple("id" -> Forms.longNumber, "ean" -> Forms.longNumber, "name" -> Forms.text, "descr" -> Forms.text)
  val productMapping = Forms.mapping("id" -> Forms.longNumber, "ean" -> Forms.longNumber, "name" -> Forms.text, "descr" -> Forms.text)(Product.apply)(Product.unapply)

  val dataForm = Form(tupleMapping)
  val productForm = Form(productMapping)


  val processedForm = dataForm.bind(data)


  def test(): Unit = {
    if (!processedForm.hasErrors) {
      val productTuple = processedForm.get
      println(s"prodcutTuple--> $productTuple")
    } else {
      val errors = processedForm.errors
      println(s"errors--> $errors")
    }
  }

  def test2(): Unit = {
    processedForm.fold(
      hasErrors = {
        form =>
          val data = form.data
          val errors = form.errors
          println(s"Data-->$data")
          println(s"Errors-->$errors")
      },
      success = {
        productTuple => println(productTuple)
      }
    )
  }

  def test3(): Unit = {
    processedForm.bind(data).fold(
      hasErrors = { form =>
        val data = form.data
        val errors = form.errors
        println(s"Data-->$data")
        println(s"Errors-->$errors")
      },

      success = {
        product => println(s"Proudct-->$product")
      }
    )
  }


  //自定义mapping两种方式:
  // 1.将一个现有的mapping通过函数进行转换,需要实现两个函数将两种类型相互转换
  //如果格式错误不能转换,不能自定义错误信息,将会直接抛出一个异常
  val localDateMapping = Forms.text.transform((dateString: String) => LocalDate.parse(dateString), (localDate: LocalDate) => localDate.toString)
  //2.现实一个自定义mapping
  implicit val localDateFormatter = new Formatter[LocalDate] {
    def bind(key: String, data: Map[String, String]) = {
      data.get(key).toRight{
        Seq(FormError(key, "error.required", Nil))
      }.right.flatMap{
        string => Exception.allCatch[LocalDate].either(LocalDate.parse(string)).left.map{ exception =>
          Seq(FormError(key, "Date formatted as YYYY-MM-DD expected", Nil))
        }
      }
    }
    def unbind(key:String, ld: LocalDate) = Map(key -> ld.toString)
    override val format = Some(("date.format=Date(YYYY-MM-DD)", Nil))
  }

  val localDateMapping2 = Forms.of[LocalDate]
  val d = Map("startdate" -> "2015-09-11", "enddate" -> "2016-11-09")

  val dMapping = Forms.tuple("startdate" -> localDateMapping, "enddate" -> Forms.of(localDateFormatter))

  def test4(): Unit = {
    Form(dMapping).bind(d).fold(
      hasErrors = { form =>
        val data = form.data
        val errors = form.errors
        println(s"Data-->$data")
        println(s"Errors-->$errors")
      },

      success = {
        dtuple => println(s"Tuple-->$dtuple")
      }
    )
  }
}


object runnable extends App {
  //  MappingScript.test()
  //  MappingScript.test2()
//  MappingScript.test3()
  MappingScript.test4()
}

