package controllers

import javax.inject._

import play.api.libs.json.Json
import play.api.mvc._
import repositories.{MemberRepository, TeamRepository}
import scalikejdbc._
import services.TeamService
import support.PooledContexts

import scala.concurrent.Future

@Singleton
class TIEMainController @Inject()(cc: ControllerComponents,
                                  pc:PooledContexts,
                                  teamRepository: TeamRepository,
                                  memberRepository: MemberRepository,
                                  teamService: TeamService)
  extends AbstractController(cc) {
  private implicit val ec = pc.appContext

  def teams() = Action.async { implicit req =>
    DB.futureLocalTx{implicit db =>
      for {
        teams <- teamRepository.listLimited(30)
      } yield {
        Ok(views.html.teams(teams))
      }
    }
  }

  def allMembers() = Action {implicit req =>
    println(s"#### req = ${req}")
    Ok("")
  }

  def members(teamId:Long) = Action.async {implicit req =>
    DB.futureLocalTx{implicit db =>
      for {
        teamFound <- teamRepository.findById(teamId)
        members <- teamFound match {
          case Some(team) =>
            teamService.listTeamMembers(team.id.get)
          case _ => Future.successful(Nil)
        }
      } yield {
        if (teamFound.isDefined) {
          Ok(views.html.members(members, teamFound.get))
        } else {
          NotFound("")
        }
      }
    }
  }

  def test100() = Action.async {implicit req =>
    DB.futureLocalTx{implicit db =>
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
}
