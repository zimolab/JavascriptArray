package com.zimolab.jsarray.base

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

    operator fun set(index: Int, value: T?)
    operator fun get(index: Int): T?
    fun concat(other: JsArrayInterface<T>): JsArrayInterface<T>
    fun join(separator: String = ","): String
    fun reverse(): JsArrayInterface<T>
    fun pop(): T?
    fun push(vararg elements: T?): Int
    fun shift(): T?
    fun unshift(vararg elements: T?): Int
    fun slice(start: Int, end: Int? = null): JsArrayInterface<T>
    fun splice(index: Int, count: Int, vararg items: T?): JsArrayInterface<T>
    fun fill(value: T?, start: Int = 0, end: Int? = null): JsArrayInterface<T>
    fun find(callback: JsArrayIteratorCallback<T?, Boolean>): T?
    fun findIndex(callback: JsArrayIteratorCallback<T, Boolean>): Int
    fun includes(element: T?, start: Int = 0): Boolean
    fun indexOf(element: T?, start: Int = 0): Int
    fun lastIndexOf(element: T?, start: Int = -1): Int
    fun forLoop(callback: JsArrayIteratorCallback<T?, Boolean>, startIndex: Int = 0, stopIndex: Int = -1, step: Int = 1)
    fun forEach(callback: JsArrayIteratorCallback<T?, Unit>)
    fun filter(callback: JsArrayIteratorCallback<T?, Boolean>): JsArrayInterface<T>
    fun map(callback: JsArrayIteratorCallback<T?, T?>): JsArrayInterface<T>
    fun every(callback: JsArrayIteratorCallback<T?, Boolean>): Boolean
    fun some(callback: JsArrayIteratorCallback<T?, Boolean>): Boolean
    fun <R> reduce(callback: JsArrayIteratorCallback<T?, R?>): R?
    fun <R> reduceRight(callback: JsArrayIteratorCallback<T?, R?>): R?
    fun sort(sortFunction: JsArraySortFunction<T?>? = null): JsArrayInterface<T>
}