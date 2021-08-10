package jsarray

import javafx.scene.web.WebEngine
import jsarray.base.*
import jsarray.base.JsAPIs.UNDEFINED
import netscape.javascript.JSObject

class JsIntArray private constructor(reference: JSObject) : BaseJsArray<Int?>(reference) {

    companion object {
        fun from(reference: JSObject): JsIntArray {
            if (!JsArrayInterface.isJsArray(reference)) {
                throw RuntimeException("the reference is point to an javascript Array object.")
            }
            return JsIntArray(reference)
        }

        fun newInstance(env: JSObject, initLength: Int = 0): JsIntArray {
            val result = env.execute("new Array($initLength)")
            if (result is JSObject) {
                return from(result)
            } else {
                throw RuntimeException("cannot create an Array object in given JavaScript env.")
            }
        }

        fun newInstance(env: WebEngine, initLength: Int = 0): JsIntArray {
            val a = env.execute("new Array($initLength)")
            if (a is JSObject) {
                return from(a)
            } else {
                throw RuntimeException("cannot create an Array object in given JavaScript env.")
            }
        }
    }

    override fun set(index: Int, value: Int?) {
        reference.setSlot(index, value)
    }

    override fun get(index: Int): Int? {
        return when (val value = reference.getSlot(index)) {
            is Int? -> value
            UNDEFINED -> null
            else -> throw JsArrayValueTypeError("value at index $index is not null or Int.(index $index: $value)")
        }
    }

    override fun concat(other: JsArrayInterface<Int?>): JsArrayInterface<Int?> {
        val result = invoke(JsAPIs.Array.CONCAT, other.reference)
        if (result is JSObject)
            return from(result)
        throw JsArrayExecutionError("failed to invoke ${JsAPIs.Array.CONCAT}() function.")
    }

    override fun join(separator: String): String {
        val result = invoke(JsAPIs.Array.JOIN, separator)
        if (result is String)
            return result
        throw JsArrayExecutionError("failed to invoke ${JsAPIs.Array.JOIN}() function.")
    }

    override fun reverse(): JsArrayInterface<Int?> {
        return if (invoke(JsAPIs.Array.REVERSE) is JSObject) {
            this
        } else {
            throw JsArrayExecutionError("failed to invoke ${JsAPIs.Array.REVERSE}() function.")
        }
    }

    override fun pop(): Int? {
        return when (val result = invoke(JsAPIs.Array.POP)) {
            is Int? -> result
            UNDEFINED -> null
            else -> throw JsArrayExecutionError("failed to invoke ${JsAPIs.Array.POP}() function.")
        }
    }

    override fun push(vararg elements: Int?): Int {
        return when (val result = invoke(JsAPIs.Array.PUSH, *elements)) {
            is Int -> result
            else -> throw JsArrayExecutionError("failed to invoke ${JsAPIs.Array.PUSH}() function.")
        }
    }

    override fun shift(): Int? {
        return when (val result = invoke(JsAPIs.Array.SHIFT)) {
            is Int? -> result
            UNDEFINED -> null
            else -> throw JsArrayExecutionError("failed to invoke ${JsAPIs.Array.SHIFT}() function.")
        }
    }

    override fun unshift(vararg elements: Int?): Int {
        return when (val result = invoke(JsAPIs.Array.UNSHIFT, *elements)) {
            is Int -> result
            else -> throw JsArrayExecutionError("failed to invoke ${JsAPIs.Array.UNSHIFT}() function.")
        }
    }

    override fun slice(start: Int, end: Int?): JsArrayInterface<Int?> {
        val result = if (end == null) {
            invoke(JsAPIs.Array.SLICE, start)
        } else {
            invoke(JsAPIs.Array.SLICE, end)
        }
        if (result is JSObject)
            return from(result)
        throw JsArrayExecutionError("failed to invoke ${JsAPIs.Array.SLICE}() function.")
    }

    override fun splice(index: Int, count: Int, vararg items: Int?): JsArrayInterface<Int?> {
        return when (val result = invoke(JsAPIs.Array.SPLICE, index, count, *items)) {
            is JSObject -> from(result)
            else -> throw JsArrayExecutionError("failed to invoke ${JsAPIs.Array.SPLICE}() function.")
        }
    }

    override fun fill(value: Int?, start: Int, end: Int?): JsArrayInterface<Int?> {
        val result = if (end == null) {
            invoke(JsAPIs.Array.FILL, value, start)
        } else {
            invoke(JsAPIs.Array.FILL, value, start, end)
        }
        return if (result is JSObject) {
            this
        } else {
            throw JsArrayExecutionError("failed to invoke ${JsAPIs.Array.FILL}() function.")
        }
    }

    override fun find(callback: IteratorCallback<Int?, Boolean>): Int? {
        val result = with("__find_cb__", callback) { method: String ->
            execute("this.${JsAPIs.Array.FIND}((item, index, arr)=>{ return $method(item, index, null, arr); })")
        }
        return when (result) {
            is Int? -> result
            UNDEFINED -> null
            else -> throw JsArrayExecutionError("failed to invoke ${JsAPIs.Array.FIND}() function.")
        }
    }

