package com.example.nearby.nearbylist.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.nearby.R
import com.example.nearby.nearbylist.model.view.NearbyEntity
import com.example.nearby.nearbylist.util.isEmpty
import com.example.nearby.nearbylist.util.shimmerEffect
import com.example.nearby.nearbylist.viewmodel.NearbyListUiState
import com.example.nearby.nearbylist.viewmodel.NearbyListViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph

@RootNavGraph(start = true)
@Destination
@Composable
fun NearbyListScreen(nearbyListViewModel: NearbyListViewModel = hiltViewModel()) {

    val uiState by nearbyListViewModel.uiState.collectAsStateWithLifecycle()
    val nearbyListPager = nearbyListViewModel.nearbyListPager.collectAsLazyPagingItems()
    val sliderPosition by nearbyListViewModel.sliderPosition.collectAsStateWithLifecycle()
    val range by nearbyListViewModel.range.collectAsStateWithLifecycle()

    val uriHandler = LocalUriHandler.current

    LaunchedEffect(nearbyListPager.loadState.refresh) {
        if (nearbyListPager.loadState.refresh is LoadState.NotLoading) {
            nearbyListViewModel.updateUiState(NearbyListUiState.NetworkLoaded)
        }
    }

    RenderNearbyListScreen(
        uiState = uiState,
        nearbyListPager = nearbyListPager,
        sliderPosition = sliderPosition,
        onSliderPositionChanged = { nearbyListViewModel.updateSliderPosition(it) },
        range = range,
        onNearbyListItemClicked = { uriHandler.openUri(it.url) }
    )
}

@Composable
private fun RenderNearbyListScreen(
    uiState: NearbyListUiState,
    nearbyListPager: LazyPagingItems<NearbyEntity>,
    sliderPosition: Float,
    onSliderPositionChanged: (Float) -> Unit,
    range: String,
    onNearbyListItemClicked: (NearbyEntity) -> Unit
) {
    Scaffold(
        topBar = { RenderNearbyListTopBar() },
        content = {
            RenderNearbyContent(
                uiState = uiState,
                modifier = Modifier.padding(it),
                nearbyListPager = nearbyListPager,
                onNearbyListItemClicked = onNearbyListItemClicked
            )
        },
        bottomBar = {
            RenderNearbyListBottomBar(
                sliderPosition = sliderPosition,
                onSliderPositionChanged = onSliderPositionChanged,
                range = range
            )
        })
}

@Composable
private fun RenderNearbyListTopBar() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colorScheme.onSurfaceVariant)
    ) {
        TextField(
            value = "", onValueChange = {},
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 28.dp, start = 16.dp, end = 16.dp, bottom = 16.dp)
                .background(color = Color.White),
            singleLine = true,
            maxLines = 1,
            placeholder = {
                Text(text = "Search")
            }
        )
    }
}

@Composable
private fun RenderNearbyListBottomBar(
    sliderPosition: Float,
    onSliderPositionChanged: (Float) -> Unit,
    range: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colorScheme.onSurfaceVariant)
    ) {
        Column {

            Spacer(modifier = Modifier.height(16.dp))

            Slider(
                value = sliderPosition,
                onValueChange = onSliderPositionChanged,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = "Restaurants within $range",
                modifier = Modifier.align(Alignment.CenterHorizontally),
                color = Color.White
            )

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun RenderNearbyContent(
    uiState: NearbyListUiState,
    modifier: Modifier,
    nearbyListPager: LazyPagingItems<NearbyEntity>,
    onNearbyListItemClicked: (NearbyEntity) -> Unit
) {
    when (uiState) {
        NearbyListUiState.Loading -> RenderNearbyListLoadingState(modifier = modifier)
        is NearbyListUiState.CacheLoaded -> RenderNearbyListLoadedState(
            modifier = modifier,
            nearbyList = uiState.nearbyEntities,
            onNearbyListItemClicked = onNearbyListItemClicked
        )

        NearbyListUiState.NetworkLoaded -> RenderNearbyListLoadedState(
            modifier = modifier,
            nearbyListPager = nearbyListPager,
            onNearbyListItemClicked = onNearbyListItemClicked
        )
    }
}

@Composable
private fun RenderNearbyListLoadingState(modifier: Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(all = 16.dp)
    ) {

        Spacer(modifier = Modifier.height(16.dp))

        for (i in 1..8) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(68.dp)
                    .shimmerEffect()
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
private fun RenderNearbyListLoadedState(
    modifier: Modifier,
    nearbyList: List<NearbyEntity>,
    onNearbyListItemClicked: (NearbyEntity) -> Unit
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(all = 16.dp)
    ) {
        items(nearbyList.size) { index ->
            NearbyListItemView(
                modifier = Modifier.padding(bottom = 16.dp),
                nearbyEntity = nearbyList[index],
                onNearbyListItemClicked = onNearbyListItemClicked
            )

        }
    }
}

@Composable
private fun RenderNearbyListLoadedState(
    modifier: Modifier,
    nearbyListPager: LazyPagingItems<NearbyEntity>,
    onNearbyListItemClicked: (NearbyEntity) -> Unit
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(all = 16.dp)
    ) {
        items(nearbyListPager.itemCount) { index ->
            nearbyListPager[index]?.let { nearbyEntity ->
                NearbyListItemView(
                    modifier = Modifier.padding(bottom = 16.dp),
                    nearbyEntity = nearbyEntity,
                    onNearbyListItemClicked = onNearbyListItemClicked
                )
            }
        }

        if (nearbyListPager.isEmpty()) {
            item { Text(text = "It looks like we couldn't find any nearby places.\nTry changing the range.") }
        }

        when (nearbyListPager.loadState.refresh) {
            is LoadState.Loading -> {
                item {
                    Column(
                        modifier = modifier
                            .fillMaxWidth()
                    ) {
                        for (i in 1..8) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(68.dp)
                                    .shimmerEffect()
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }
                }
            }

            else -> Unit
        }
    }
}

@Composable
private fun NearbyListItemView(
    modifier: Modifier,
    nearbyEntity: NearbyEntity,
    onNearbyListItemClicked: (NearbyEntity) -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onNearbyListItemClicked(nearbyEntity) },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = R.drawable.baseline_location_city_24),
            contentDescription = "",
            modifier = Modifier
                .background(
                    shape = RoundedCornerShape(4.dp),
                    color = MaterialTheme.colorScheme.onBackground
                )
                .size(48.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column {
            Text(
                text = nearbyEntity.name,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.primary,
                maxLines = 1
            )

            Text(
                text = nearbyEntity.location,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.tertiary,
                maxLines = 2
            )
        }
    }
}