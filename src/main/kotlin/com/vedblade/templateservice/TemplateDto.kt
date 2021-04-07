package com.vedblade.templateservice

import javax.validation.constraints.NotNull

class TemplateDto(
	@field:NotNull
	val templateId: String,
	@field:NotNull
	val template: String,
	@field:NotNull
	val recipients: Collection<String>
)