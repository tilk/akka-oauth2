package eu.tilk.akka.oauth2

import akka.http.scaladsl.model._
import scala.util.parsing.json._

abstract class Message[T <: Param] private (paramTypes : Map[String, ParamType[T]]) {
  def this(paramTypes : Seq[ParamType[T]]) = this(paramTypes.map(x => (x.name, x)).toMap)
  def apply(params : T*) = ParamSet(params:_*)
  def apply(query : Uri.Query) : ParamSet[T] = apply(query.toSeq)
  def apply(query : Iterable[(String, String)]) : ParamSet[T] = {
    val pts = query.toSeq.map(p => paramTypes.get(p._1).map(_(p._2)).toList).flatten
    apply(pts:_*)
  }
  def apply(s : String, contentType : ContentType) : ParamSet[T] = contentType.mediaType match {
    case MediaTypes.`application/x-www-form-urlencoded` => apply(Uri.Query(s))
    case MediaTypes.`application/json` => 
      apply(JSON.parseFull(s).get.asInstanceOf[Map[String, Any]].mapValues {
        case s : String => s
        case d : Double => d.toInt.toString
      })
    case _ => throw new IllegalArgumentException("Got response of type " ++ contentType.toString)
  }
}

import Param._

import OAuth2ProviderParam._
import OpenIDConnectProviderParam._

object AuthRequest extends Message[AuthRequestParam](
    List(scope, redirect_uri, response_type, client_id, state, nonce, display, prompt, max_age, login_hint, ui_locales, acr_values)) 

object TokenRequest extends Message[TokenRequestParam](
    List(scope, client_id, client_secret, redirect_uri, grant_type, code, username, password, refresh_token)) 

object AuthResponse extends Message[AuthResponseParam](List(code, access_token, token_type, expires_in, scope, state)) 

object TokenResponse extends Message[TokenResponseParam](List(scope, expires_in, access_token, refresh_token, id_token, token_type))

object ErrorResponse extends Message[ErrorResponseParam](List(error, error_description, error_uri))

object Provider extends Message[OAuth2ProviderParam](List(authorization_endpoint, token_endpoint, userinfo_endpoint, revocation_endpoint, jwks_uri)) 
