package es.masopego.cactus.feedback.infrastructure.http.dto

data class FeedbackErrorResponse(
    val code: Int,
    val message: String
)

