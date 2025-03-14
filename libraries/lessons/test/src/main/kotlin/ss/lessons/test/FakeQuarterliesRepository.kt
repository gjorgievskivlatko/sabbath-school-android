/*
 * Copyright (c) 2024. Adventech <info@adventech.io>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package ss.lessons.test

import app.ss.models.PublishingInfo
import app.ss.models.QuarterlyGroup
import app.ss.models.SSQuarterly
import app.ss.models.SSQuarterlyInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
import ss.lessons.api.repository.QuarterliesRepository

class FakeQuarterliesRepository : QuarterliesRepository {

    var quarterliesMap: MutableMap<Pair<String?, QuarterlyGroup?>, Flow<Result<List<SSQuarterly>>>> = mutableMapOf()
    var quarterlyInfoMap: MutableMap<String, Result<SSQuarterlyInfo>> = mutableMapOf()
    var publishingInfoFlow: Flow<Result<PublishingInfo?>> = emptyFlow()

    override suspend fun getQuarterlyInfo(index: String): Result<SSQuarterlyInfo> {
        return quarterlyInfoMap[index]!!
    }

    override fun getQuarterlies(languageCode: String?, group: QuarterlyGroup?): Flow<Result<List<SSQuarterly>>> {
        return quarterliesMap[languageCode to group]!!
    }

    override fun getPublishingInfo(): Flow<Result<PublishingInfo?>> {
        return publishingInfoFlow
    }
}
