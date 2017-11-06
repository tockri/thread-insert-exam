package services

import javax.inject.Inject

import models.{Member, Team, TeamMember}
import repositories.{MemberRepository, TeamMemberRepository, TeamRepository}
import scalikejdbc.DBSession
import support.PooledContexts

import scala.concurrent.Future

class TeamService@Inject()(teamRepository: TeamRepository,
                           teamMemberRepository: TeamMemberRepository,
                           memberRepository: MemberRepository,
                           pooledContexts: PooledContexts) {
  private implicit val ec = pooledContexts.serviceContext

  def createTeamWithRandom100Members()(implicit db:DBSession):Future[Team] = {
    for {
      members <- Future.sequence((1 to 100).map { _ =>
        createRandomMember()
      })
      team <- createTeamWithMembers(members)
    } yield team
  }

  private def createTeamWithMembers(members:Seq[Member])(implicit db:DBSession):Future[Team] = {
    val num = (Math.random() * 1000).toInt
    for {
      t <- teamRepository.insert(Team.create(s"team-${num}"))
      _ <- Future.sequence(members.map{m =>
        (t.id, m.id) match {
          case (Some(teamId), Some(memberId)) =>
            teamMemberRepository.insert(TeamMember(teamId = teamId, memberId = memberId))
        }
      })
    } yield t
  }

  private def createRandomMember()(implicit db:DBSession):Future[Member] = {
    val num = (Math.random() * 100000).toInt
    val age = (Math.random() * 100).toInt
    val m = Member.create(s"member-${num}", age)
    memberRepository.insert(m)
  }

  def listTeamMembers(teamId:Long)(implicit db:DBSession):Future[List[Member]] = {
    teamMemberRepository.listMembersByTeam(teamId)
  }
}
