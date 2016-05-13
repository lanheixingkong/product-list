package mytrait

import models.Cart


/**
  * Created by Lei on 2016/5/13.
  */
trait WithCart {
    implicit def getCart(): Cart = {
      Cart(18)
    }

    implicit val cart = getCart()
}
