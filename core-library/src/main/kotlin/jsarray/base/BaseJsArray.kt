package jsarray.base

import javafx.scene.web.WebEngine
import jsarray.JsIntArray
import netscape.javascript.JSObject

abstract class BaseJsArray<T>(override val reference: JSObject) : JsArrayInterface<T> {

    inline fun <reified M, R> with(
        nameInJs: String,
        callback: IteratorCallback<M, R>,
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

    inline fun <reified T> with(
        nameInJs: String,
        callback: SortFunction<T>,
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

    open fun invoke(method: String, vararg args: Any?): Any? {
        return reference.invoke(method, *args)
    }

    open fun execute(jsExp: String): Any? {
        return reference.execute(jsExp)
    }

    override val length: Int
        get() = execute("this.${JsAPIs.Array.LENGTH}") as Int

    override fun toString(): String {
        return "[${join()}]"
    }
}