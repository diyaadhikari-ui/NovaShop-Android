package com.novashop.app.data.model

import com.stripe.android.Stripe
import android.content.Context

object StripeConfig {

    private const val STRIPE_PUBLISHABLE_KEY =
            "pk_test_51TlAQIHF4RypNg1B6UB08wc6s3uvEycE9OMZdpixPevj9rt2EoqpJmbYTZZ4pV8Ee0U2eyPzK4MTvFRtnGAeFtA400MkVCDlzK"

    const val BACKEND_URL = "http://10.0.2.2:3000/api"

    const val CREATE_PAYMENT_INTENT_URL = "$BACKEND_URL/create-payment-intent"
    const val CONFIRM_PAYMENT_URL = "$BACKEND_URL/confirm-payment"

    private var stripeInstance: Stripe? = null

    fun initialize(context: Context) {
        if (stripeInstance == null) {
            stripeInstance = Stripe(context, STRIPE_PUBLISHABLE_KEY)
        }
    }

    fun getInstance(context: Context): Stripe {
        if (stripeInstance == null) {
            initialize(context)
        }
        return stripeInstance ?: Stripe(context, STRIPE_PUBLISHABLE_KEY)
    }

    fun getPublishableKey(): String = STRIPE_PUBLISHABLE_KEY
}