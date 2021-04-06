package com.vedblade.templateservice

import javax.persistence.ElementCollection
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Lob

@Entity
open class TemplateEntity(
	@Id
	var id: String,
	@Lob
	var template: String,
	@ElementCollection
	var recipients: Collection<String>
)
