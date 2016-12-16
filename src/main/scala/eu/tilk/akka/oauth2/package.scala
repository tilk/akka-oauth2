package eu.tilk.akka

package object oauth2 {
  type AuthRequest = ParamSet[AuthRequestParam]
  type AuthResponse = ParamSet[AuthResponseParam]
  type TokenRequest = ParamSet[TokenRequestParam]
  type TokenResponse = ParamSet[TokenResponseParam]
  type ErrorResponse = ParamSet[ErrorResponseParam]
  type Provider = ParamSet[OAuth2ProviderParam]
  
  type StringParam = SimpleParam[String]
  type IntParam = SimpleParam[Int]
}
