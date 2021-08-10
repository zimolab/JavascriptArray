package com.zimolab.testifyfx.view

import javafx.concurrent.Worker
import javafx.scene.layout.VBox
import jsarray.StringJsArray
import jsarray.base.JsArraySortFunction
import jsarray.base.TypedIteratorCallback
import jsarray.base.UnTypedIteratorCallback
import jsarray.base.execute
import netscape.javascript.JSObject
import tornadofx.*
import java.util.*

class MainView : View("JavascriptArray Demo") {
    override val root: VBox = vbox {
        webview {
            engine.loadWorker.stateProperty().addListener { observable, oldValue, newValue ->
                if (newValue == Worker.State.SUCCEEDED) {
                    val arr = engine.execute(" [\"1\", \"2\", \"3\", null, null, 5, undefined]")
                    val arr2 = engine.execute("['a', 'b', 'c', 'd']")
                    if (arr is JSObject) {
                        val strArr = StringJsArray.from(arr)
                        println("strArr.length: ${strArr.length}")
                        println("arr[3]: ${arr.getSlot(3)}")
                        println("strArr[3]: ${strArr[3]}")

                        // get()
                        println("arr[6]: ${arr.getSlot(6)}")
                        println("strArr[6]: ${strArr[6]}")

                        // 引发异常，因为索引5上的值并非null或者string
                        //println("arr[5]: ${arr.getSlot(5)}")
                        //println("strArr[5]: ${strArr[5]}")

                        // set()
                        strArr[5] = "5"

                        // concat() && toString()(join())
                        val strArr2 = StringJsArray.from(arr2 as JSObject)
                        val strArr3 = strArr.concat(strArr2)
                        println(strArr3)

                        // pop() && push()
                        strArr3.pop()
                        println(strArr3)
                        strArr3.push("hello", "world")
                        println(strArr3)

                        // shift() && unshift
                        strArr3.shift()
                        println(strArr3)
                        strArr3.unshift("hello", "world")
                        println(strArr3)

                        // slice()
                        println(strArr3.slice(2, 5))

                        // splice()
                        println(strArr3.splice(1, 2))

                        // reverse()
                        println(strArr3.reverse())

                        // find() && findIndex()
                        println(strArr3.find(object : TypedIteratorCallback<String?, Boolean> {
                            override fun call(currentValue: String?, index: Int, total: String?, arr: Any?): Boolean {
                                if (currentValue!= null && currentValue != "undefined" && currentValue.length <= 2)
                                    return true
                                return false
                            }
                        }))
                        println(strArr3.findIndex(object : TypedIteratorCallback<String?, Boolean> {
                            override fun call(currentValue: String?, index: Int, total: String?, arr: Any?): Boolean {
                                if (currentValue!= null && currentValue != "undefined" && currentValue.length <= 2)
                                    return true
                                return false
                            }
                        }))

                        // indexOf && lastIndexOf()
                        println(strArr3.indexOf("hello"))
                        println(strArr3.lastIndexOf("hello"))
                        println(strArr3.lastIndexOf("foo"))

                        // includes()
                        println(strArr3.includes("world"))
                        println(strArr3.includes("world!"))

                        // static newInstance() && fill()
                        val strArr4 = StringJsArray.newInstance(engine, 3)
                        val strArr5 = StringJsArray.newInstance(strArr3.reference, 5)
                        strArr4.fill("hello", 0, null)
                        strArr5.fill("world", 0, 2)
                        println(strArr4)
                        println(strArr5)

                        // forLoop()


                        // forEach() 1
                        strArr3.forEach(object : TypedIteratorCallback<String?, Unit> {
                            override fun call(currentValue: String?, index: Int, total: String?, arr: Any?) {
                                println("index=$index, value=$currentValue")
                            }
                        })
                        println("=============================")

                        arr.setSlot(10, Date())
                        // 由于使用了限定类型的迭代回调对象（TypedIteratorCallback），而上面一行代码又将第10个元素设置为一个Date对象，因此，下面的代码应当会引发异常
                        // 异常为：java.lang.IllegalArgumentException: argument type mismatch
//                    strArr.forEach(object : TypedIteratorCallback<String?, Unit>() {
//                        override fun call(currentValue: String?, index: Int, total: String?, arr: Any?) {
//                            println("index=$index, value=$currentValue")
//                        }
//                    })
                        // 如果不想因为类型不符合的问题引发，则需要使用类型通用版本的迭代回调类对象（UnTypedIteratorCallback）
                        strArr.forEach(object : UnTypedIteratorCallback<Unit> {
                            override fun call(currentValue: Any?, index: Int, total: Any?, arr: Any?): Unit {
                                println("index=$index, value=$currentValue")
                            }

                        })

                        println("==============================")
                        // filter()
                        println(strArr3.filter(object : TypedIteratorCallback<String?, Boolean> {
                            override fun call(currentValue: String?, index: Int, total: String?, arr: Any?): Boolean {
                                println("$currentValue, ${currentValue == null}")
                                return currentValue != null && currentValue.length >= 2
                            }
                        }))
                        println("==============================")
                        strArr3.forLoop(step = 2, callback = object : TypedIteratorCallback<String?, Boolean> {
                            override fun call(currentValue: String?, index: Int, total: String?, arr: Any?): Boolean {
                                if (index >= 5)
                                    return false // break
                                println("$index: $currentValue")
                                return true
                            }
                        })
                        println("==============================")
                        // map()
                        println(strArr.map(object : UnTypedIteratorCallback<String?> {
                            override fun call(currentValue: Any?, index: Int, total: Any?, arr: Any?): String {
                                return when (currentValue) {
                                    is String -> "$index-string-$currentValue"
                                    null -> "$index-null"
                                    else -> "$index-not-string-$currentValue"
                                }
                            }
                        }))
                        println("==========================")
                        val strArr6 = StringJsArray.newInstance(engine, 10).fill("hello")
                        val strArr7 = StringJsArray.newInstance(engine, 10).fill("hello")
                        strArr7[5] = null
                        println(strArr6.every(object : TypedIteratorCallback<String?, Boolean> {
                            override fun call(currentValue: String?, index: Int, total: String?, arr: Any?): Boolean {
                                return currentValue != null && currentValue.length >= 3
                            }
                        }))
                        println(strArr7.every(object : TypedIteratorCallback<String?, Boolean> {
                            override fun call(currentValue: String?, index: Int, total: String?, arr: Any?): Boolean {
                                return currentValue != null && currentValue.length >= 3
                            }
                        }))
                        println(strArr7.some(object : TypedIteratorCallback<String?, Boolean> {
                            override fun call(currentValue: String?, index: Int, total: String?, arr: Any?): Boolean {
                                return currentValue != null && currentValue.length >= 3
                            }
                        }))
                        println("=============================")
                        println(strArr3.reduce(object : TypedIteratorCallback<String?, String?> {
                            override fun call(currentValue: String?, index: Int, total: String?, arr: Any?): String {
                                return if (currentValue == null) {
                                    total + "NULL;"
                                } else {
                                    "$total$currentValue;"
                                }
                            }
                        }))
                        println(strArr3.reduceRight(object : TypedIteratorCallback<String?, String?> {
                            override fun call(currentValue: String?, index: Int, total: String?, arr: Any?): String {
                                return if (currentValue == null) {
                                    total + "NULL;"
                                } else {
                                    "$total$currentValue;"
                                }
                            }
                        }))
                        println("=========================")
                        println(strArr3)
                        println(strArr3.sort())
                        println(strArr3.sort(object : JsArraySortFunction<String?> {
                            override fun compare(a: String?, b: String?): Boolean {
                                return a == null && b == null
                            }
                        }))
                    }
                }
            }
            engine.load(javaClass.getResource("/index.html")?.toExternalForm())
        }
    }
}
