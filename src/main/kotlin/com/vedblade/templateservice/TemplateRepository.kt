package com.vedblade.templateservice

import org.springframework.data.repository.CrudRepository

interface TemplateRepository : CrudRepository<TemplateEntity, String>