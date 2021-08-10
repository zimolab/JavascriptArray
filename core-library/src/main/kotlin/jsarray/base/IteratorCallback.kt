package jsarray.base

interface IteratorCallback<in T, out R> {
    fun call(currentValue: T?, index: Int, total: T?, arr: Any?): R
}

typealias TypedIteratorCallback<T, R> = IteratorCallback<T, R>

interface UnTypedIteratorCallback<out R>: IteratorCallback<Any?, R> {
    override fun call(currentValue: Any?, index: Int, total: Any?, arr: Any?): R
}