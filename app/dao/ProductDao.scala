package dao

import javax.inject.{Inject, Singleton}

import anorm.{RowParser, _}
import models.{Product, StockItem}
import play.api.db.Database

/**
  * Created by Lei on 2016/5/10.
  */
@Singleton
class ProductDao @Inject()(db: Database){
  val sql: SqlQuery = SQL("select * from products order by name")

  def getAll: List[Product] = db.withConnection {
    implicit connection =>
      sql().map( row =>
        Product(row[Long]("id"), row[Long]("ean"), row[String]("name"), row[String]("descr"))).toList
  }

  def getAllWithPatterns: List[Product] = db.withConnection {
    implicit connection =>
      import anorm.Row
      sql().collect {
        case Row(Some(id: Long), Some(ean: Long), Some(name: String), Some(descr: String)) =>
          Product(id, ean, name, descr)
        case _ => Product(123, 122, "lalalal", "kjkjkjkj")
      }.toList
  }

  def getAllWithParser: List[Product] = db.withConnection {
    implicit connetion => sql.as(productsParser)
  }

  //  def findById(id: Long): List[Product] = db.withConnection {
  //    implicit connetion =>
  //      SQL("select *, s.* from products where id = {id}").on("id" -> id).executeQuery().
  //  }

  val productParser: RowParser[Product] = {
    import anorm.~
    import anorm.SqlParser._

    long("id") ~  long("ean") ~ str("name") ~ str("descr") map{
      case id ~ ean ~ name ~ descr => Product(id, ean, name, descr)
    }

  }

  val stockItemParser: RowParser[StockItem] = {
    import anorm.~
    import anorm.SqlParser._

    long("id") ~  long("product_id") ~ long("warehouse_id") ~ long("quantity") map{
      case id ~ productId ~ warehouseId ~ quantity => StockItem(id, productId, warehouseId, quantity)
    }

  }

  import anorm.ResultSetParser

  val productsParser: ResultSetParser[List[Product]] = { productParser *}

  def productStockItemParser: RowParser[(Product, StockItem)] = {
    import anorm.SqlParser._
    productParser ~ stockItemParser map flatten
  }

  def getAllProductWithStockItem: Map[Product, List[StockItem]] = db.withConnection {
    implicit connetion =>
      val sql = SQL("select p.*, s.* from products p inner join stock_items s on p.id = s.product_id")
      val results: List[(Product, StockItem)] = sql.as(productStockItemParser *)
      results.groupBy(_._1).mapValues(_.map(_._2))
  }

  def insert(product: Product):Boolean = db.withConnection{
    implicit connection =>
      SQL("""insert into products(ean, name, descr) values ({ean}, {name}, {descr})""").on(
        "ean" -> product.ean,
        "name" -> product.name,
        "descr" -> product.descr
      ).executeUpdate() == 1
  }

  def update(product: Product):Boolean = db.withConnection{
    implicit connection =>
      SQL("""update products set ean = {ean},name = {name},descr = {descr} where id = {id}""").on(
        "id" -> product.id,
        "ean" -> product.ean,
        "name" -> product.name,
        "descr" -> product.descr
      ).executeUpdate() == 1
  }

  def delete(id: Long):Boolean = db.withConnection{
    implicit connection =>
      SQL("""delete from products where id = {id}""").on(
        "id" -> id
      ).executeUpdate() == 1
  }
}
