package services

import java.util.concurrent.{ExecutorService, Executors, TimeUnit}

import scala.concurrent.ExecutionContext

/**
  * ExecutorContext
  */
object PooledContexts {
  private def newService(threadCount:Int):ExecutorService =
    ExecutionContext.fromExecutorService(Executors.newFixedThreadPool(threadCount))
  private def ctx(service:ExecutorService) = ExecutionContext.fromExecutor(service)

  private val db = newService(4)
  val dbContext:ExecutionContext = ctx(db)

  private val app = ExecutionContext.fromExecutorService(Executors.newCachedThreadPool())
  val appContext:ExecutionContext = ctx(app)

  private val service = newService(4)
  val serviceContext:ExecutionContext = ctx(service)

  def shutdown(): Unit = {
    Seq(db, app, service).foreach {svc =>
      try {
        svc.awaitTermination(Long.MaxValue, TimeUnit.NANOSECONDS)
      } catch {
        case ex:Exception => println(ex)
      } finally {
        svc.shutdownNow()
      }
    }
  }

}
