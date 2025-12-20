package com.agrodiary

import com.agrodiary.data.repository.AnimalRepository
import com.agrodiary.ui.animals.AnimalsViewModel
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AnimalsViewModelTest {

    private lateinit var viewModel: AnimalsViewModel
    private val repository: AnimalRepository = mockk(relaxed = true)
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = AnimalsViewModel(repository)
    }

    @Test
    fun `initial search query is empty`() {
        assertEquals("", viewModel.searchQuery.value)
    }

    @Test
    fun `setSearchQuery updates searchQuery`() {
        val query = "Cow"
        viewModel.setSearchQuery(query)
        assertEquals(query, viewModel.searchQuery.value)
    }
}
