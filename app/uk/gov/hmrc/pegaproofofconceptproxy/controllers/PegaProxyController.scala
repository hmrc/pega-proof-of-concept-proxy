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

import play.api.Logging
import play.api.mvc.{Action, AnyContent, ControllerComponents, Result}
import uk.gov.hmrc.http.HttpResponse
import uk.gov.hmrc.pegaproofofconceptproxy.connector.PegaConnector
import uk.gov.hmrc.pegaproofofconceptproxy.models.StartCaseRequest
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton()
class PegaProxyController @Inject() (cc: ControllerComponents, pegaConnector: PegaConnector)(implicit ec: ExecutionContext)
  extends BackendController(cc) with Logging {

  val startCase: Action[AnyContent] = Action.async { implicit request =>
    pegaConnector.startCase(StartCaseRequest.payload).map(forwardResponse)
  }

  def getCase(caseId: String): Action[AnyContent] = Action.async { implicit request =>
    pegaConnector.getCase(caseId).map(forwardResponse)
  }

  private def forwardResponse(httpResponse: HttpResponse): Result =
    Option(httpResponse.body).fold[Result](Status(httpResponse.status))(body => Status(httpResponse.status)(body))

}
