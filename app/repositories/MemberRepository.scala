package repositories

import java.time.ZonedDateTime

import models.Member
import scalikejdbc._

import scala.concurrent.Future

object MemberRepository extends SQLSyntaxSupport[Member] with RepositoryBase {
  override val tableName = "member"

  lazy val stx: QuerySQLSyntaxProvider[SQLSyntaxSupport[Member], Member] = syntax("m")

  def entity(r: ResultName[Member])(rs: WrappedResultSet): Member =
    Member(
      id = Some(rs.long(r.id)),
      name = rs.string(r.name),
      age = rs.int(r.age),
      created = Some(rs.get(r.created))
    )

  def findById(id: Long)(implicit db: DBSession): Future[Option[Member]] = Future {
    withSQL {
      select.from(this as stx).where.eq(stx.id, id)
    }.map(entity(stx.resultName)).single().apply()
  }

  def listByTeam(teamId: Long)(implicit db: DBSession): Future[List[Member]] = Future {
    val tm = TeamMemberRepository.stx
    withSQL {
      select.from(this as stx)
        .innerJoin(TeamMemberRepository as tm)
        .on(tm.memberId, stx.id)
        .where.eq(tm.teamId, teamId)
        .orderBy(stx.id desc)
        .limit(30)
    }.map(entity(stx.resultName)).list().apply()
  }

  def insert(member: Member)(implicit db: DBSession): Future[Member] = Future {
    val now = ZonedDateTime.now()
    val newId = withSQL {
      insertInto(this).namedValues(Map(
        column.name -> member.name,
        column.age -> member.age,
        column.created -> now
      ))
    }.updateAndReturnGeneratedKey.apply()
    member.copy(id = Some(newId), created = Some(now))
  }
}
