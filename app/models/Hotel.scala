package models

/**
 *
 * @author roger1224@gmail.com
 *
 *         $$DateTime$$
 *         $$Change$$
 *         $$Author$$
 */
class Hotel(val _id: Long,
            val _city: String,
            val _room: String,
            val _price: Double) {

  def id = _id

  def city = _city

  def room = _room

  def price = _price


  override def toString = s"Hotel($id, $city, $room, $price)"


}
