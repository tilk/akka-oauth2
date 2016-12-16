package eu.tilk.akka.oauth2

import akka.http.scaladsl.model._

trait Param {
  type T
  def name : String = this.getClass.getSimpleName
  def value : T
  def stringValue : String
}
abstract class SimpleParam[U] extends Param {
  type T = U
  def stringValue : String = value.toString
}
abstract class ListParam[U] extends Param {
  type T = List[U]
  def stringValue = value.map(_.toString).mkString(" ")
}
trait RequestParam extends Param
trait ResponseParam extends Param
trait AuthResponseParam extends ResponseParam
trait TokenResponseParam extends ResponseParam
trait AuthRequestParam extends RequestParam
trait TokenRequestParam extends RequestParam
trait ErrorResponseParam extends Param
trait OAuth2ProviderParam extends Param
trait OpenIDConnectProviderParam extends OAuth2ProviderParam

trait ParamType[+T <: Param] {
  def apply(str : String) : T
  def name = getClass.getSimpleName.stripSuffix("$")
}
abstract class SimpleParamType[T <: Param](f : String => T#T) extends ParamType[T] with Function1[T#T, T] {
  def apply(str : String) : T = apply(f(str))
}
abstract class StringParamType[T <: StringParam] extends SimpleParamType[T](x => x)
abstract class IntParamType[T <: IntParam] extends SimpleParamType[T](_.toInt)
abstract class ListParamType[T <: ListParam[V], V](f : String => V) extends SimpleParamType[T](_.split(" ").map(f).toList)
abstract class UriParamType[T <: SimpleParam[Uri]] extends SimpleParamType[T](Uri.apply)

object Param {
  case class response_type(val value : ResponseType) extends SimpleParam[ResponseType] with AuthRequestParam
  object response_type extends SimpleParamType[response_type](ResponseType)
  
  case class client_id(val value : String) extends StringParam with AuthRequestParam with TokenRequestParam
  object client_id extends StringParamType[client_id]
  
  case class client_secret(val value : String) extends StringParam with TokenRequestParam
  object client_secret extends StringParamType[client_secret]
  
  case class scope(val value : List[Scope]) extends ListParam[Scope] with AuthRequestParam
    with TokenRequestParam with AuthResponseParam with TokenResponseParam
  object scope extends ListParamType[scope, Scope](Scope)
  
  case class redirect_uri(val value : Uri) extends SimpleParam[Uri] with AuthRequestParam with TokenRequestParam
  object redirect_uri extends UriParamType[redirect_uri]
  
  case class state(val value : String) extends StringParam with AuthRequestParam with AuthResponseParam
  object state extends StringParamType[state]
  
  case class nonce(val value : String) extends StringParam with AuthRequestParam
  object nonce extends StringParamType[nonce]
  
  case class display(val value : Display) extends SimpleParam[Display] with AuthRequestParam
  object display extends SimpleParamType[display](Display)
  
  case class prompt(val value : List[Prompt]) extends ListParam[Prompt] with AuthRequestParam
  object prompt extends ListParamType[prompt, Prompt](Prompt)
  
  case class max_age(val value : Int) extends SimpleParam[Int] with AuthRequestParam
  object max_age extends IntParamType[max_age]
  
  case class id_token_hint(val value : String) extends StringParam with AuthRequestParam
  object id_token_hint extends StringParamType[id_token_hint]
  
  case class login_hint(val value : String) extends StringParam with AuthRequestParam
  object login_hint extends StringParamType[login_hint]
  
  case class ui_locales(val value : List[String]) extends ListParam[String] with AuthRequestParam
  object ui_locales extends ListParamType[ui_locales, String](x => x)

  case class claims_locales(val value : List[String]) extends ListParam[String] with AuthRequestParam
  object claims_locales extends ListParamType[claims_locales, String](x => x)

  case class acr_values(val value : List[String]) extends ListParam[String] with AuthRequestParam
  object acr_values extends ListParamType[acr_values, String](x => x)
  
  case class claims(val value : Claims) extends SimpleParam[Claims] with AuthRequestParam
  object claims extends SimpleParamType[claims](Claims)
  
  case class registration(val value : Registration) extends SimpleParam[Registration] with AuthRequestParam
  object registration extends SimpleParamType[registration](Registration)
  
  case class request(val value : Request) extends SimpleParam[Request] with AuthRequestParam
  object request extends SimpleParamType[request](Request)
  
  case class request_uri(val value : Uri) extends SimpleParam[Uri] with AuthRequestParam
  object request_uri extends UriParamType[request_uri]
  
  case class grant_type(val value : GrantType) extends SimpleParam[GrantType] with TokenRequestParam
  object grant_type extends SimpleParamType[grant_type](GrantType)
  
  case class code(val value : String) extends StringParam with AuthResponseParam with TokenRequestParam
  object code extends StringParamType[code]
  
  case class username(val value : String) extends StringParam with TokenRequestParam
  object username extends StringParamType[username]
  
  case class password(val value : String) extends StringParam with TokenRequestParam
  object password extends StringParamType[password]
  
  case class refresh_token(val value : String) extends StringParam with TokenRequestParam with TokenResponseParam
  object refresh_token extends StringParamType[refresh_token]
  
  case class access_token(val value : String) extends StringParam with AuthResponseParam with TokenResponseParam
  object access_token extends StringParamType[access_token]
  
  case class token_type(val value : TokenType) extends SimpleParam[TokenType] with AuthResponseParam with TokenResponseParam
  object token_type extends SimpleParamType[token_type](TokenType)
  
  case class id_token(val value : String) extends StringParam with TokenResponseParam
  object id_token extends StringParamType[id_token]
  
  case class expires_in(val value : Int) extends SimpleParam[Int] with AuthResponseParam with TokenResponseParam
  object expires_in extends IntParamType[expires_in]
  
  case class error(val value : String) extends StringParam with ErrorResponseParam
  object error extends StringParamType[error]
  
  case class error_description(val value : String) extends StringParam with ErrorResponseParam
  object error_description extends StringParamType[error_description]
  
  case class error_uri(val value : Uri) extends SimpleParam[Uri] with ErrorResponseParam
  object error_uri extends SimpleParamType[error_uri](Uri.apply)
  
}

object OAuth2ProviderParam {
  case class authorization_endpoint(val value : Uri) extends SimpleParam[Uri] with OAuth2ProviderParam 
  object authorization_endpoint extends SimpleParamType[authorization_endpoint](Uri.apply)
  
  case class token_endpoint(val value : Uri) extends SimpleParam[Uri] with OAuth2ProviderParam 
  object token_endpoint extends SimpleParamType[token_endpoint](Uri.apply)  
}

object OpenIDConnectProviderParam {
  case class userinfo_endpoint(val value : Uri) extends SimpleParam[Uri] with OpenIDConnectProviderParam 
  object userinfo_endpoint extends SimpleParamType[userinfo_endpoint](Uri.apply)  
  
  case class revocation_endpoint(val value : Uri) extends SimpleParam[Uri] with OpenIDConnectProviderParam 
  object revocation_endpoint extends SimpleParamType[revocation_endpoint](Uri.apply)  
  
  case class jwks_uri(val value : Uri) extends SimpleParam[Uri] with OpenIDConnectProviderParam 
  object jwks_uri extends SimpleParamType[jwks_uri](Uri.apply)  
}
