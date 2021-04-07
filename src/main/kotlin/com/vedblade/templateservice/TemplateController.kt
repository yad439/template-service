package com.vedblade.templateservice

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
class TemplateController(private val templateService: TemplateService) {

	@PostMapping("/upload")
	fun upload(@Valid @RequestBody templateDto: TemplateDto) {
		templateService.save(templateDto)
	}

	@PostMapping("/send")
	fun send(@Valid @RequestBody data: SendRequestDto) {
		templateService.send(data.templateId, data.variables)
	}
}