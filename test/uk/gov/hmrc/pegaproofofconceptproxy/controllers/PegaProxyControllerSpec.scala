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

import com.github.tomakehurst.wiremock.client.WireMock.{aResponse, post, stubFor, urlPathEqualTo}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import org.scalatestplus.scalacheck.ScalaCheckDrivenPropertyChecks
import play.api.http.Status
import play.api.libs.json.Json
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.http.test.ExternalWireMockSupport
import uk.gov.hmrc.pegaproofofconceptproxy.testsupport.FakeApplicationProvider
import uk.gov.hmrc.pegaproofofconceptproxy.models.Payload.formats
import uk.gov.hmrc.pegaproofofconceptproxy.utils.Generators

class PegaProxyControllerSpec extends AnyWordSpec with Matchers with GuiceOneAppPerSuite with ExternalWireMockSupport with FakeApplicationProvider
  with Generators with ScalaCheckDrivenPropertyChecks {

  private val controller = app.injector.instanceOf[PegaProxyController]

  "PegaProxyController" should {
    "return ok if stubs return ok" in {
      forAll(nonEmptyPayload) { payload =>
        stubFor(
          post(urlPathEqualTo("/pega-proof-of-concept-stubs/submit-payload"))
            .willReturn(aResponse().withStatus(200))
        )
        val fakeRequestWithJson = FakeRequest().withBody(Json.toJsObject(payload))

        val result = controller.payload()(fakeRequestWithJson)
        status(result) shouldBe Status.OK
      }
    }
    "return internal server error if something wrong with response status not 200" in {
      forAll(nonEmptyPayload) { payload =>
        stubFor(
          post(urlPathEqualTo("/pega-proof-of-concept-stubs/submit-payload"))
            .willReturn(aResponse().withStatus(204))
        )
        val fakeRequestWithJson = FakeRequest().withBody(Json.toJsObject(payload))

        val result = controller.payload()(fakeRequestWithJson)
        status(result) shouldBe Status.INTERNAL_SERVER_ERROR
      }
    }
  }

}
