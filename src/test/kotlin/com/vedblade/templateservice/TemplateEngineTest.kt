package com.vedblade.templateservice

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe

internal class TemplateEngineTest : WordSpec() {
	init {
		val engine = TemplateEngine()

		"\"process\" function" When {
			"template in correct" should {
				val template = "\$var0\$ abc \$var1\$ test \$var2\$"
				val values = mapOf("var0" to "smth", "var1" to "\$another", "var2" to "and another")

				"substitute all variables"{
					val result = engine.process(template, values)
					result shouldBe "smth abc \$another test and another"
				}
			}

			"variable data is incomplete" should {
				val template = "\$var0\$ abc \$var1\$ test \$var2\$"
				val values = mapOf("var0" to "smth", "var2" to "and another")

				"throw a TemplateException"{
					val exception = shouldThrow<TemplateException> {
						engine.process(template, values)
					}
					exception.message shouldBe "Could not get value for variable \"var1\""
				}
			}

			"template has unpaired '\$'" should {
				val template = "\$var0\$ abc \$var1\$ test \$var2"
				val values = mapOf("var0" to "smth", "var1" to "\$another", "var2" to "and another")

				"throw a TemplateException"{
					shouldThrow<TemplateException> {
						engine.process(template, values)
					}
				}
			}
		}

		"\"validate\" function" When {
			"template in correct" should {
				val template = "\$var0\$ abc \$var1\$ test \$var2\$"

				"return VALID"{
					val result = engine.validate(template)
					result shouldBe TemplateEngine.ValidationResult.VALID
				}
			}

			"template has unpaired '\$'" should {
				val template = "\$var0\$ abc \$var1 test \$var2\$"

				"return UNPAIRED_INTERPOLATION_CHARACTER"{
					val result = engine.validate(template)

					result shouldBe TemplateEngine.ValidationResult.UNPAIRED_INTERPOLATION_CHARACTER
				}
			}
		}
	}
}