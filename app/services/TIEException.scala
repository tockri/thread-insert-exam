package services

case class TIEException(errorType:TIEException.ErrorType, message:String, cause:Throwable = null) extends Exception(message, cause) {
}

object TIEException {
  sealed trait ErrorType
  object NotFound extends ErrorType
  object NotAllowed extends ErrorType
  object InternalError extends ErrorType
}
