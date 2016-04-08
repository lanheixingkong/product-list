package models

/**
  * Created by Lei on 2016/4/7.
  */
case class Product (ean: Long, name: String, descr: String )

object Product {
  var products = Set(Product(5010255079763L, "Paperclips Large", "Large Plain Pack of 1000"),
    Product(5018206244666L, "Giant Paperclips",
      "Giant Plain 51mm 100 pack"),
    Product(5018306332812L, "Paperclip Giant Plain",
      "Giant Plain Pack of 10000"),
    Product(5018306312913L, "五年高考，十年模拟",
      "五年高考，十年模拟，地狱训练"),
    Product(5018206244611L, "一只特立独行的猪",
      "说的就是你，不用在怀疑"))

  def findAll = this.products.toList.sortBy(_.ean)

  def findByEan(ean: Long) = this.products.find(_.ean == ean)

  def add(product: Product) {
    this.products = this.products + product
  }

  def remove(ean: Long) {
    this.products = this.products.filter(_.ean != ean)
  }
}
