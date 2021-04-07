package com.vedblade.templateservice

import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.ReportingPolicy

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
interface TemplateMapper {
	@Mapping(source = "templateId", target = "id")
	fun dtoToEntity(dto: TemplateDto): TemplateEntity
}