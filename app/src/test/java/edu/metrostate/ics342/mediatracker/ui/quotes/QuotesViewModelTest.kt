package edu.metrostate.ics342.mediatracker.ui.quotes

import android.app.Application
import edu.metrostate.ics342.mediatracker.data.model.Media
import edu.metrostate.ics342.mediatracker.data.model.Quote
import edu.metrostate.ics342.mediatracker.data.network.DefaultMediaRepository
import edu.metrostate.ics342.mediatracker.data.network.QuotePage
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
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class QuotesViewModelTest {

    private val dispatcher =
        StandardTestDispatcher()

    private val application =
        mockk<Application>(
            relaxed = true
        )

    private val repository =
        mockk<DefaultMediaRepository>()

    private val quote =
        Quote(
            id = 1,
            userId = "other-user",
            mediaId = 409,
            quoteText =
                "Test public quote",
            pageNumber = 10,
            isPublic = true,
            likeCount = 0,
            createdAt =
                "2026-08-06T00:00:00Z",
            media =
                Media(
                    id = 409,
                    mediaType = "book",
                    title = "Test Book",
                    author = "Test Author"
                )
        )

    @Before
    fun setUp() {
        Dispatchers.setMain(
            dispatcher
        )

        coEvery {
            repository.getQuotes(
                publicOnly = null,
                after = null
            )
        } returns QuotePage(
            items = listOf(quote),
            nextCursor = null,
            hasMore = false
        )

        coEvery {
            repository.getQuotes(
                publicOnly = true,
                after = null
            )
        } returns QuotePage(
            items = listOf(quote),
            nextCursor = null,
            hasMore = false
        )

        coEvery {
            repository.likeQuote(1)
        } returns Unit

        coEvery {
            repository.unlikeQuote(1)
        } returns Unit
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `like and unlike public quote updates state`() =
        runTest(dispatcher) {

            val viewModel =
                QuotesViewModel(
                    application = application,
                    repository = repository
                )

            /*
             * Finish initial My Quotes load.
             */
            dispatcher.scheduler
                .advanceUntilIdle()

            viewModel.loadQuotes(
                QuotesMode.PUBLIC
            )

            dispatcher.scheduler
                .advanceUntilIdle()

            /*
             * LIKE
             */
            viewModel.toggleLike(1)

            val likedState =
                viewModel.uiState.value
                        as QuotesUiState.Success

            assertTrue(
                1 in
                        likedState.likedQuoteIds
            )

            assertEquals(
                1,
                likedState.quotes
                    .first()
                    .likeCount
            )

            dispatcher.scheduler
                .advanceUntilIdle()

            coVerify(
                exactly = 1
            ) {
                repository.likeQuote(1)
            }

            /*
             * UNLIKE
             */
            viewModel.toggleLike(1)

            val unlikedState =
                viewModel.uiState.value
                        as QuotesUiState.Success

            assertFalse(
                1 in
                        unlikedState
                            .likedQuoteIds
            )

            assertEquals(
                0,
                unlikedState.quotes
                    .first()
                    .likeCount
            )

            dispatcher.scheduler
                .advanceUntilIdle()

            coVerify(
                exactly = 1
            ) {
                repository.unlikeQuote(1)
            }
        }
}