package eu.tilk.akka.oauth2

import scala.concurrent.ExecutionContext
import akka.actor.ActorSystem
import akka.stream.Materializer
import akka.http.scaladsl._
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.headers._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server._
import java.time.LocalDateTime
import scala.util.Random
import eu.tilk.jwt._

object Directives {
  private def getXsrfCookie : Directive1[String] = Directive { r =>
    cookie("oauth2-xsrf") { stateCookie =>
      deleteCookie(stateCookie.toCookie) {
        r(Tuple1(stateCookie.value))
      }
    }
  }
  private def testXsrfCookie : Directive0 = Directive { r =>
    (getXsrfCookie & parameter('state)) { (state1, state2) => 
      validate(state1 == state2, "Oauth2 XSRF token invalid") { 
        r(())
      }
    }
  }
  def oauth2Manager(providers : List[ProviderConfig]) : Directive[(ProviderConfig, Either[ErrorResponse, TokenResponse])] = Directive { success =>
    val pmap = providers.map(c => (c.name, c)).toMap
    (pathPrefix(pmap) & extractUri) { (conf, uri) =>
      (pathEnd & get) {
        val stateToken = LocalDateTime.now().toString + Random.nextLong().toString
        import Param._, OAuth2ProviderParam._
        val request = AuthRequest(
            client_id(conf.clientId), 
            redirect_uri(uri.withPath(uri.path / "redirect")), 
            response_type(ResponseType.code),
            scope(List(Scope.openid, Scope.email, Scope.profile)),
            state(stateToken),
            nonce(stateToken)
        )
        val location = conf.provider[authorization_endpoint].withQuery(request.toQuery)
        setCookie(HttpCookie("oauth2-xsrf", value=stateToken)) {
          complete(HttpResponse(StatusCodes.TemporaryRedirect, headers = List(Location(location))))
        }
      } ~
      (path("redirect") & testXsrfCookie & parameterMap & get) { params => 
        if (params.contains("code")) { 
          val response = AuthResponse(params)
          import Param._, OAuth2ProviderParam._
          val request = TokenRequest(
              grant_type(GrantType.authorization_code),
              client_id(conf.clientId),
              client_secret(conf.secret),
              redirect_uri(uri.withQuery(Uri.Query.Empty)),
              code(response[code])
          )
          extractExecutionContext { implicit executor => extractActorSystem { implicit system => extractMaterializer { implicit materializer => ctx =>
            val client = new Client
            client.accessToken(conf.provider[token_endpoint], request).flatMap { response =>
              success((conf, response))(ctx)
            }
          }}}
        } else if (params.contains("error")) {
          success((conf, Left(ErrorResponse(params))))
        } else {
          failWith(new Exception()) // TODO
        }
      }
    }
  }
  def openIdConnectManager(providers : List[ProviderConfig]) : Directive[(ProviderConfig, Either[ErrorResponse, (TokenResponse, Jwt)])] = Directive { success =>
    import Param._
    oauth2Manager(providers) {
      case (conf, Left(e)) => success((conf, Left(e)))
      case (conf, Right(response)) => // TODO test nonce
        validate(response.get[id_token].isDefined, "No OpenID token received") {
          val jwt = new JwtValidator(Some(conf.clientId)).validate(response[id_token])
          success((conf, Right(response, jwt)))
        }
    }
  }
  def receiveUserinfo(conf : ProviderConfig, atoken : String) : Directive1[ClaimSet] = Directive { success =>
    extractExecutionContext { implicit executor => extractActorSystem { implicit system => extractMaterializer { implicit materializer => ctx =>
      import OAuth2ProviderParam._, OpenIDConnectProviderParam._
      val client = new Client
      client.userInfo(conf.provider[userinfo_endpoint], atoken).flatMap {
        case Left(claims) => success(Tuple1(claims))(ctx)
        case Right(jwt) => success(Tuple1(jwt.claims))(ctx)
      }
    }}}
  }
}