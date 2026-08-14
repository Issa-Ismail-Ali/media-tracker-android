package edu.metrostate.ics342.mediatracker

import android.app.Application
import edu.metrostate.ics342.mediatracker.data.model.LibraryItem
import edu.metrostate.ics342.mediatracker.data.model.LibraryStatus
import edu.metrostate.ics342.mediatracker.data.model.Media
import edu.metrostate.ics342.mediatracker.data.network.DefaultMediaRepository
import edu.metrostate.ics342.mediatracker.data.network.LibraryPage
import edu.metrostate.ics342.mediatracker.ui.library.LibraryUiState
import edu.metrostate.ics342.mediatracker.ui.library.LibraryViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class LibraryViewModelTest {

    private val dispatcher =
        StandardTestDispatcher()

    private val application =
        mockk<Application>(relaxed = true)

    private val repository =
        mockk<DefaultMediaRepository>()

    private val testItem =
        LibraryItem(
            userId = "user-1",
            mediaId = 1,
            status = LibraryStatus.WANT_TO,
            addedAt = "2026-01-01T00:00:00Z",
            updatedAt = "2026-01-01T00:00:00Z",
            media = Media(
                id = 1,
                mediaType = "book",
                title = "Test Book",
                author = "Test Author"
            )
        )

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)

        coEvery {
            repository.getLibrary(
                status = LibraryStatus.WANT_TO,
                after = null
            )
        } returns LibraryPage(
            items = listOf(testItem),
            nextCursor = null,
            hasMore = false
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `failed remove restores item to library`() =
        runTest(dispatcher) {

            coEvery {
                repository.removeFromLibrary(1)
            } throws IOException("Network failed")

            val viewModel =
                LibraryViewModel(
                    application = application,
                    repository = repository
                )

            // Complete initial GET /library
            dispatcher.scheduler.advanceUntilIdle()

            val initialState =
                viewModel.uiState.value
                        as LibraryUiState.Success

            Assert.assertTrue(
                initialState.items.any {
                    it.mediaId == 1
                }
            )

            viewModel.removeItem(1)

            // Item disappears immediately
            val optimisticState =
                viewModel.uiState.value
                        as LibraryUiState.Success

            Assert.assertFalse(
                optimisticState.items.any {
                    it.mediaId == 1
                }
            )

            // Run failed DELETE and rollback
            dispatcher.scheduler.advanceUntilIdle()

            val rolledBackState =
                viewModel.uiState.value
                        as LibraryUiState.Success

            Assert.assertTrue(
                rolledBackState.items.any {
                    it.mediaId == 1
                }
            )

            Assert.assertEquals(
                "Couldn't remove item. Try again.",
                rolledBackState.actionError
            )

            coVerify(exactly = 1) {
                repository.removeFromLibrary(1)
            }
        }
}