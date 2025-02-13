/*
 * Copyright (c) 2023. Adventech <info@adventech.io>
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

package ss.lessons.api

import app.ss.models.media.SSAudio
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import ss.lessons.model.VideosInfoModel

interface SSMediaApi {

    @GET("api/v1/{lang}/quarterlies/{quarterly_id}/audio.json")
    suspend fun getAudio(
        @Path("lang") language: String,
        @Path("quarterly_id") quarterlyId: String
    ): Response<List<SSAudio>>

    @GET("api/v2/video/languages.json")
    suspend fun getVideoLanguages(): Response<List<String>>

    @GET("api/v2/{lang}/video/latest.json")
    suspend fun getLatestVideo(
        @Path("lang") language: String,
    ): Response<List<VideosInfoModel>>
}
