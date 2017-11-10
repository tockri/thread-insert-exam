package services

import javax.inject.Inject

import models.{Member, Team, TeamMember}
import repositories.{MemberRepository, TeamMemberRepository, TeamRepository}
import scalikejdbc.DB
import support.PooledContexts

import scala.concurrent.Future

class TeamService@Inject()(teamRepository: TeamRepository,
                           teamMemberRepository: TeamMemberRepository,
                           memberRepository: MemberRepository,
                           pooledContexts: PooledContexts) {
  private implicit val ec = pooledContexts.serviceContext

  /**
    * ランダムに100人Member追加して、その全員が所属するTeamを作る
    */
  def createTeamWithRandom100Members():Future[Team] = {
    DB.futureLocalTx { implicit db =>
      for {
        members <- Future.sequence((1 to 100).map { _ =>
          // Member追加
          val num = (Math.random() * 100000).toInt
          val age = (Math.random() * 100).toInt
          val m = Member.create(s"member-${num}", age)
          memberRepository.insert(m)
        })
        team <- {
          // Team追加
          val num = (Math.random() * 1000).toInt
          teamRepository.insert(Team.create(s"team-${num}"))
        }
        _ <- Future.sequence(members.map {m =>
          // TeamMember追加
          teamMemberRepository.insert(TeamMember(team.id.get, m.id.get))
        })
      } yield team
    }
  }

  /**
    * Team所属Memberを返す
    */
  def listTeamAndMembers(teamId:Long):Future[(Team,List[Member])] = {
    DB.futureLocalTx { implicit db =>
      for {
        team <- teamRepository.findById(teamId).map{
          _.getOrElse(throw TIEException(TIEException.NotFound, "not found"))
        }
        members <- teamMemberRepository.listMembersByTeam(team.id.get)
      } yield (team, members)
    }
  }

  /**
    * チーム一覧を返す
    */
  def listTeams():Future[List[Team]] = {
    DB.futureLocalTx{ implicit db =>
      teamRepository.listLimited(30)
    }
  }
}
