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

package uk.gov.hmrc.pegaproofofconceptproxy.connector

import cats.syntax.either._
import com.google.inject.{Inject, Singleton}
import play.api.Logging
import play.api.http.HeaderNames
import play.api.libs.json.Json
import uk.gov.hmrc.http.HttpReads.Implicits._
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse, StringContextOps, UpstreamErrorResponse}
import uk.gov.hmrc.pegaproofofconceptproxy.config.AppConfig
import uk.gov.hmrc.pegaproofofconceptproxy.models.StartCaseRequest

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class PegaConnector @Inject() (client: HttpClientV2, config: AppConfig)(implicit ec: ExecutionContext) extends Logging {

  def startCase(startCaseRequest: StartCaseRequest, pegaToken: String)(implicit hc: HeaderCarrier): Future[HttpResponse] =
    client
      .post(url"${config.pegaStartCaseUrl}")
      .withProxy
      .withBody(Json.toJson(startCaseRequest))
      .setHeader(HeaderNames.AUTHORIZATION -> "Bearer ".concat(pegaToken))
      .execute[Either[UpstreamErrorResponse, HttpResponse]]
      .map(_.leftMap(throw _).merge)

  def getCase(caseId: String, pegaToken: String)(implicit hc: HeaderCarrier): Future[HttpResponse] =
    client
      .get(url"${config.pegaGetCaseUrl}/$caseId")
      .withProxy
      .setHeader(HeaderNames.AUTHORIZATION -> "Bearer ".concat(pegaToken))
      .execute[Either[UpstreamErrorResponse, HttpResponse]]
      .map(_.leftMap(throw _).merge)

}
