package repositories

import services.PooledContexts

import scala.concurrent.ExecutionContext

trait RepositoryBase {
  implicit val executorContext: ExecutionContext = PooledContexts.dbContext


}
