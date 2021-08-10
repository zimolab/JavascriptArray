package com.zimolab.jsarray.demo.view

import javafx.concurrent.Worker
import jsarray.JsIntArray
import jsarray.base.TypedIteratorCallback
import jsarray.base.UnTypedIteratorCallback
import netscape.javascript.JSObject
import tornadofx.*

class JsIntArrayDemoView : View("JsIntArray Demo") {
    override val root = vbox {
        webview {
            engine.loadWorker.stateProperty().addListener { _, _, state->
                if (state == Worker.State.SUCCEEDED) {
                    val intArr1 = JsIntArray.newInstance(engine, 10).fill(10)
                    println("intArr1=$intArr1")
                    intArr1.forEach(object : TypedIteratorCallback<Int?, Unit>{
                        override fun call(currentValue: Int?, index: Int, total: Int?, arr: Any?) {
                            println("intArr1[$index] = $currentValue")
                        }
                    })
                    println()
                    val arr2 = engine.executeScript("[1,2,3,,5,6,8,9,,'hello', null,10.9]")
                    val intArr2 = JsIntArray.from(arr2 as JSObject)
                    println("intArr2=$intArr2; length=${intArr2.length}")
                    println("使用TypedIteratorCallback")
                    intArr2.forEach(object : TypedIteratorCallback<Int?, Unit>{
                        override fun call(currentValue: Int?, index: Int, total: Int?, arr: Any?) {
                            println("intArr2[$index] = $currentValue")
                        }
                    })
                    println("使用UnTypedIteratorCallback")
                    intArr2.forEach(object : UnTypedIteratorCallback<Unit>{
                        override fun call(currentValue: Any?, index: Int, total: Any?, arr: Any?) {
                            println("intArr2[$index] = $currentValue")
                        }
                    })
                    println("forLoop")
                    intArr2.forLoop(step = 2, callback = object : UnTypedIteratorCallback<Boolean>{
                        override fun call(currentValue: Any?, index: Int, total: Any?, arr: Any?): Boolean {
                            if (currentValue == null)
                                return false // break
                            println("intArr2[$index]=$currentValue")
                            return true
                        }

                    })
                }
            }

            engine.load(javaClass.getResource("/index.html")?.toExternalForm())
        }
    }
}
