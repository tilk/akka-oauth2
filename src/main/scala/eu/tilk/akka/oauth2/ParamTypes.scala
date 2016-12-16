package eu.tilk.akka.oauth2

import io.circe._, io.circe.parser._

case class Claim(val essential : Boolean = false, val values : List[String] = Nil) {
  def toJson = this match {
    case Claim(false, Nil) => Json.Null
    case Claim(true, Nil) => Json.obj(("essential", Json.True))
    case Claim(_, List(v)) => Json.obj(("essential", Json.fromBoolean(essential)), ("value", Json.fromString(v)))
    case _ => Json.obj(("essential", Json.fromBoolean(essential)), ("values", Json.arr(values.map(Json.fromString(_)):_*)))
  }
}

case class Claims(val userinfo : Map[String, Claim], val id_token : Map[String, Claim]) {
  def toJson = Json.obj(
      ("userinfo", Json.obj(userinfo.mapValues(_.toJson).toSeq:_*)),
      ("id_token", Json.obj(id_token.mapValues(_.toJson).toSeq:_*)))
  override def toString = toJson.toString
}
object Claims extends Function1[String, Claims] {
  def apply(s : String) = parse(s) match {
    case Left(_) => throw new IllegalArgumentException()
    case Right(j) => throw new UnsupportedOperationException() // TODO
  }
}

case class Registration() // TODO
object Registration extends Function1[String, Registration] {
  def apply(s : String) = Registration() // TODO
}

case class Request() // TODO
object Request extends Function1[String, Request] {
  def apply(s : String) = Request() // TODO
}

sealed abstract class TokenType
object TokenType extends Function1[String, TokenType] {
  case object Bearer extends TokenType
  def apply(s : String) = s match {
    case "Bearer" => Bearer
    case _ => throw new IllegalArgumentException()
  }
}

sealed abstract class Scope
object Scope extends Function1[String, Scope] {
  case object openid extends Scope
  case object profile extends Scope
  case object email extends Scope
  case object address extends Scope
  case object phone extends Scope
  case object offline_access extends Scope
  def apply(s : String) = s match {
    case "openid" => openid
    case "profile" => profile
    case "email" => email
    case "address" => address
    case "phone" => phone
    case "offline_access" => offline_access
    case _ => throw new IllegalArgumentException()
  }
}

sealed abstract class ResponseType
object ResponseType extends Function1[String, ResponseType] {
  case object code extends ResponseType
  def apply(s : String) = s match {
    case "code" => code
    case _ => throw new IllegalArgumentException()
  }
}

sealed abstract class Display
object Display extends Function1[String, Display] {
  case object page extends Display
  case object popup extends Display
  case object touch extends Display
  case object wap extends Display
  def apply(s : String) = s match {
    case "page" => page
    case "popup" => popup
    case "touch" => touch
    case "wap" => wap
    case _ => throw new IllegalArgumentException()
  }
}

sealed abstract class Prompt
object Prompt extends Function1[String, Prompt] {
  case object none extends Prompt
  case object login extends Prompt
  case object consent extends Prompt
  case object select_account extends Prompt
  def apply(s : String) = s match {
    case "none" => none
    case "login" => login
    case "consent" => consent
    case "select_account" => select_account
    case _ => throw new IllegalArgumentException()
  }
}

sealed abstract class GrantType
object GrantType extends Function1[String, GrantType] {
  case object authorization_code extends GrantType
  case object password extends GrantType
  case object client_credentials extends GrantType
  case object refresh_token extends GrantType
  def apply(s : String) = s match {
    case "authorization_code" => authorization_code
    case "password" => password
    case "client_credentials" => client_credentials
    case "refresh_token" => refresh_token
    case _ => throw new IllegalArgumentException()
  }
}

sealed abstract class Error
object Error extends Function1[String, Error] {
  case object invalid_request extends Error
  case object invalid_client extends Error
  case object unauthorized_client extends Error
  case object access_denied extends Error
  case object unsupported_response_type extends Error
  case object unsupported_grant_type extends Error
  case object invalid_scope extends Error
  case object invalid_grant extends Error
  case object server_error extends Error
  case object temporarily_unavailable extends Error
  def apply(s : String) : Error = s match {
    case "invalid_request" => invalid_request
    case "invalid_client" => invalid_client
    case "unauthorized_client" => unauthorized_client
    case "access_denied" => access_denied
    case "unsupported_response_type" => unsupported_response_type
    case "unsupported_grant_type" => unsupported_grant_type
    case "invalid_scope" => invalid_scope
    case "invalid_grant" => invalid_grant
    case "server_error" => server_error
    case "temporarily_unavailiable" => temporarily_unavailable
  }
}

