package repositories

import java.time.ZonedDateTime
import javax.inject.{Inject, Singleton}

import models.Member
import scalikejdbc._
import support.PooledContexts

import scala.concurrent.Future

@Singleton
class MemberRepository@Inject()(pc:PooledContexts)
  extends RepositoryBase(pc) with SQLSyntaxSupport[Member] {
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
