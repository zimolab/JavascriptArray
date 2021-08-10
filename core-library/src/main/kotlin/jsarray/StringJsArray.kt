package jsarray

import jsarray.base.JsAPIs.Array.CONCAT
import jsarray.base.JsAPIs.Array.EVERY
import jsarray.base.JsAPIs.Array.FILL
import jsarray.base.JsAPIs.Array.FILTER
import jsarray.base.JsAPIs.Array.FIND
import jsarray.base.JsAPIs.Array.FIND_INDEX
import jsarray.base.JsAPIs.Array.FOR_EACH
import jsarray.base.JsAPIs.Array.INCLUDES
import jsarray.base.JsAPIs.Array.INDEX_OF
import jsarray.base.JsAPIs.Array.JOIN
import jsarray.base.JsAPIs.Array.LAST_INDEX_OF
import jsarray.base.JsAPIs.Array.LENGTH
import jsarray.base.JsAPIs.Array.MAP
import jsarray.base.JsAPIs.Array.POP
import jsarray.base.JsAPIs.Array.PUSH
import jsarray.base.JsAPIs.Array.REDUCE
import jsarray.base.JsAPIs.Array.REDUCE_RIGHT
import jsarray.base.JsAPIs.Array.REVERSE
import jsarray.base.JsAPIs.Array.SHIFT
import jsarray.base.JsAPIs.Array.SLICE
import jsarray.base.JsAPIs.Array.SOME
import jsarray.base.JsAPIs.Array.SORT
import jsarray.base.JsAPIs.Array.SPLICE
import jsarray.base.JsAPIs.Array.UNSHIFT
import javafx.scene.web.WebEngine
import jsarray.base.*
import netscape.javascript.JSObject

open class StringJsArray private constructor(override val reference: JSObject) : JsArrayInterface<String?> {

    companion object {
        fun from(reference: JSObject): StringJsArray {
            if (!JsArrayInterface.isJsArray(reference)) {
                throw RuntimeException("the reference is point to an javascript Array object.")
            }
            return StringJsArray(reference)
        }

        fun newInstance(env: JSObject, initLength: Int = 0): StringJsArray {
            val result = env.execute("new Array($initLength)")
            if (result is JSObject) {
                return from(result)
            } else {
                throw RuntimeException("Cannot create an Array object in given JavaScript env.")
            }
        }

        fun newInstance(env: WebEngine, initLength: Int = 0): StringJsArray {
            val a = env.execute("new Array($initLength)")
            if (a is JSObject) {
                return from(a)
            } else {
                throw RuntimeException("Cannot create an Array object in given JavaScript env.")
            }
        }
    }

    private inline fun <reified M, R> with(
        nameInJs: String,
        callback: JsArrayIteratorCallback<M, R>,
        execution: (method: String) -> Any?
    ): Any? {
        reference.inject(nameInJs, callback)
        val result = execution("this.${nameInJs}.call")
        reference.uninject(nameInJs)
        if (result is Throwable) {
            throw JsArrayExecutionError("fail to execute JavaScript expression.")
        }
        return result
    }

    private inline fun <reified T> with(
        nameInJs: String,
        callback: JsArraySortFunction<T>,
        execution: (method: String) -> Any?
    ): Any? {
        reference.inject(nameInJs, callback)
        val result = execution("this.${nameInJs}.compare")
        reference.uninject(nameInJs)
        if (result is Throwable) {
            throw JsArrayExecutionError("fail to execute JavaScript expression.")
        }
        return result
    }

    private fun invoke(method: String, vararg args: Any?): Any? {
        return reference.invoke(method, *args)
    }

    private fun execute(jsExp: String): Any? {
        return reference.execute(jsExp)
    }

    override val length: Int
        get() {
            return execute("this.$LENGTH") as Int
        }


    override fun set(index: Int, value: String?) {
        reference.setSlot(index, value)
    }

