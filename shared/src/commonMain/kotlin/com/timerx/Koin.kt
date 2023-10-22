package com.timerx

import com.timerx.repository.TimerRepository
import com.timerx.repository.TimerRepository.TimerRepositoryImpl
import org.koin.dsl.module

fun shareModule() = module {
    single<TimerRepository> { TimerRepositoryImpl() }
}