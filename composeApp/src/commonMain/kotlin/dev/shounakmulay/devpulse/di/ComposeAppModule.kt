package dev.shounakmulay.devpulse.di

import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module

@Module
@ComponentScan(
    "dev.shounakmulay.devpulse.theme",
    "dev.shounakmulay.devpulse.startup"
)
class ComposeAppModule
