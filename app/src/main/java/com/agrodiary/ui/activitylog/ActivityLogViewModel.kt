package com.agrodiary.ui.activitylog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.agrodiary.data.local.entity.ActivityLogEntity
import com.agrodiary.data.repository.ActivityLogRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class ActivityLogViewModel @Inject constructor(
    repository: ActivityLogRepository
) : ViewModel() {

    val logs: StateFlow<List<ActivityLogEntity>> =
        repository.getAllLogs()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}
