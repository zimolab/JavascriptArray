package jsarray.base

interface JsArraySortFunction<in T> {
    fun compare(a: T, b: T): Boolean
}

interface UnTypedJsArraySortFunction: JsArraySortFunction<Any?>