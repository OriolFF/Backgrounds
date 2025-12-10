package com.uriolus.backgrounds

import android.app.Application
import com.uriolus.feature.backgrounds.di.backgroundsModule
import com.uriolus.feature.shaders.di.shadersModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

/**
 * Application class for initializing Koin DI
 */
class BackgroundsApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        startKoin {
            androidLogger(Level.DEBUG)
            androidContext(this@BackgroundsApplication)
            modules(
                backgroundsModule,
                shadersModule
                // Add more feature modules here as they're created
            )
        }
    }
}
