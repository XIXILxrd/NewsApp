package com.example.news.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.news.main.models.State
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import javax.inject.Provider

@HiltViewModel
internal class NewsMainViewModel @Inject constructor(
    getAllArticlesUseCase: Provider<GetAllArticlesUseCase>
) : ViewModel() {

    val state: StateFlow<State> = getAllArticlesUseCase.get().invoke(query = "android")
        .map {
            it.toState()
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, State.None)
}
