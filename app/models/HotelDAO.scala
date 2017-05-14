package models


import scala.collection.mutable
import scala.collection.mutable.ListBuffer

/**
 *
 * @author roger1224@gmail.com
 *
 *         $$DateTime$$
 *         $$Change$$
 *         $$Author$$
 */
class HotelDAO {

  /**
   * Store all hotels
   */
  val allHotels: mutable.HashMap[Long, Hotel] = new mutable.HashMap()

  /**
   * Store the mapping of cityId -> Hotel Id
   */
  val cityIndex: mutable.HashMap[String, mutable.TreeSet[Long]] = new mutable.HashMap()


  def addHotel(hotel: Hotel) = {
    if (allHotels.contains(hotel.id)) {
      throw new RuntimeException("Hotel existed. " + hotel.id)
    }

    allHotels.put(hotel.id, hotel)
    val hotelIds: mutable.TreeSet[Long] = cityIndex.getOrElseUpdate(hotel.city, new mutable.TreeSet[Long]())
    hotelIds += hotel.id

    printf("added hotel. (%s)\n", hotel)
  }

  def findByCity(city: String, sortBy: String, sortOrder: String): List[Hotel] = {
    sortBy match {
      case "price" => findByCityOrderByPrice(city, sortOrder)
      case _ => findByCityOrderById(city)
    }
  }

  private def findByCityOrderByPrice(city: String, sortOrder: String): List[Hotel] = {
    sortOrder match {
      case "asc" => findByCity(city, (h1, h2) => h1.price < h2.price)
      case "desc" => findByCity(city, (h1, h2) => h1.price > h2.price)
      case _ => findByCityOrderById(city)
    }
  }

  private def findByCityOrderById(city: String) = {
    findByCity(city, (h1, h2) => h1.id < h2.id)
  }

  private def findByCity(city: String, sortWith: (Hotel, Hotel) => Boolean): List[Hotel] = {
    val hotels: ListBuffer[Hotel] = new ListBuffer[Hotel]()
    val hotelIds: Option[mutable.TreeSet[Long]] = cityIndex.get(city)

    if (hotelIds.isEmpty || sortWith == null) return hotels.toList

    for (hotelId: Long <- hotelIds.get) {
      hotels += allHotels.get(hotelId).get
    }

    hotels.sortWith(sortWith).toList
  }
}
