package support

import java.util.concurrent.{ExecutorService, Executors, TimeUnit}
import javax.inject.{Inject, Singleton}

import play.api.Configuration

import scala.concurrent.ExecutionContext

/**
  * ExecutorContext
  */
@Singleton
class PooledContexts@Inject()(configuration: Configuration) {
  private def svc(key:String) = {
    val threadCount = configuration.get[Int](s"threadexam.threads.${key}")
    if (threadCount == 0) {
      ExecutionContext.fromExecutorService(Executors.newCachedThreadPool())
    } else {
      ExecutionContext.fromExecutorService(Executors.newFixedThreadPool(threadCount))
    }
  }
  private def ctx(ec:ExecutorService) = {
    ExecutionContext.fromExecutor(ec)
  }
  private val db = svc("db")
  val dbContext:ExecutionContext = ctx(db)
  private val service = svc("service")
  val serviceContext:ExecutionContext = ctx(service)

  def shutdown(): Unit = {
    Seq(db, service).foreach {svc =>
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