    override fun get(index: Int): String? {
        return when (val value = reference.getSlot(index)) {
            null -> value
            is String -> value
            else -> throw JsArrayValueTypeError("value at index $index is not null or String.(index $index: $value)")
        }
    }

    override fun concat(other: JsArrayInterface<String?>): JsArrayInterface<String?> {
        val result = invoke(CONCAT, other.reference)
        if (result is JSObject)
            return from(result)
        throw JsArrayExecutionError("failed to invoke $CONCAT() function.")
    }

    override fun join(separator: String): String {
        val result = invoke(JOIN, separator)
        if (result is String)
            return result
        throw JsArrayExecutionError("failed to invoke $JOIN() function.")
    }

    override fun pop(): String? {
        return when (val result = invoke(POP)) {
            is String? -> result
            else -> throw JsArrayExecutionError("failed to invoke $POP() function.")
        }
    }

    override fun push(vararg elements: String?): Int {
        return when (val result = invoke(PUSH, *elements)) {
            is Int -> result
            else -> throw JsArrayExecutionError("failed to invoke $PUSH() function.")
        }
    }

    override fun forEach(callback: JsArrayIteratorCallback<String?, Unit>) {
        this.with("__forEach_cb__", callback) { method ->
            execute("this.$FOR_EACH((item, index, arr)=>{ $method(item, index, null, arr); })")
        }
    }

    override fun shift(): String? {
        return when (val result = invoke(SHIFT)) {
            is String? -> result
            else -> throw JsArrayExecutionError("failed to invoke $SHIFT() function.")
        }
    }

    override fun unshift(vararg elements: String?): Int {
        return when (val result = invoke(UNSHIFT, *elements)) {
            is Int -> result
            else -> throw JsArrayExecutionError("failed to invoke $UNSHIFT() function.")
        }
    }

    override fun slice(start: Int, end: Int?): JsArrayInterface<String?> {
        val result = if (end == null) {
            invoke(SLICE, start)
        } else {
            invoke(SLICE, end)
        }
        if (result is JSObject)
            return from(result)
        throw JsArrayExecutionError("failed to invoke $SLICE() function.")
    }

    override fun reverse(): JsArrayInterface<String?> {
        return if (invoke(REVERSE) is JSObject) {
            this
        } else {
            throw JsArrayExecutionError("failed to invoke $REVERSE() function.")
        }
    }

    override fun splice(index: Int, count: Int, vararg items: String?): JsArrayInterface<String?> {
        return when (val result = invoke(SPLICE, index, count, *items)) {
            is JSObject -> from(result)
            else -> throw JsArrayExecutionError("failed to invoke $SPLICE() function.")
        }
    }

    override fun fill(value: String?, start: Int, end: Int?): JsArrayInterface<String?> {
        val result = if (end == null) {
            invoke(FILL, value, start)
        } else {
            invoke(FILL, value, start, end)
        }
        return if (result is JSObject) {
            this
        } else {
            throw JsArrayExecutionError("failed to invoke $FILL() function.")
        }
    }

    override fun find(callback: JsArrayIteratorCallback<String?, Boolean>): String? {
        val result = with("__find_cb__", callback) { method: String ->
            execute("this.$FIND((item, index, arr)=>{ return $method(item, index, null, arr); })")
        }
        if (result is String?)
            return result
        throw JsArrayExecutionError("failed to invoke $FIND() function.")
    }

    override fun findIndex(callback: JsArrayIteratorCallback<String?, Boolean>): Int {
        val result = with("__find_index_cb__", callback) { method ->
            execute("this.$FIND_INDEX((item, index, arr)=>{ return $method(item, index, null, arr); })")
        }
        if (result is Int)
            return result
        throw JsArrayExecutionError("failed to invoke $FIND_INDEX() function.")
    }

    override fun includes(element: String?, start: Int): Boolean {
        return when (val result = invoke(INCLUDES, element, start)) {
            is Boolean -> result
            else -> throw JsArrayExecutionError("failed to invoke $INCLUDES() function.")
        }
    }

