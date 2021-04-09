package com.vedblade.templateservice

import javax.persistence.ElementCollection
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.Id
import javax.persistence.Lob

@Entity
open class TemplateEntity(
	@Id
	open var id: String?,
	@Lob
	open var template: String?,
	@ElementCollection(fetch = FetchType.EAGER)
	open var recipients: Collection<String>?
)
