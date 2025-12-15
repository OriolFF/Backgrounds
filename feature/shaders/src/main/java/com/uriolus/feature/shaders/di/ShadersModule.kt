package com.uriolus.feature.shaders.di

import com.uriolus.feature.shaders.ShadersViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

/**
 * Koin module for shader editor feature
 */
val shadersModule = module {
    viewModel { ShadersViewModel(context = get()) }
}
