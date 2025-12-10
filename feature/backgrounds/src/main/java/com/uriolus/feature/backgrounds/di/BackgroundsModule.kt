package com.uriolus.feature.backgrounds.di

import com.uriolus.feature.backgrounds.BackgroundsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

/**
 * Koin module for Backgrounds feature
 */
val backgroundsModule = module {
    viewModel { BackgroundsViewModel() }
}
