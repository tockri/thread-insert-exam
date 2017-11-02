package models

case class Team(id:Option[Long], name:String) {

}

object Team {
  def create(name:String) = Team(None, name)
}
