package repositories

import models.TeamMember
import scalikejdbc._

import scala.concurrent.Future

object TeamMemberRepository extends SQLSyntaxSupport[TeamMember] with RepositoryBase {
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
}
