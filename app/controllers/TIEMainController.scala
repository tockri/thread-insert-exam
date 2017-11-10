package controllers

import javax.inject._

import play.api.libs.json.Json
import play.api.mvc._
import repositories.{MemberRepository, TeamRepository}
import services.TeamService
import support.PooledContexts

import scala.concurrent.ExecutionContext

@Singleton
class TIEMainController @Inject()(cc: ControllerComponents,
                                  pc:PooledContexts,
                                  teamRepository: TeamRepository,
                                  memberRepository: MemberRepository,
                                  teamService: TeamService,
                                  implicit val ec:ExecutionContext)
  extends AbstractController(cc) {

  def teams() = Action.async { implicit req =>
    for {
      teams <- teamService.listTeams()
    } yield {
      Ok(views.html.teams(teams))
    }
  }

  def allMembers() = Action {implicit req =>
    println(s"#### req = ${req}")
    Ok("")
  }

  def members(teamId:Long) = Action.async { implicit req =>
    for {
      (team, members) <- teamService.listTeamAndMembers(teamId)
    } yield {
      Ok(views.html.members(members, team))
    }
  }

  def test100() = Action.async { implicit req =>
    for {
      _ <- teamService.createTeamWithRandom100Members()
    } yield {
      println("#### completed test100")
      Ok(Json.obj(
        "success" -> true
      ))
    }
  }
}
