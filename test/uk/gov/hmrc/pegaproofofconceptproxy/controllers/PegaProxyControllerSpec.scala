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
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import org.scalatestplus.scalacheck.ScalaCheckDrivenPropertyChecks
import play.api.http.{HeaderNames, Status}
import play.api.libs.json.Json
import play.api.test.FakeRequest
import play.api.test.Helpers.{await, contentAsJson, defaultAwaitTimeout, status}
import uk.gov.hmrc.http.UpstreamErrorResponse
import uk.gov.hmrc.http.test.ExternalWireMockSupport
import uk.gov.hmrc.pegaproofofconceptproxy.testsupport.FakeApplicationProvider

class PegaProxyControllerSpec extends AnyWordSpec with Matchers with GuiceOneAppPerSuite with ExternalWireMockSupport with FakeApplicationProvider
  with ScalaCheckDrivenPropertyChecks {

  private val controller = app.injector.instanceOf[PegaProxyController]

  "PegaProxyController" when {

    "handling start case" should {

      val url: String = "/pega-proof-of-concept-stubs/start-case"
      val tokenUrl: String = "/pega-proof-of-concept-stubs/get-token"

      "return ok if pega return ok" in {

        stubFor(
          post(urlPathEqualTo(tokenUrl))
            .willReturn(aResponse().withStatus(200).withBody(
              """
                |{
                |  "access_token": "dToxMjM0",
                |  "token_type": "bearer",
                |  "expires_in": 3600
                |}
                |""".stripMargin
            ))
        )

        stubFor(
          post(urlPathEqualTo(url))
            .willReturn(aResponse().withStatus(201).withBody(
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
        status(result) shouldBe Status.CREATED

        verify(
          postRequestedFor(urlPathEqualTo(url))
            .withHeader(HeaderNames.AUTHORIZATION, equalTo("Bearer dToxMjM0"))
        )
      }

      "return an error status if something wrong with response status not 200" in {

        stubFor(
          post(urlPathEqualTo(tokenUrl))
            .willReturn(aResponse().withStatus(200).withBody(
              """
                |{
                |  "access_token": "dToxMjM0",
                |  "token_type": "bearer",
                |  "expires_in": 3600
                |}
                |""".stripMargin
            ))
        )

        stubFor(
          post(urlPathEqualTo(url))
            .willReturn(aResponse().withStatus(503))
        )

        val exception = intercept[UpstreamErrorResponse](await(controller.startCase()(FakeRequest())))

        exception.statusCode shouldBe Status.SERVICE_UNAVAILABLE
      }
    }

    "handling get case" should {

      val caseId = "beans"
      val url: String = s"/pega-proof-of-concept-stubs/case/$caseId"
      val tokenUrl: String = "/pega-proof-of-concept-stubs/get-token"

      "return ok if pega returns ok" in {

        val responseJson = Json.parse(
          """
            |{
            |  "a":"b"
            |}
            |""".stripMargin
        )

        stubFor(
          post(urlPathEqualTo(tokenUrl))
            .willReturn(aResponse().withStatus(200).withBody(
              """
                |{
                |  "access_token": "dToxMjM0",
                |  "token_type": "bearer",
                |  "expires_in": 3600
                |}
                |""".stripMargin
            ))
        )

        stubFor(
          get(urlPathEqualTo(url))
            .willReturn(aResponse().withStatus(200).withBody(responseJson.toString()))
        )

        val result = controller.getCase(caseId)(FakeRequest())
        status(result) shouldBe Status.OK
        contentAsJson(result) shouldBe responseJson

        verify(
          getRequestedFor(urlPathEqualTo(url))
            .withHeader(HeaderNames.AUTHORIZATION, equalTo("Bearer dToxMjM0"))
        )
      }

      "return an error status if something wrong with response status not 200" in {

        stubFor(
          post(urlPathEqualTo(tokenUrl))
            .willReturn(aResponse().withStatus(200).withBody(
              """
                |{
                |  "access_token": "dToxMjM0",
                |  "token_type": "bearer",
                |  "expires_in": 3600
                |}
                |""".stripMargin
            ))
        )

        stubFor(
          get(urlPathEqualTo(url))
            .willReturn(aResponse().withStatus(400))
        )

        val exception = intercept[UpstreamErrorResponse](await(controller.getCase(caseId)(FakeRequest())))
        exception.statusCode shouldBe Status.BAD_REQUEST
      }
    }

  }

}
