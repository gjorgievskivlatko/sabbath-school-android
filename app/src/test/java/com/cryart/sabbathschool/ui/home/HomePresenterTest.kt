package com.cryart.sabbathschool.ui.home

import android.content.Context
import androidx.core.os.bundleOf
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.ss.auth.test.FakeAuthRepository
import app.ss.models.auth.SSUser
import com.cryart.sabbathschool.core.navigation.Destination
import com.cryart.sabbathschool.reminder.DailyReminderManager
import com.slack.circuit.test.FakeNavigator
import com.slack.circuit.test.test
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.junit.Test
import org.junit.runner.RunWith
import ss.libraries.circuit.navigation.LegacyDestination
import ss.libraries.circuit.navigation.LoginScreen
import ss.libraries.circuit.navigation.QuarterliesScreen
import ss.misc.SSConstants
import ss.prefs.api.test.FakeSSPrefs

@RunWith(AndroidJUnit4::class)
class HomePresenterTest {

    private val fakeNavigator = FakeNavigator(HomeScreen)
    private val fakeAuthRepository = FakeAuthRepository()
    private val fakePrefs = FakeSSPrefs()
    private val fakeDailyReminderManager = FakeDailyReminderManager()

    private val underTest = HomePresenter(
        navigator = fakeNavigator,
        ssPrefs = fakePrefs,
        authRepository = fakeAuthRepository,
        dailyReminderManager = fakeDailyReminderManager,
    )

    @Test
    fun `present - user authed - schedule reminder`() = runTest {
        fakeAuthRepository.userDelegate = { Result.success(SSUser.fake()) }
        with(fakePrefs) {
            quarterlyIndexDelegate = { null }
            reminderEnabledDelegate = { true }
            reminderScheduledDelegate = { false }
        }

        underTest.test {
            awaitItem()

            fakeDailyReminderManager.reminderScheduled shouldBeEqualTo true
            fakeNavigator.awaitResetRoot().newRoot shouldBeEqualTo QuarterliesScreen

            ensureAllEventsConsumed()
        }
    }

    @Test
    fun `present - user authed - reminder scheduled`() = runTest {
        fakeAuthRepository.userDelegate = { Result.success(SSUser.fake()) }
        with(fakePrefs) {
            quarterlyIndexDelegate = { null }
            reminderEnabledDelegate = { true }
            reminderScheduledDelegate = { true }
        }

        underTest.test {
            awaitItem()

            fakeDailyReminderManager.reminderScheduled shouldBeEqualTo false
            fakeNavigator.awaitResetRoot().newRoot shouldBeEqualTo QuarterliesScreen

            ensureAllEventsConsumed()
        }
    }

    @Test
    fun `present - no user - go to Login`() = runTest {
        fakeAuthRepository.userDelegate = { Result.success(null) }

        underTest.test {
            awaitItem()

            fakeNavigator.awaitResetRoot().newRoot shouldBeEqualTo LoginScreen

            ensureAllEventsConsumed()
        }
    }

    @Test
    fun `present - user authed - last index available - go to Lessons`() = runTest {
        val index = "index-1"
        fakeAuthRepository.userDelegate = { Result.success(SSUser.fake()) }
        with(fakePrefs) {
            quarterlyIndexDelegate = { index }
            reminderEnabledDelegate = { false }
            isReadingLatestQuarterlyDelegate = { true }
        }

        underTest.test {
            awaitItem()

            fakeNavigator.awaitResetRoot().newRoot shouldBeEqualTo QuarterliesScreen

            val screen = fakeNavigator.awaitNextScreen() as LegacyDestination
            with(screen) {
                destination shouldBeEqualTo Destination.LESSONS
                extras?.getString(SSConstants.SS_QUARTERLY_INDEX_EXTRA) shouldBeEqualTo index
            }

            ensureAllEventsConsumed()
        }
    }

    @Test
    fun `present - user authed - last index available - not reading latest quarterly - go to Lessons`() = runTest {
        val index = "index-1"
        fakeAuthRepository.userDelegate = { Result.success(SSUser.fake()) }
        with(fakePrefs) {
            quarterlyIndexDelegate = { index }
            reminderEnabledDelegate = { false }
            isReadingLatestQuarterlyDelegate = { false }
        }

        underTest.test {
            awaitItem()

            fakeNavigator.awaitResetRoot().newRoot shouldBeEqualTo QuarterliesScreen

            ensureAllEventsConsumed()
        }
    }
}

private class FakeDailyReminderManager : DailyReminderManager {
    var reminderScheduled: Boolean = false
        private set

    override fun scheduleReminder() {
        reminderScheduled = true
    }

    override fun reSchedule() {}
    override fun showNotification(context: Context) {}
    override fun cancel() {}
}
