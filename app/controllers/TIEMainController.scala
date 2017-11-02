package controllers

import javax.inject._

import models.{Member, Team, TeamMember}
import play.api.libs.json.Json
import play.api.mvc._
import repositories.{MemberRepository, TeamMemberRepository, TeamRepository}
import services.PooledContexts
import scalikejdbc._

import scala.concurrent.Future

@Singleton
class TIEMainController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {
  implicit val ec = PooledContexts.appContext

  def teams() = Action.async { implicit req =>
    DB.futureLocalTx{implicit db =>
      for {
        teams <- TeamRepository.listLimited(30)
      } yield {
        Ok(views.html.teams(teams))
      }
    }
  }

  def allMembers() = Action {implicit req =>
    Ok("")
  }

  def members(teamId:Long) = Action.async {implicit req =>
    DB.futureLocalTx{implicit db =>
      for {
        teamFound <- TeamRepository.findById(teamId)
        members <- teamFound match {
          case Some(team) =>
            MemberRepository.listByTeam(teamId)
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
        members <- Future.sequence((1 to 100).map { _ =>
          insertMember()
        })
        _ <- insertTeam(members = members)
      } yield {
        Ok(Json.obj(
          "success" -> true
        ))
      }
    }
  }

  private def insertTeam(members:Seq[Member])(implicit db:DBSession):Future[Team] = {
    val num = (Math.random() * 1000).toInt
    for {
      t <- TeamRepository.insert(Team.create(s"team-${num}"))
      _ <- Future.sequence(members.map{m =>
        (t.id, m.id) match {
          case (Some(teamId), Some(memberId)) =>
            TeamMemberRepository.insert(TeamMember(teamId = teamId, memberId = memberId))
        }
      })
    } yield t
  }

  private def insertMember()(implicit db:DBSession):Future[Member] = {
    val num = (Math.random() * 100000).toInt
    val age = (Math.random() * 100).toInt
    val m = Member.create(s"member-${num}", age)
    MemberRepository.insert(m)
  }
}
