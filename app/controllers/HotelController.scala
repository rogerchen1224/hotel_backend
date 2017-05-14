package controllers

import controllers.Application._
import models.{Hotel, HotelDAO}
import play.api.Play
import play.api.libs.json.{Json, Writes}


/**
 *
 * @author roger1224@gmail.com
 *
 *         $$DateTime$$
 *         $$Change$$
 *         $$Author$$
 */
object HotelController {

  val hotelDao: HotelDAO = new HotelDAO()

  loadFromFile

  implicit val hotelWrites = new Writes[Hotel] {
    def writes(hotel: Hotel) = Json.obj(
      "id" -> hotel.id,
      "city" -> hotel.city,
      "room" -> hotel.room,
      "price" -> hotel.price
    )
  }

  private def loadFromFile = {
    val lines = scala.io.Source.fromFile(Play.current.getFile("conf/hoteldb.csv")).getLines

    for (line <- lines) {
      val fields: Array[String] = line.split(",")
      if (fields.length >= 4 && fields(1) != "HOTELID")
        hotelDao.addHotel(new Hotel(fields(1).toLong, fields(0), fields(2), fields(3).toDouble))
    }

    println("Init HotelDB.")
  }

  /**
   * Find hotels by cityId
   * URI: hotel/[cityId]?sortBy=price&sortOrder=asc
   *
   * @param cityId
   */
  def findByCityId(cityId: String) = RateLimitedAction { implicit request =>
    val sortBy: Option[String] = request.getQueryString("sortBy")
    val sortOrder: Option[String] = request.getQueryString("sortOrder")

    val hotels: List[Hotel] = hotelDao.findByCity(cityId, sortBy.getOrElse(""), sortOrder.getOrElse(""))
    Ok(Json.toJson(hotels))
  }

}
