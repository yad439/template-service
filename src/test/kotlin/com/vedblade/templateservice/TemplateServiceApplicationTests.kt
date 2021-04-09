package com.vedblade.templateservice

import com.fasterxml.jackson.databind.ObjectMapper
import io.kotest.core.annotation.Tags
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.optional.shouldBePresent
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import java.util.concurrent.TimeUnit

@Suppress("LeakingThis", "BlockingMethodInNonBlockingContext")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Tags("integration")
class TemplateServiceApplicationTests(val repository: TemplateRepository, val client: WebTestClient) : BehaviorSpec() {
	init {
		Given("a template service") {//todo error cases
			When("template upload is requested") {
				val request = TemplateDto(
					"internshipRequest",
					"Jetbrains Internship in \$teamName\$ team.",
					listOf("https://some.server.url/endpoint", "https://some.other.url/endpoint")
				)
				val result = client.post().uri("/upload")
					.contentType(MediaType.APPLICATION_JSON).bodyValue(request)
					.exchange()

				Then("response should be OK") {
					result.expectStatus().isOk
				}
				Then("new template should be added") {
					val newTemplate = repository.findById(request.templateId)
					newTemplate shouldBePresent {
						id shouldBe request.templateId
						template shouldBe request.template
						recipients shouldContainExactly request.recipients
					}
				}
			}

			And("recipient") {
				val testServer = MockWebServer()
				testServer.enqueue(MockResponse())
				testServer.start()

				And("a template") {
					val templateEntity = TemplateEntity(
						"internshipRequest",
						"Jetbrains Internship in \$teamName\$ team.",
						listOf(testServer.url("").toString())
					)
					repository.save(templateEntity)

					When("requested to send messages") {
						val request = SendRequestDto(templateEntity.id!!, mapOf("teamName" to "Analytics Platform"))
						val result = client.post().uri("/send")
							.contentType(MediaType.APPLICATION_JSON).bodyValue(request)
							.exchange()

						Then("status should be OK") {
							result.expectStatus().isOk
						}
						Then("message should be sent") {
							val recorded = testServer.takeRequest(5, TimeUnit.SECONDS)!!
							recorded.getHeader("Content-Type") shouldContain "application/json"
							val jsonMapper = ObjectMapper()
							val message = jsonMapper.readTree(recorded.body.readUtf8()).get("message").asText()
							message shouldBe "Jetbrains Internship in Analytics Platform team."
						}
					}
				}
				testServer.shutdown()
			}
		}
		afterContainer { repository.deleteAll() }
	}
}