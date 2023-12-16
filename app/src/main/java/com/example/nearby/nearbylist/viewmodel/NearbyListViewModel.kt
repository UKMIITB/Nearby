package com.example.nearby.nearbylist.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.example.nearby.nearbylist.model.view.NearbyEntity
import com.example.nearby.nearbylist.repository.NearbyListRepository
import com.example.nearby.nearbylist.repository.NearbyPagingSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NearbyListViewModel @Inject constructor(
    private val nearbyListRepository: NearbyListRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<NearbyListUiState>(NearbyListUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _sliderPosition = MutableStateFlow(0.5f)
    val sliderPosition = _sliderPosition.asStateFlow()

    val range = sliderPosition.map {
        val roundedAndMultipliedBy10Value = String.format("%.1f", it * 10).toDouble()
        "${roundedAndMultipliedBy10Value}mi"
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), "5mi")

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    val nearbyListPager = sliderPosition.debounce(300).distinctUntilChanged()
        .flatMapLatest {
            Pager(
                config = PagingConfig(
                    pageSize = NearbyPagingSource.PER_PAGE_LIMIT,
                    prefetchDistance = NearbyPagingSource.PER_PAGE_LIMIT
                )
            ) {
                NearbyPagingSource(
                    nearbyListRepository = nearbyListRepository,
                    range = range.value
                )
            }.flow.cachedIn(scope = viewModelScope)
        }


    init {
        viewModelScope.launch(Dispatchers.IO) {
            val nearbyList = nearbyListRepository.getNearbyPlacesCachedResponse()

            if (nearbyList.isNotEmpty()) {
                updateUiState(NearbyListUiState.CacheLoaded(nearbyEntities = nearbyList))
            }
        }
    }

    fun updateUiState(uiState: NearbyListUiState) {
        _uiState.update { uiState }
    }

    fun updateSliderPosition(sliderPosition: Float) {
        _sliderPosition.update { sliderPosition }
    }
}

sealed class NearbyListUiState {
    data object Loading : NearbyListUiState()
    data class CacheLoaded(val nearbyEntities: List<NearbyEntity>) : NearbyListUiState()
    data object NetworkLoaded : NearbyListUiState()
}