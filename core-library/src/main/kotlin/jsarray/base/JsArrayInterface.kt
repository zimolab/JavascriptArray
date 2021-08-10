package jsarray.base

import netscape.javascript.JSObject

interface JsArrayInterface<T> {
    companion object {
        private const val CHECK_IS_JS_ARRAY = "Array.isArray(%s)"
        fun isJsArray(jsObject: JSObject): Boolean {
            return jsObject.eval(CHECK_IS_JS_ARRAY.format("this")) == true
        }
    }

    val length: Int
    val reference: JSObject

    operator fun set(index: Int, value: T)
    operator fun get(index: Int): T
    fun concat(other: JsArrayInterface<T>): JsArrayInterface<T>
    fun join(separator: String = ","): String
    fun reverse(): JsArrayInterface<T>
    fun pop(): T
    fun push(vararg elements: T): Int
    fun shift(): T
    fun unshift(vararg elements: T): Int
    fun slice(start: Int, end: Int? = null): JsArrayInterface<T>
    fun splice(index: Int, count: Int, vararg items: T): JsArrayInterface<T>
    fun fill(value: T, start: Int = 0, end: Int? = null): JsArrayInterface<T>
    fun find(callback: IteratorCallback<T, Boolean>): T
    fun findIndex(callback: IteratorCallback<T, Boolean>): Int
    fun includes(element: T, start: Int = 0): Boolean
    fun indexOf(element: T, start: Int = 0): Int
    fun lastIndexOf(element: T, start: Int = -1): Int
    fun forLoop( startIndex: Int = 0, stopIndex: Int = -1, step: Int = 1, callback: IteratorCallback<T, Boolean>)
    fun forEach(callback: IteratorCallback<T, Unit>)
    fun filter(callback: IteratorCallback<T, Boolean>): JsArrayInterface<T>
    fun map(callback: IteratorCallback<T, T>): JsArrayInterface<T>
    fun every(callback: IteratorCallback<T, Boolean>): Boolean
    fun some(callback: IteratorCallback<T, Boolean>): Boolean
    fun reduce(callback: IteratorCallback<T, T>): T
    fun reduceRight(callback: IteratorCallback<T, T>): T
    fun sort(sortFunction: SortFunction<T>? = null): JsArrayInterface<T>
}