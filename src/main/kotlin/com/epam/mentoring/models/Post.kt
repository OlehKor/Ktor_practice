package com.epam.mentoring.models

data class Post(
    val id: Int = 0,
    val userId: Int,
    val title: String,
    val body: String
) 