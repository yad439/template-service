package com.vedblade.templateservice

import org.springframework.stereotype.Service

private const val INTERPOLATION_CHARACTER = '$'

@Service
class TemplateEngine {
	fun process(template: String, data: Map<String, String>): String {
		val resultBuilder = StringBuilder(template.length)
		val variableBuilder = StringBuilder()
		var state = State.TEXT

		for (character in template) {
			when (state) {
				State.TEXT     -> {
					if (character == INTERPOLATION_CHARACTER) state = State.VARIABLE
					else resultBuilder.append(character)
				}
				State.VARIABLE -> {
					if (character == INTERPOLATION_CHARACTER) {
						val variable = variableBuilder.toString()
						val value = data[variable]
							?: throw TemplateException("Could not get value for variable \"${variable}\"")
						resultBuilder.append(value)
						state = State.TEXT
					} else variableBuilder.append(character)
				}
			}
		}
		if (state == State.VARIABLE) throw TemplateException("Invalid template: unpaired '${INTERPOLATION_CHARACTER}'")
		return resultBuilder.toString()
	}

	private enum class State { TEXT, VARIABLE }
}