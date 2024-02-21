/*
 * Copyright 2024 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.pegaproofofconceptproxy.controllers

import cats.syntax.eq._
import play.api.libs.json.JsValue
import play.api.mvc.{Action, ControllerComponents}
import uk.gov.hmrc.pegaproofofconceptproxy.connector.PegaConnector
import uk.gov.hmrc.pegaproofofconceptproxy.models.Payload
import uk.gov.hmrc.pegaproofofconceptproxy.models.Payload.formats
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton()
class PegaProxyController @Inject() (cc: ControllerComponents, pegaConnector: PegaConnector)(implicit ec: ExecutionContext)
  extends BackendController(cc) {

  val payload: Action[JsValue] = Action.async(parse.json) { implicit request =>
    withJsonBody[Payload]{ _ =>
      pegaConnector.submitPayload(Payload.payload).map{
        case response if response.status === 200 => Ok(response.json)
        case _                                   => InternalServerError
      }
    }
  }

}
