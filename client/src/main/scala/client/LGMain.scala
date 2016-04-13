package client


import client.modules
import japgolly.scalajs.react.extra.router._
import client.components.{GlobalStyles, Icon}
import client.css.{AppCSS, FooterCSS, HeaderCSS, DashBoardCSS}
import client.logger._
import client.modules._
import org.querki.jquery._
import org.scalajs.dom
import client.services.LGCircuit
import scala.scalajs.js.annotation.JSExport
import scalacss.Defaults._
import scalacss.ScalaCssReact._
import scalacss.mutable.GlobalRegistry
import japgolly.scalajs.react.{ReactDOM, React}
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.prefix_<^._
import scala.scalajs.js
import js.{Date, UndefOr}

@JSExport("LGMain")
object LGMain extends js.JSApp {

  // Define the locations (pages) used in this application
  sealed trait Loc

  case object DashboardLoc extends Loc

  case object TodoLoc extends Loc

  case object CreateAgentLoc extends Loc

  case object AgentLoginLoc extends Loc

  case object MessagesLoc extends Loc

  case object JobPostsLoc extends Loc

  case object ContractsLoc extends Loc

  case object ContestsLoc extends Loc

  case object EmployersLoc extends Loc

  case object OfferingsLoc extends Loc

  case object ProfilesLoc extends Loc

  case object ConnectionsLoc extends Loc

  case object LegalLoc extends Loc

  case object BiddingScreenLoc extends Loc

  case object LandingLoc extends Loc

  val menuItem = MainMenu.menuItems
 // println(menuItem)

  def sidebar = Callback {
    val sidebtn: js.Object = "#searchContainer"
    $(sidebtn).toggleClass("sidebar-left sidebar-animate sidebar-md-show")
  }

  // configure the router
  val routerConfig = RouterConfigDsl[Loc].buildConfig { dsl =>
    import dsl._
    (staticRoute(root, LandingLoc) ~> renderR(ctl => LandingLocation.component(ctl))
      | staticRoute("#dashboard", DashboardLoc) ~> renderR(ctl => Dashboard.component(ctl))
      | staticRoute("#messages", MessagesLoc) ~> renderR(ctl => AppModule(AppModule.Props("messages")))
      | staticRoute("#projects", JobPostsLoc) ~> renderR(ctl => AppModule(AppModule.Props("projects")))
      | staticRoute("#contract", ContractsLoc) ~> renderR(ctl => AppModule(AppModule.Props("contract")))
      | staticRoute("#contests", ContestsLoc) ~> renderR(ctl => <.div(^.id := "mainContainer", ^.className := "DashBoardCSS_Style-mainContainerDiv")(""))
      | staticRoute("#talent", ProfilesLoc) ~> renderR(ctl => AppModule(AppModule.Props("talent")))
      | staticRoute("#offerings", OfferingsLoc) ~> renderR(ctl => AppModule(AppModule.Props("offerings")))
      | staticRoute("#employers", EmployersLoc) ~> renderR(ctl => <.div(^.id := "mainContainer", ^.className := "DashBoardCSS_Style-mainContainerDiv")(""))
      | staticRoute("#connections", ConnectionsLoc) ~> renderR(ctl => AppModule(AppModule.Props("connections")))
      ).notFound(redirectToPage(LandingLoc)(Redirect.Replace))
  }.renderWith(layout)

  // base layout for all pages
  def layout(c: RouterCtl[Loc], r: Resolution[Loc]) = {
      <.div()(
      <.img(^.id := "loginLoader", DashBoardCSS.Style.loading, ^.className := "hidden", ^.src := "./assets/images/processing.gif"),
      <.nav(^.id := "naviContainer", HeaderCSS.Style.naviContainer, ^.className := "navbar navbar-fixed-top")(
        <.div(^.className := "col-lg-1")(),
        <.div(^.className := "col-lg-10")(
          <.div(^.className := "navbar-header", ^.display := "flex")(

            //Adding toggle button for sidebar
            <.button(^.id := "sidebarbtn", ^.`type` := "button", ^.className := "navbar-toggle toggle-left hidden-md hidden-lg", ^.float := "left", "data-toggle".reactAttr := "sidebar", "data-target".reactAttr := ".sidebar-left",
              ^.onClick --> sidebar)(
              <.span(Icon.bars)
            ),
            c.link(LandingLoc)(^.className := "navbar-header", <.img(HeaderCSS.Style.imgLogo, ^.src := "./assets/images/logo-symbol.png")),
            <.button(^.className := "navbar-toggle", "data-toggle".reactAttr := "collapse", "data-target".reactAttr := "#navi-collapse")(
              // ToDo:  put actual menu name below, not r.page.toString.  Also some alignment problems?
               <.span(^.color := "white",^.float:="right")( r.page.toString.substring(0,r.page.toString.length-3),"  ", Icon.thList)
            ),
            <.div(^.className:="loggedInUserNav")(LGCircuit.connect(_.user)(proxy => LoggedInUser(LoggedInUser.Props(c, r.page, proxy))))
          ),
          <.div(^.id := "navi-collapse", ^.className := "collapse navbar-collapse")(
            LGCircuit.connect(_.user)(proxy => MainMenu(MainMenu.Props(c, r.page, proxy)))
          ),
          <.div(^.className:="loggedInUser")(LGCircuit.connect(_.user)(proxy => LoggedInUser(LoggedInUser.Props(c, r.page, proxy))))
        ),
        <.div()()
      ),
      // the vertically center area
      r.render(),
      Footer(Footer.Props(c, r.page))
    )
  }

  @JSExport
  def main(): Unit = {
    log.warn("LGMain - Application starting")
    // send log messages also to the server
    log.enableServerLogging("/logging")
    log.info("LGMain - This message goes to server as well")
    // create stylesheet
    GlobalStyles.addToDocument()
    AppCSS.load
    GlobalRegistry.addToDocumentOnRegistration()
    // create the router
    val router = Router(BaseUrl(dom.window.location.href.takeWhile(_ != '#')), routerConfig)
    // tell React to render the router in the document body
    //ReactDOM.render(router(), dom.document.getElementById("root"))
    ReactDOM.render(router(), dom.document.getElementById("root"))
  }
}