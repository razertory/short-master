package controllers

import javax.inject._
import play.api.mvc._
import service.ShortenerService

import scala.util.Properties

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject()(cc: ControllerComponents, shortenerService: ShortenerService) extends AbstractController(cc) {

  /**
   * Create an Action to render an HTML page.
   *
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  def index() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.index())
  }

  // TODO redirect to origin url, using 301 or 302
  def redirect(short: String) = Action { implicit request: Request[AnyContent] =>
    val url = shortenerService.getUrl(short)
    if (url == null) {
      Ok(views.html.index())
    } else {
      Redirect(url, 301)
    }
  }

  def short() = Action { request =>
    val body: AnyContent = request.body
    val form = body.asFormUrlEncoded
    form.map { data =>
      Ok(getShort(data.get("url").get(0)))
    }.getOrElse(
      BadRequest("Expecting formUrlEncoded request body")
    )
  }

  private def getShort(url: String): String = {
    val baseUrl = Properties.envOrElse("BASE_URL", "http://127.0.0.1:9000/r")
    baseUrl + "/" + shortenerService.getOrGenerate(url)
  }
}
