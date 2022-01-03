package app.ss.widgets

import app.ss.lessons.data.repository.lessons.LessonsRepository
import app.ss.widgets.model.TodayWidgetModel
import app.ss.widgets.model.WeekDayWidgetModel
import app.ss.widgets.model.WeekLessonWidgetModel
import com.cryart.sabbathschool.core.extensions.coroutines.SchedulerProvider
import com.cryart.sabbathschool.core.misc.SSConstants
import com.cryart.sabbathschool.core.navigation.Destination
import com.cryart.sabbathschool.core.navigation.toUri
import kotlinx.coroutines.withContext
import timber.log.Timber

internal interface WidgetDataProvider {

    suspend fun getTodayModel(): TodayWidgetModel?

    suspend fun getWeekLessonModel(): WeekLessonWidgetModel?
}

internal class WidgetDataProviderImpl constructor(
    private val repository: LessonsRepository,
    private val schedulerProvider: SchedulerProvider
) : WidgetDataProvider {

    override suspend fun getTodayModel(): TodayWidgetModel? = withContext(schedulerProvider.default) {
        try {
            repository.getTodayRead().data?.let { data ->
                TodayWidgetModel(
                    data.title,
                    data.date,
                    data.cover,
                    Destination.READ.toUri(SSConstants.SS_LESSON_INDEX_EXTRA to data.lessonIndex)
                )
            }
        } catch (ex: Exception) {
            Timber.e(ex)
            null
        }
    }

    override suspend fun getWeekLessonModel(): WeekLessonWidgetModel? = withContext(schedulerProvider.default) {
        try {
            repository.getWeekData().data?.let { data ->
                val days = data.days.mapIndexed { index, day ->
                    WeekDayWidgetModel(
                        day.title,
                        day.date,
                        Destination.READ.toUri(
                            SSConstants.SS_LESSON_INDEX_EXTRA to data.lessonIndex,
                            SSConstants.SS_READ_POSITION_EXTRA to index.toString()
                        ),
                        day.today
                    )
                }

                WeekLessonWidgetModel(
                    data.quarterlyTitle,
                    data.lessonTitle,
                    data.cover,
                    days,
                    Destination.LESSONS.toUri(
                        SSConstants.SS_QUARTERLY_INDEX_EXTRA to data.quarterlyIndex
                    )
                )
            }
        } catch (ex: Exception) {
            Timber.e(ex)
            null
        }
    }
}
