package repositories

import models.{Member, Team}
import scalikejdbc._

import scala.concurrent.Future


object TeamRepository extends SQLSyntaxSupport[Team] with RepositoryBase {

  override val tableName = "team"

  lazy val stx: QuerySQLSyntaxProvider[SQLSyntaxSupport[Team], Team] = syntax("t")

  def entity(r: ResultName[Team])(rs: WrappedResultSet): Team =
    Team(
      id = Some(rs.long(r.id)),
      name = rs.string(r.name)
    )

  def findById(id: Long)(implicit db: DBSession): Future[Option[Team]] = Future {
    withSQL {
      select.from(this as stx).where.eq(stx.id, id)
    }.map(entity(stx.resultName)).single().apply()
  }

  def listLimited(limit: Int, offset: Int = 0)(implicit db: DBSession): Future[List[Team]] = Future {
    withSQL {
      select.from(this as stx)
        .orderBy(stx.id desc)
        .limit(limit).offset(offset)
    }.map(entity(stx.resultName)).list().apply()
  }

  def list()(implicit db: DBSession): Future[List[Team]] = Future {
    withSQL {
      select.from(this as stx)
    }.map(entity(stx.resultName)).list().apply()
  }

  def listMembers(teamId: Long)(implicit db: DBSession): Future[List[Member]] = Future {
    val mr = MemberRepository
    val m = mr.stx
    val tmr = TeamMemberRepository
    val tm = tmr.stx
    withSQL {
      select.from(mr as m)
        .innerJoin(tmr as tm).on(tm.memberId, m.id)
        .where.eq(tm.teamId, teamId)
    }.map(mr.entity(m.resultName)).list().apply()
  }

  def insert(team: Team)(implicit db: DBSession): Future[Team] = Future {
    val newId = withSQL {
      insertInto(this).namedValues(Map(
        column.name -> team.name,
      ))
    }.updateAndReturnGeneratedKey.apply()
    team.copy(id = Some(newId))
  }
}


