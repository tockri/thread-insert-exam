package support

import scala.concurrent.{ExecutionContext, Future}

trait ThreadingSupport {

  def sequential[A, B](funcs: List[A => Future[B]])(implicit ec:ExecutionContext) = { (args: List[A]) =>
    funcs.zip(args).foldLeft[Future[List[B]]](Future.successful(Nil)) { case (fResults, (func, arg)) =>
      for {
        results <- fResults
        r <- func(arg)
      } yield results :+ r
    }
  }
}
