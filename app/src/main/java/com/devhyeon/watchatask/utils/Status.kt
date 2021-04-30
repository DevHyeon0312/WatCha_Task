package com.devhyeon.watchatask.utils

/** 상태에 따라 처리하기 위해 작성 */
sealed class Status<T>(val data: T? = null, val errorCode: Int? = null, val errorMessage: String? = null) {
    class Run<T>(data: T? = null)                           : Status<T>(data)
    class Success<T>(data: T)                               : Status<T>(data)
    class Failure<T>(errorCode: Int, errorMessage: String)  : Status<T>(null, errorCode, errorMessage)
}