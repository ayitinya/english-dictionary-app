package com.ayitinya.englishdictionary.di

import android.content.Context
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.PurchasesUpdatedListener
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PlayBilling {
    @Singleton
    @Provides
    fun providesPurchasesUpdatedListener(): PurchasesUpdatedListener =
        PurchasesUpdatedListener { billingResult, purchases ->
        }

    @Singleton
    @Provides
    fun providesBillingClient(
        @ApplicationContext context: Context, purchasesUpdatedListener: PurchasesUpdatedListener
    ): BillingClient = BillingClient.newBuilder(context).setListener(purchasesUpdatedListener)
        .enablePendingPurchases().build()
}