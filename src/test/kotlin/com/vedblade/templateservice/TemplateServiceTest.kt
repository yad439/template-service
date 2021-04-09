package com.vedblade.templateservice

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import java.util.Optional

internal class TemplateServiceTest : WordSpec() {
	init {
		val engine = spyk(TemplateEngine())
		val sendingService = mockk<SendingService>(relaxUnitFun = true)
		val repository = mockk<TemplateRepository>()
		val mapper = mockk<TemplateMapper>()

		val service = TemplateService(repository, engine, sendingService, mapper)

		"\"save\" function" When {
			val template = TemplateDto("id0", "test", listOf("a", "b"))
			val entity = TemplateEntity(template.templateId, template.template, template.recipients)

			every { mapper.dtoToEntity(template) } returns entity
			every { repository.save(entity) } returns entity

			"template is valid" should {
				//every { engine.validate(template.template) } returns TemplateEngine.ValidationResult.VALID //https://github.com/mockk/mockk/issues/473

				service.save(template)
				"save template to repository"{
					verify { repository.save(entity) }
					confirmVerified(repository)
				}
			}

			"template is invalid" should {
				val template2 = TemplateDto("id0", "test\$", listOf("a", "b"))

				"throw TemplateException"{
					val exception = shouldThrow<TemplateException> {
						service.save(template2)
					}
					exception.message shouldBe TemplateEngine.ValidationResult.UNPAIRED_INTERPOLATION_CHARACTER.message
				}
			}
		}

		"\"send\" function" When {
			val request = SendRequestDto("id0", mapOf("d" to "v"))
			val recipients = listOf("a", "b")
			val entity = TemplateEntity("id0", "a template", recipients)
			"called" should {
				val processed = "processed"
				every { repository.findById(entity.id!!) } returns Optional.of(entity)
				every { engine.process(entity.template!!, request.variables) } returns processed

				service.send(request.templateId, request.variables)

				"send to all endpoints"{
					recipients.forEach {
						verify { sendingService.send(text = processed, endpoint = it) }
					}
					confirmVerified(sendingService)
				}
			}
		}

		afterTest {
			clearAllMocks()
		}
	}
}