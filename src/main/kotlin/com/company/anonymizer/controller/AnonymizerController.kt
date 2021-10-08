package com.company.anonymizer.controller

import com.company.anonymizer.service.Anonymizer
import org.springframework.beans.factory.ObjectFactory
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid
import javax.validation.constraints.NotEmpty

@RestController
class AnonymizerController(private val anonymizerFactory: ObjectFactory<Anonymizer>) {

    @PostMapping("/anonymize")
    fun anonymize(@Valid @RequestBody request: TextModel): TextModel {
        return TextModel(anonymizerFactory.getObject().anonymize(request.text))
    }

}

data class TextModel(@NotEmpty val text: String)
