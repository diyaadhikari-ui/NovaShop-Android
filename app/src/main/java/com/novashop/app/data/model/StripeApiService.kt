package com.novashop.app.data.model

import retrofit2.http.Body
import retrofit2.http.POST
import com.google.gson.annotations.SerializedName
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

interface StripeApiService {

    @POST("create-payment-intent")
    suspend fun createPaymentIntent(
        @Body request: CreatePaymentIntentRequest
    ): CreatePaymentIntentResponse

    @POST("confirm-payment")
    suspend fun confirmPayment(
        @Body request: ConfirmPaymentRequest
    ): ConfirmPaymentResponse
}

data class CreatePaymentIntentRequest(
    @SerializedName("amount")
    val amount: Long,

    @SerializedName("currency")
    val currency: String = "npr",

    @SerializedName("orderId")
    val orderId: String,

    @SerializedName("userId")
    val userId: String,

    @SerializedName("description")
    val description: String = "",

    @SerializedName("metadata")
    val metadata: Map<String, String> = emptyMap()
)

data class CreatePaymentIntentResponse(
    @SerializedName("client_secret")
    val clientSecret: String,

    @SerializedName("payment_intent_id")
    val paymentIntentId: String,

    @SerializedName("success")
    val success: Boolean,

    @SerializedName("message")
    val message: String = ""
)

data class ConfirmPaymentRequest(
    @SerializedName("payment_intent_id")
    val paymentIntentId: String,

    @SerializedName("orderId")
    val orderId: String,

    @SerializedName("userId")
    val userId: String,

    @SerializedName("status")
    val status: String
)

data class ConfirmPaymentResponse(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("message")
    val message: String,

    @SerializedName("order_id")
    val orderId: String? = null,

    @SerializedName("payment_status")
    val paymentStatus: String = "pending"
)

object StripeApiServiceFactory {
    private var apiService: StripeApiService? = null

    fun create(baseUrl: String = StripeConfig.BACKEND_URL): StripeApiService {
        if (apiService == null) {
            apiService = Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(StripeApiService::class.java)
        }
        return apiService!!
    }
}