    override fun indexOf(element: String?, start: Int): Int {
        return when (val result = invoke(INDEX_OF, element, start)) {
            is Int -> result
            else -> throw JsArrayExecutionError("failed to invoke $INDEX_OF() function.")
        }
    }

    override fun lastIndexOf(element: String?, start: Int): Int {
        return when (val result = invoke(LAST_INDEX_OF, element, start)) {
            is Int -> result
            else -> throw JsArrayExecutionError("failed to invoke $LAST_INDEX_OF() function.")
        }
    }

    override fun forLoop(startIndex: Int, stopIndex: Int, step: Int, callback: JsArrayIteratorCallback<String?, Boolean>) {
        this.with("__for_cb__", callback) { method ->
            val stop = if (stopIndex <= 0) length else stopIndex
            execute("for(let i=${startIndex}; i < ${stop}; i = i + $step){if(!$method(this[i], i, null, this)) break}")
        }
    }

    override fun filter(callback: JsArrayIteratorCallback<String?, Boolean>): JsArrayInterface<String?> {
        val result = this.with("__filter_cb__", callback) { method ->
            execute("this.$FILTER((item, index, arr)=>{ return $method(item, index, null, arr) })")
        }
        if (result is JSObject)
            return from(result)
        throw JsArrayExecutionError("failed to invoke $FILTER() function.")
    }

    override fun map(callback: JsArrayIteratorCallback<String?, String?>): JsArrayInterface<String?> {
        val result = this.with("__map_cb__", callback) { method ->
            execute("this.$MAP((item, index, arr)=>{ return $method(item, index, null, arr) })")
        }
        if (result is JSObject)
            return from(result)
        throw JsArrayExecutionError("failed to invoke $MAP() function.")
    }

    override fun every(callback: JsArrayIteratorCallback<String?, Boolean>): Boolean {
        val result = this.with("__every_cb__", callback) { method ->
            execute("this.$EVERY((item, index, arr)=>{ return $method(item, index, null, arr) })")
        }
        if (result is Boolean)
            return result
        throw JsArrayExecutionError("failed to invoke $EVERY() function.")
    }

    override fun some(callback: JsArrayIteratorCallback<String?, Boolean>): Boolean {
        val result = this.with("__some_cb__", callback) { method ->
            execute("this.$SOME((item, index, arr)=>{ return $method(item, index, null, arr) })")
        }
        if (result is Boolean)
            return result
        throw JsArrayExecutionError("failed to invoke $SOME() function.")
    }

    override fun reduce(callback: JsArrayIteratorCallback<String?, String?>): String? {
        val result = this.with("__reduce_cb__", callback) { method ->
            execute("this.$REDUCE((total, item, index, arr)=>{ return $method(item, index, total, arr) })")
        }
        if (result is String?)
            return result
        throw JsArrayExecutionError("failed to invoke $REDUCE() function.")
    }

    override fun reduceRight(callback: JsArrayIteratorCallback<String?, String?>): String? {
        val result = this.with("__right_reduce_cb__", callback) { method ->
            execute("this.$REDUCE_RIGHT((total, item, index, arr)=>{ return $method(item, index, total, arr) })")
        }
        if (result is String?)
            return result
        throw JsArrayExecutionError("failed to invoke $REDUCE_RIGHT() function.")
    }

    override fun sort(sortFunction: JsArraySortFunction<String?>?): JsArrayInterface<String?> {
        val result = if (sortFunction == null)
            invoke(SORT)
        else {
            this.with("__sort_cb__", sortFunction) { method ->
                execute("this.$SORT((a, b)=>{ return $method(a, b) })")
            }
        }
        if (result is JSObject)
            return this
        throw JsArrayExecutionError("failed to invoke $SORT() function.")
    }

    override fun toString(): String {
        return "[${join()}]"
    }

}