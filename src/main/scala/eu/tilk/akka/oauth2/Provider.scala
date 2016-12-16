package eu.tilk.akka.oauth2

import akka.http.scaladsl.model._

object Providers {
  import OAuth2ProviderParam._
  import OpenIDConnectProviderParam._
  
  def FACEBOOK = Provider(
      authorization_endpoint("https://graph.facebook.com/oauth/authorize"), 
      token_endpoint("https://graph.facebook.com/oauth/access_token"))
  def FOURSQUARE = Provider(
      authorization_endpoint("https://foursquare.com/oauth2/authenticate"), 
      token_endpoint("https://foursquare.com/oauth2/access_token"))
  def GITHUB = Provider(
      authorization_endpoint("https://github.com/login/oauth/authorize"), 
      token_endpoint("https://github.com/login/oauth/access_token"))
  def GOOGLE = Provider(
      authorization_endpoint("https://accounts.google.com/o/oauth2/auth"), 
      token_endpoint("https://accounts.google.com/o/oauth2/token"),
      userinfo_endpoint("https://www.googleapis.com/oauth2/v3/userinfo"),
      revocation_endpoint("https://accounts.google.com/o/oauth2/revoke"),
      jwks_uri("https://www.googleapis.com/oauth2/v3/certs"))
  def INSTAGRAM = Provider(
      authorization_endpoint("https://api.instagram.com/oauth/authorize"), 
      token_endpoint("https://api.instagram.com/oauth/access_token"))
  def LINKEDIN = Provider(
      authorization_endpoint("https://www.linkedin.com/uas/oauth2/authorization"), 
      token_endpoint("https://www.linkedin.com/uas/oauth2/accessToken"))
  def MICROSOFT = Provider(
      authorization_endpoint("https://login.live.com/oauth20_authorize.srf"), 
      token_endpoint("https://login.live.com/oauth20_token.srf"))
  def PAYPAL = Provider( 
      authorization_endpoint("https://identity.x.com/xidentity/resources/authorize"), 
      token_endpoint("https://identity.x.com/xidentity/oauthtokenservice"))
  def REDDIT = Provider(
      authorization_endpoint("https://ssl.reddit.com/api/v1/authorize"), 
      token_endpoint("https://ssl.reddit.com/api/v1/access_token"))
  def SALESFORCE = Provider(
      authorization_endpoint("https://login.salesforce.com/services/oauth2/authorize"), 
      token_endpoint("https://login.salesforce.com/services/oauth2/token"))
  def YAMMER = Provider(
      authorization_endpoint("https://www.yammer.com/dialog/oauth"), 
      token_endpoint("https://www.yammer.com/oauth2/access_token.json"))
}

final case class ProviderConfig(val provider : Provider, val name : String, val clientId : String, val secret : String) {
}
