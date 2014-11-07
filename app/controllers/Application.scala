package controllers

import actors.User
import play.api.mvc._
import play.api.libs.json.{JsValue}
import play.api.Play.current

object Application extends Controller {

  def index = Action { implicit request =>
    Ok(views.html.index())
  }

  def ws = WebSocket.acceptWithActor[JsValue, JsValue] { request => client =>
    User.props(client)
  }
}