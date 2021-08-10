package jsarray.base

interface SortFunction<in T> {
    fun compare(a: T, b: T): Boolean
}

interface UnTypedSortFunction: SortFunction<Any?>