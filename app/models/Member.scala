package models

import java.time.ZonedDateTime

case class Member(id:Option[Long], name:String, age:Int, created:Option[ZonedDateTime]) {
}

object Member {
  def create(name:String, age:Int):Member =
    Member(id = None, name = name, age = age, created = None)
}
