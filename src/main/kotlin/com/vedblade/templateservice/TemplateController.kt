package com.vedblade.templateservice

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import javax.validation.Valid

@RestController
class TemplateController(private val templateService: TemplateService) {

	@PostMapping("/upload")
	fun upload(@Valid @RequestBody templateDto: TemplateDto) {
		try {
			templateService.save(templateDto)
		} catch (e: TemplateException) {
			throw ResponseStatusException(HttpStatus.BAD_REQUEST, e.message)
		}
	}

	@PostMapping("/send")
	fun send(@Valid @RequestBody data: SendRequestDto) {
		try {
			templateService.send(data.templateId, data.variables)
		} catch (e: TemplateException) {
			throw ResponseStatusException(HttpStatus.BAD_REQUEST, e.message)
		}
	}
}