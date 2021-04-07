package com.vedblade.templateservice

import org.springframework.stereotype.Service

@Service
class TemplateService(
	private val repository: TemplateRepository,
	private val engine: TemplateEngine,
	private val sendingService: SendingService,
	private val mapper: TemplateMapper
) {
	fun save(templateDto: TemplateDto) {
		val validationResult = engine.validate(templateDto.template)
		if (validationResult != TemplateEngine.ValidationResult.VALID) throw TemplateException(validationResult.message)
		val entity = mapper.dtoToEntity(templateDto)
		repository.save(entity)
	}

	fun send(templateId: String, data: Map<String, String>) {
		val template = repository.findById(templateId).orElseThrow()
		val text = engine.process(template.template!!, data)
		template.recipients!!.forEach { recipient ->
			sendingService.send(endpoint = recipient, text = text)
		}
	}
}