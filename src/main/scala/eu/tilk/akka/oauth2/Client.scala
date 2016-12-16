package eu.tilk.akka.oauth2

import java.nio.charset.StandardCharsets
import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import scala.collection.JavaConverters._
import akka.actor.ActorSystem
import akka.stream.Materializer
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import headers._
import scala.reflect.runtime.universe.TypeTag
import scala.concurrent.Future
import eu.tilk.jwt.{Jwt, ClaimSet}

private[oauth2]
class Client(implicit system : ActorSystem, materializer : Materializer, ec : ExecutionContext) {
  def accessToken(uri : Uri, oauthRequest : ParamSet[RequestParam], method : HttpMethod = HttpMethods.POST) : Future[Either[ErrorResponse, TokenResponse]] = {
    val entity = if (method == HttpMethods.GET) HttpEntity.Empty
    else HttpEntity(MediaTypes.`application/x-www-form-urlencoded` withCharset HttpCharsets.`UTF-8`, oauthRequest.toQuery.toString)
    val request = HttpRequest(method, uri = uri, entity = entity)
    Http().singleRequest(request).flatMap { response =>
      response.entity.toStrict(FiniteDuration(1, SECONDS)).map { entity =>
        val responseString = entity.data.decodeString(entity.contentType.charsetOption.map(_.nioCharset()).getOrElse(StandardCharsets.UTF_8))
        if (response.status.isSuccess()) Right(TokenResponse(responseString, entity.contentType))
        else Left(ErrorResponse(responseString, entity.contentType))
      }
    }
  }
  def userInfo(uri : Uri, access_token : String, method : HttpMethod = HttpMethods.GET) : Future[Either[ClaimSet, Jwt]] = {
    val request = HttpRequest(method, uri = uri, headers = List(headers.Authorization(OAuth2BearerToken(access_token))))
    Http().singleRequest(request).flatMap { response =>
      response.entity.toStrict(FiniteDuration(1, SECONDS)).flatMap { entity =>
        if (response.status.isSuccess()) {
          import eu.tilk.jwt._
          val responseString = entity.data.decodeString(entity.contentType.charsetOption.map(_.nioCharset()).getOrElse(StandardCharsets.UTF_8))
          entity.contentType match {
            case ContentType(MediaTypes.`application/json`, _) => Future(Left(ClaimSet(responseString)))
            case ContentType(MediaType("application/jwt"), _) => Future(Right(new JwtValidator().validate(responseString)))
            case _ => Future.failed(new Exception(entity.contentType.toString)) // TODO
          }
        } else Future.failed(new Exception()) // TODO
      }
    }
  }
}