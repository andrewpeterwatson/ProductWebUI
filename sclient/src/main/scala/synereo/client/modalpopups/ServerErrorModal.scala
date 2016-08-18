package synereo.client.modalpopups

import diode.react.ModelProxy
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.prefix_<^._
import org.scalajs.dom._
import synereo.client.rootmodels.AppRootModel
import synereo.client.components.Bootstrap._
import synereo.client.components._
import synereo.client.components.GlobalStyles
import synereo.client.css.{LoginCSS, SynereoCommanStylesCSS}
import synereo.client.handlers.LogoutUser
import synereo.client.services.SYNEREOCircuit

import scala.language.reflectiveCalls
import scalacss.ScalaCssReact._
import diode.AnyAction._
/**
  * Created by mandar.k on 4/13/2016.
  */
object ServerErrorModal {
  // shorthand fo
  @inline private def bss = GlobalStyles.bootstrapStyles

  case class Props(submitHandler: () => Callback /*, proxy: ModelProxy[AppRootModel]*/)

  case class State()

  class Backend(t: BackendScope[Props, State]) {
    def closeForm = Callback {
      SYNEREOCircuit.dispatch(LogoutUser())
      jQuery(t.getDOMNode()).modal("hide")
    }

    def modalClosed(state: State, props: Props): Callback = {
      props.submitHandler()
    }

    def render(s: State, p: Props) = {
      val headerText = "Error"
      Modal(
        Modal.Props(
          // header contains a cancel button (X)
          header = hide => <.span(<.div()(headerText)),

          closed = () => modalClosed(s, p)
        ),

        <.div(^.className := "row")(
          <.div(^.className := "col-md-12 col-sm-12 col-xs-12")(
            <.div(^.className := "row")(
              <.div()(
                <.h3(SynereoCommanStylesCSS.Style.loginErrorHeading)(s"Encountering problems in serving request. And here is the server Error ${SYNEREOCircuit.zoom(_.appRootModel.serverErrorMsg).value}") /*,*/
                //                <.div(SYNEREOCircuit.zoom(_.appRootModel.serverErrorMsg).value)

              ),
              <.div(bss.modal.footer, SynereoCommanStylesCSS.Style.errorModalFooter)(
                <.div(^.className := "row")(
                  <.div(^.className := "col-md-12 text-center")(
                    <.div()(<.button(^.tpe := "button", ^.className := "btn btn-default", ^.onClick --> closeForm)("Close"))
                  )
                )
              )
            )
          )
        )
      )
    }
  }

  private val component = ReactComponentB[Props]("ErrorModal")
    .initialState_P(p => State())
    .renderBackend[Backend]
    // .shouldComponentUpdate(scope => scope.currentProps.proxy().isServerError)
    .build

  def apply(props: Props) = component(props)
}

