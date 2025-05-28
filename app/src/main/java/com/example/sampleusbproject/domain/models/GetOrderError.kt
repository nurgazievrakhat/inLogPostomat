package com.example.sampleusbproject.domain.models

sealed interface GetOrderError {
    object NotFound: GetOrderError
    object Unexpected: GetOrderError
}