    override fun findIndex(callback: IteratorCallback<Int?, Boolean>): Int {
        val result = with("__find_index_cb__", callback) { method ->
            execute("this.${JsAPIs.Array.FIND_INDEX}((item, index, arr)=>{ return $method(item, index, null, arr); })")
        }
        if (result is Int)
            return result
        throw JsArrayExecutionError("failed to invoke ${JsAPIs.Array.FIND_INDEX}() function.")
    }

    override fun includes(element: Int?, start: Int): Boolean {
        return when (val result = invoke(JsAPIs.Array.INCLUDES, element, start)) {
            is Boolean -> result
            else -> throw JsArrayExecutionError("failed to invoke ${JsAPIs.Array.INCLUDES}() function.")
        }
    }

    override fun indexOf(element: Int?, start: Int): Int {
        return when (val result = invoke(JsAPIs.Array.INDEX_OF, element, start)) {
            is Int -> result
            else -> throw JsArrayExecutionError("failed to invoke ${JsAPIs.Array.INDEX_OF}() function.")
        }
    }

    override fun lastIndexOf(element: Int?, start: Int): Int {
        return when (val result = invoke(JsAPIs.Array.LAST_INDEX_OF, element, start)) {
            is Int -> result
            else -> throw JsArrayExecutionError("failed to invoke ${JsAPIs.Array.LAST_INDEX_OF}() function.")
        }
    }

    override fun forLoop(startIndex: Int, stopIndex: Int, step: Int, callback: IteratorCallback<Int?, Boolean>) {
        this.with("__for_cb__", callback) { method ->
            val stop = if (stopIndex <= 0) length else stopIndex
            execute("for(let i=${startIndex}; i < ${stop}; i = i + $step){if(!$method(this[i], i, null, this)) break}")
        }
    }

    override fun forEach(callback: IteratorCallback<Int?, Unit>) {
        this.with("__forEach_cb__", callback) { method ->
            execute("this.${JsAPIs.Array.FOR_EACH}((item, index, arr)=>{ $method(item, index, null, arr); })")
        }
    }

    override fun filter(callback: IteratorCallback<Int?, Boolean>): JsArrayInterface<Int?> {
        val result = this.with("__filter_cb__", callback) { method ->
            execute("this.${JsAPIs.Array.FILTER}((item, index, arr)=>{ return $method(item, index, null, arr) })")
        }
        if (result is JSObject)
            return from(result)
        throw JsArrayExecutionError("failed to invoke ${JsAPIs.Array.FILTER}() function.")
    }

    override fun map(callback: IteratorCallback<Int?, Int?>): JsArrayInterface<Int?> {
        val result = this.with("__map_cb__", callback) { method ->
            execute("this.${JsAPIs.Array.MAP}((item, index, arr)=>{ return $method(item, index, null, arr) })")
        }
        if (result is JSObject)
            return from(result)
        throw JsArrayExecutionError("failed to invoke ${JsAPIs.Array.MAP}() function.")
    }

    override fun every(callback: IteratorCallback<Int?, Boolean>): Boolean {
        val result = this.with("__every_cb__", callback) { method ->
            execute("this.${JsAPIs.Array.EVERY}((item, index, arr)=>{ return $method(item, index, null, arr) })")
        }
        if (result is Boolean)
            return result
        throw JsArrayExecutionError("failed to invoke ${JsAPIs.Array.EVERY}() function.")
    }

    override fun some(callback: IteratorCallback<Int?, Boolean>): Boolean {
        val result = this.with("__some_cb__", callback) { method ->
            execute("this.${JsAPIs.Array.SOME}((item, index, arr)=>{ return $method(item, index, null, arr) })")
        }
        if (result is Boolean)
            return result
        throw JsArrayExecutionError("failed to invoke ${JsAPIs.Array.SOME}() function.")
    }

    override fun reduce(callback: IteratorCallback<Int?, Int?>): Int? {
        val result = this.with("__reduce_cb__", callback) { method ->
            execute("this.${JsAPIs.Array.REDUCE}((total, item, index, arr)=>{ return $method(item, index, total, arr) })")
        }
        return when (result) {
            is Int? -> result
            UNDEFINED -> null
            else -> throw JsArrayExecutionError("failed to invoke ${JsAPIs.Array.REDUCE}() function.")
        }
    }

    override fun reduceRight(callback: IteratorCallback<Int?, Int?>): Int? {
        val result = this.with("__right_reduce_cb__", callback) { method ->
            execute("this.${JsAPIs.Array.REDUCE_RIGHT}((total, item, index, arr)=>{ return $method(item, index, total, arr) })")
        }
        return when (result) {
            is Int? -> result
            UNDEFINED -> null
            else -> throw JsArrayExecutionError("failed to invoke ${JsAPIs.Array.REDUCE_RIGHT}() function.")
        }
    }

    override fun sort(sortFunction: SortFunction<Int?>?): JsArrayInterface<Int?> {
        val result = if (sortFunction == null)
            invoke(JsAPIs.Array.SORT)
        else {
            this.with("__sort_cb__", sortFunction) { method ->
                execute("this.${JsAPIs.Array.SORT}((a, b)=>{ return $method(a, b) })")
            }
        }
        if (result is JSObject)
            return this
        throw JsArrayExecutionError("failed to invoke ${JsAPIs.Array.SORT}() function.")
    }
}