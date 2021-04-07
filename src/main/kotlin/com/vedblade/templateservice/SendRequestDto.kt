package com.vedblade.templateservice

import javax.validation.constraints.NotNull

class SendRequestDto(@field:NotNull val templateId: String, @field:NotNull val variables: Map<String, String>)