package repositories

import javax.inject.{Inject, Singleton}

import models.{Member, TeamMember}
import scalikejdbc._
import support.PooledContexts

import scala.concurrent.Future

@Singleton
class TeamMemberRepository@Inject()(pc:PooledContexts,
                                    memberRepository: MemberRepository)
  extends RepositoryBase(pc) with SQLSyntaxSupport[TeamMember] {
  override val tableName = "team_member"

  lazy val stx: QuerySQLSyntaxProvider[SQLSyntaxSupport[TeamMember], TeamMember] = syntax("tm")

  def entity(r:ResultName[TeamMember])(rs:WrappedResultSet):TeamMember =
    TeamMember(
      memberId = rs.long(r.memberId),
      teamId = rs.long(r.teamId)
    )

  def insert(teamMember: TeamMember)(implicit db:DBSession):Future[TeamMember] = Future {
    withSQL {
      insertInto(this).namedValues(Map(
        column.teamId -> teamMember.teamId,
        column.memberId -> teamMember.memberId
      ))
    }.update().apply()
    teamMember
  }

  def listMembersByTeam(teamId: Long)(implicit db: DBSession): Future[List[Member]] = Future {
    val mr = memberRepository
    val m = mr.stx
    withSQL {
      select.from(mr as m)
        .innerJoin(this as stx).on(stx.memberId, m.id)
        .where.eq(stx.teamId, teamId)
    }.map(mr.entity(m.resultName)).list().apply()
  }
}
