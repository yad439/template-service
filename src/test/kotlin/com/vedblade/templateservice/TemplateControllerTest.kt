package com.vedblade.templateservice

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

internal class TemplateControllerTest : WordSpec() {
	init {
		val service = mockk<TemplateService>(relaxUnitFun = true)
		val controller = TemplateController(service)

		"\"upload\" endpoint" When {
			val dto = TemplateDto("someId", "a template", listOf("a", "b"))

			"input is correct" should {

				"save new template"{
					controller.upload(dto)

					verify { service.save(dto) }
					confirmVerified(service)
				}
			}
			"input is incorrect" should {
				every { service.save(dto) } throws TemplateException("a message")

				"throw response exception"{
					val exception = shouldThrow<ResponseStatusException> {
						controller.upload(dto)
					}
					exception.status shouldBe HttpStatus.BAD_REQUEST
				}
			}
		}
		"\"send\" endpoint" When {
			val request = SendRequestDto("id0", mapOf())
			"input is correct" should {
				"send messages"{
					controller.send(request)

					verify { service.send(request.templateId, request.variables) }
					confirmVerified(service)
				}
			}
			"template is missing" should {
				every { service.send(request.templateId, request.variables) } throws NoSuchElementException()

				"throw response exception"{
					val exception = shouldThrow<ResponseStatusException> {
						controller.send(request)
					}
					exception.status shouldBe HttpStatus.BAD_REQUEST
				}
			}
			"template is invalid" should {
				every { service.send(request.templateId, request.variables) } throws TemplateException("a message")

				"throw response exception"{
					val exception = shouldThrow<ResponseStatusException> {
						controller.send(request)
					}
					exception.status shouldBe HttpStatus.BAD_REQUEST
				}
			}
		}

		afterTest {
			clearAllMocks()
		}
	}
}