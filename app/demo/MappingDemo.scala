package demo

import play.api.data.{Form, Forms}

/**
  * Created by Lei on 2016/5/13.
  */
class MappingDemo {
//id: Long, ean: Long, name: String, descr: String
  val data = Map("id" -> "1234546754", "ean" -> "123434289", "name" -> "Big Boss", "descr" -> "Big Boss is dead...")

  val mapping = Forms.tuple("id" -> Forms.longNumber, "ean" -> Forms.longNumber, "name" -> Forms.text, "descr" -> Forms.text)

  val dataForm = Form(mapping)

  val processedForm = dataForm.bind(data)

  if(!processedForm.hasErrors) {
    val productTuple = processedForm.get
    println(s"prodcutTuple--> $productTuple")
  }else{
    val errors = processedForm.errors
    println(s"errors--> $errors")
  }
}
