package com.github.newscalculator.di.modules

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DatabaseModule {
    @Provides
    @Singleton
    fun provideFirebaseStoreReference(): FirebaseFirestore {
        return Firebase.firestore
    }
}