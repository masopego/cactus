package es.masopego.cactus.feedback.infrastructure.http.dto

data class CreateFeedbackRequest(
    val rating: Short,
    val comment: String
)

