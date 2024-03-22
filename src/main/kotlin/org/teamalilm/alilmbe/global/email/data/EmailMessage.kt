package org.teamalilm.alilmbe.global.email.data

data class EmailMessage(
    val from: String = "Team Alilm",
    val to: String,
    val subject: String,
    val text: String
) {

}