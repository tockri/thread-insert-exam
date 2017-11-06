package repositories

import support.PooledContexts

import scala.concurrent.ExecutionContext

abstract class RepositoryBase(pc:PooledContexts) {
  implicit val executorContext: ExecutionContext = pc.dbContext
}
