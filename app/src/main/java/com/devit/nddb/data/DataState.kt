/*
* Copyright 2021 Wajahat Karim (https://wajahatkarim.com)
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     https://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package com.wajahatkarim3.imagine.data

/**
 * A Sealed class to fetch data from server which will be either data or the error.
 * @author Wajahat Karim
 */
sealed class DataState<T> {
    data class Success<T>(val data: T) : DataState<T>()
    data class Error<T>(val message: String) : DataState<T>()

    companion object {
        fun <T> success(data: T) = Success<T>(data)
        fun <T> error(message: String) = Error<T>(message)
    }
}
