package models

/**
  * Created by Lei on 2016/5/10.
  */
trait WithCart {
  implicit def getCart() = {
     1
  }

}
