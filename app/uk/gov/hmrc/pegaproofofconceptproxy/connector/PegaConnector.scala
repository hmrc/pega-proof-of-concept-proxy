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

import com.google.inject.{Inject, Singleton}
import play.api.libs.json.Json
import play.api.Logging
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse, StringContextOps, UpstreamErrorResponse}
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.HttpReads.Implicits.readRaw
import uk.gov.hmrc.pegaproofofconceptproxy.models.Payload
import uk.gov.hmrc.http.HttpReads.Implicits._
import uk.gov.hmrc.pegaproofofconceptproxy.config.AppConfig

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class PegaConnector @Inject() (client: HttpClientV2, config: AppConfig)(implicit ec: ExecutionContext) extends Logging {

  val pegaUrl: String = config.pegaUrl.url
  def submitPayload(payload: Payload)(implicit hc: HeaderCarrier): Future[HttpResponse] = {
    client.post(url"$pegaUrl")
      .withBody(Json.toJson(payload))
      .execute[Either[UpstreamErrorResponse, HttpResponse]]
      .map {
        case Right(response) => response
        case Left(err) =>
          logger.warn("pega-proof-of-concept stubs connection failed with error")
          throw err
      }
  }

}
