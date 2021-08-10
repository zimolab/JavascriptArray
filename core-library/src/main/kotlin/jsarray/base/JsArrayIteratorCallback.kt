package jsarray.base

interface JsArrayIteratorCallback<in T, out R> {
    fun call(currentValue: T?, index: Int, total: T?, arr: Any?): R
}

typealias TypedIteratorCallback<T, R> = JsArrayIteratorCallback<T, R>

interface UnTypedIteratorCallback<out R>: JsArrayIteratorCallback<Any?, R> {
    override fun call(currentValue: Any?, index: Int, total: Any?, arr: Any?): R
}