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

import com.github.tomakehurst.wiremock.client.WireMock._
import org.scalatest.concurrent.Eventually.eventually
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import org.scalatestplus.scalacheck.ScalaCheckDrivenPropertyChecks
import play.api.http.{HeaderNames, Status}
import play.api.test.FakeRequest
import play.api.test.Helpers.{defaultAwaitTimeout, status}
import uk.gov.hmrc.http.test.ExternalWireMockSupport
import uk.gov.hmrc.pegaproofofconceptproxy.testsupport.FakeApplicationProvider

class PegaProxyControllerSpec extends AnyWordSpec with Matchers with GuiceOneAppPerSuite with ExternalWireMockSupport with FakeApplicationProvider
  with ScalaCheckDrivenPropertyChecks {

  private val controller = app.injector.instanceOf[PegaProxyController]
  def verifyAuthHeader(url: String): Unit = eventually {
    verify(
      postRequestedFor(urlPathEqualTo(url))
        .withHeader(HeaderNames.AUTHORIZATION, equalTo("Basic dToxMjM0"))
    )
  }

  "PegaProxyController" should {
    "return ok if stubs return ok" in {
      stubFor(
        post(urlPathEqualTo("/pega-proof-of-concept-stubs/start-case"))
          .willReturn(aResponse().withStatus(200).withBody(
            """
              |{
              |  "ID":"HMRC-DEBT-WORK A-13002",
              |  "nextAssignmentID":"ASSIGN-WORKLIST HMRC-DEBT-WORK A-13002!STARTAFFORDABILITYASSESSMENT_FLOW",
              |  "nextPageID":"Perform",
              |  "pxObjClass":"Pega-API-CaseManagement-Case"
              |}
              |""".stripMargin
          ))
      )

      val result = controller.startCase()(FakeRequest())
      status(result) shouldBe Status.OK

      verifyAuthHeader("/pega-proof-of-concept-stubs/start-case")
    }
    "return internal server error if something wrong with response status not 200" in {
      stubFor(
        post(urlPathEqualTo("/pega-proof-of-concept-stubs/start-case"))
          .willReturn(aResponse().withStatus(204))
      )

      val result = controller.startCase()(FakeRequest())
      status(result) shouldBe Status.INTERNAL_SERVER_ERROR
    }
  }

}
