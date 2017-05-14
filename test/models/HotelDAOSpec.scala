package models

import org.scalatestplus.play.PlaySpec

/**
  *
  * @author roger1224@gmail.com
  *
  *         $$DateTime$$
  *         $$Change$$
  *         $$Author$$
  */
class HotelDAOSpec extends PlaySpec {
   "A HotelDAO" should {
     "be able to add hotels" in {
       val hotelDAO: HotelDAO = new HotelDAO()

       assert(hotelDAO.allHotels.size == 0)
       assert(hotelDAO.cityIndex.size == 0)

       hotelDAO.addHotel(new Hotel(1, "Taipei", "big", 1000))
       hotelDAO.addHotel(new Hotel(2, "Bangkok", "big", 2000))
       hotelDAO.addHotel(new Hotel(3, "Taipei", "small", 500))

       assert(hotelDAO.allHotels.size == 3)
       assert(hotelDAO.cityIndex.size == 2)
       assert(hotelDAO.cityIndex.get("Taipei").get.size == 2)
       assert(hotelDAO.cityIndex.get("Taipei").get.contains(1))
       assert(hotelDAO.cityIndex.get("Taipei").get.contains(3))

       assert(hotelDAO.cityIndex.get("Bangkok").get.size == 1)
       assert(hotelDAO.cityIndex.get("Bangkok").get.contains(2))
     }

     "find hotels by cityId" in {
       val hotelDAO: HotelDAO = new HotelDAO()

       hotelDAO.addHotel(new Hotel(1, "Taipei", "big", 1000))
       hotelDAO.addHotel(new Hotel(2, "Bangkok", "big", 2000))
       hotelDAO.addHotel(new Hotel(3, "Taipei", "small", 500))

       val hotels: List[Hotel] = hotelDAO.findByCity("Taipei", null, null)

       assert(hotels.size == 2)
       assert(hotels(0).id == 1)
       assert(hotels(0).price == 1000)
       assert(hotels(1).id == 3)
       assert(hotels(1).price == 500)
     }

     "find hotels by cityId sortedBy price ASC" in {
       val hotelDAO: HotelDAO = new HotelDAO()

       hotelDAO.addHotel(new Hotel(1, "Taipei", "big", 1000))
       hotelDAO.addHotel(new Hotel(2, "Bangkok", "big", 2000))
       hotelDAO.addHotel(new Hotel(3, "Taipei", "small", 500))

       val hotels: List[Hotel] = hotelDAO.findByCity("Taipei", "price", "asc")

       assert(hotels.size == 2)
       assert(hotels(0).id == 3)
       assert(hotels(0).price == 500)
       assert(hotels(1).id == 1)
       assert(hotels(1).price == 1000)
     }

     "find hotels by cityId sortedBy price DESC" in {
       val hotelDAO: HotelDAO = new HotelDAO()

       hotelDAO.addHotel(new Hotel(1, "Taipei", "big", 1000))
       hotelDAO.addHotel(new Hotel(2, "Bangkok", "big", 2000))
       hotelDAO.addHotel(new Hotel(3, "Taipei", "small", 500))

       val hotels: List[Hotel] = hotelDAO.findByCity("Taipei", "price", "desc")

       assert(hotels.size == 2)
       assert(hotels(0).id == 1)
       assert(hotels(0).price == 1000)
       assert(hotels(1).id == 3)
       assert(hotels(1).price == 500)
     }
   }
 }
