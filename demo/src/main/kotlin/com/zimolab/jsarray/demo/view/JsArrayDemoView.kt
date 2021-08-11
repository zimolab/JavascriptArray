package com.zimolab.jsarray.demo.view

import com.zimolab.jsarray.base.*
import javafx.concurrent.Worker
import javafx.scene.web.WebEngine
import netscape.javascript.JSObject
import tornadofx.*

class JsArrayDemoView : View("JsIntArray Demo") {
    val url = javaClass.getResource("/index.html")?.toExternalForm()
    override val root = vbox {
        webview {
            engine.loadWorker.stateProperty().addListener { _, _, state->
                if (state == Worker.State.SUCCEEDED) {
                    testJsIntArray(engine)
                }
            }
            engine.load(url)
        }
    }

    private fun testJsIntArray(engine: WebEngine) {
        println("============测试JsIntArray===============")
        val array1 = engine.execute("[1, 2, 3, 4, 5, 6, 7, 8, 9, 0, -1]") as JSObject
        val array2 = JsArray.newArray(engine, 10)

        val jsArray1 = JsArray.intArrayOf(array1)
        val jsArray2 = JsArray.intArrayOf(array2!!)

        println("测试: toString()")
        println("jsArray1: $jsArray1")
        println("jsArray2: $jsArray2")

        println("测试：fill()")
        println("jsArray2.fill(-1): ${jsArray2.fill(-1)}")

        println("测试: length")
        println("jsArray1.length: ${jsArray1.length}")
        println("jsArray2.length: ${jsArray2.length}")

        println("测试：concat()")
        val jsArray3 = jsArray2.concat(jsArray1)
        println("jsArray3 = jsArray2.concat(jsArray1): $jsArray3")

        println("测试：join()")
        println("jsArray3.join(\";\"): ${jsArray3.join(";")}")

        println("测试set、get")
        jsArray3[0] = Int.MAX_VALUE
        println("jsArrays[0] = Int.MAX_VALUE; jsArray[0]: ${jsArray3[0]}")

        println("测试reverse()")
        println("jsArray3.reverse(): ${jsArray3.reverse()}")

        println("测试pop()、push()、shift()、unshift()")
        println("jsArray3.pop(): ${jsArray3.pop()}")
        println("jsArray3.push(-255): ${jsArray3.push(-255)}")
        println("jsArray3.shift(): ${jsArray3.shift()}")
        println("jsArray3.unshift(-128): ${jsArray3.unshift(-128)}")
        println("jsArray3: $jsArray3")

        println("测试slice()、splice()")
        println("jsArray3: $jsArray3")
        println("jsArray3.slice(1, 3): ${jsArray3.slice(1, 3)}")
        println("jsArray3.slice(5): ${jsArray3.slice(5)}")
        println("jsArray3: $jsArray3")
        println("jsArray3.splice(1, 3): ${jsArray3.splice(1, 3)}")
        println("jsArray3: $jsArray3")
        println("jsArray3.splice(1, 3, 11, 12, 13, 14): ${jsArray3.splice(1, 3, 11, 12, 13, 14)}")
        println("jsArray3: $jsArray3")

        jsArray3.reverse()
        println("测试：find()、findIndex()")
        println("jsArray3：$jsArray3")
        var result = jsArray3.find(object : TypedIteratorCallback<Int?, Boolean> {
            override fun call(currentValue: Int?, index: Int, total: Boolean?, arr: Any?): Boolean {
                if (currentValue != null && currentValue>= 0)
                    return true
                return false
            }
        })
        println("jsArray3.find(): $result")
        // 加一些非Int数据，测试是否会抛出异常，这里直接操作原始对象
        array2.setSlot(5, "1234567abcd")
        jsArray2[3] = 5
        println("jsArray2: $jsArray2")
        result = jsArray2.findIndex(object : TypedIteratorCallback<Int?, Boolean> {
            override fun call(currentValue: Int?, index: Int, total: Boolean?, arr: Any?): Boolean {
                println("jsArray2[$index]=$currentValue")
                if (currentValue != null && currentValue >= 3)
                    return true
                return false
            }
        })
        println("jsArray2.findIndex(): $result")

        println("测试indexOf()、lastIndexOf()")
        println("jsArray3: $jsArray3")
        println("jsArray3.indexOf(-1, 2): ${jsArray3.indexOf(-1, 2)}")
        println("jsArray3.lastIndexOf(-1): ${jsArray3.lastIndexOf(-1)}")

        println("测试include()")
        println("jsArray3.includes(null):${jsArray3.includes(null)}")
        println("jsArray2.includes(null):${jsArray2.includes(null)}")

        println("测试forEach()、forLoop()、filter()、map()、every()、some()、reduce()")
        println("jsArray2.forEach()-使用UnTypedIteratorCallback")
        jsArray2.forEach(object : UnTypedIteratorCallback<Unit>{
            override fun call(currentValue: Any?, index: Int, total: Unit?, arr: Any?) {
                println("jsArray2[$index]=$currentValue")
            }
        })
        println("jsArray2.forLoop(startIndex=1, step=2)-使用TypedIteratorCallback")
        jsArray2.forLoop(callback = object : TypedIteratorCallback<Int?, Boolean>{
            override fun call(currentValue: Int?, index: Int, total: Boolean?, arr: Any?): Boolean {
                println("jsArray2[$index] = $currentValue")
                return true
            }
        }, startIndex = 1, step = 2)
        val filtered = jsArray3.filter(object : TypedIteratorCallback<Int?, Boolean>{
            override fun call(currentValue: Int?, index: Int, total: Boolean?, arr: Any?): Boolean {
                if (currentValue != null && currentValue >= 0)
                    return true
                return false
            }
        })
        println("jsArray3.filter(): $filtered")
        val mapped = jsArray3.map(object : TypedIteratorCallback<Int?, Int?>{
            override fun call(currentValue: Int?, index: Int, total: Int?, arr: Any?): Int? {
                if (currentValue != null) {
                    return currentValue * 5
                }
                return null
            }
        })
        println("jsArray3.map: $mapped")
        val r = jsArray3.every(object : UnTypedIteratorCallback<Boolean> {
            override fun call(currentValue: Any?, index: Int, total: Boolean?, arr: Any?): Boolean {
                if (currentValue is Int?)
                    return true
                return false
            }
        })
        println("jsArray3.every(): $r")
        val r1 = jsArray2.some(object : UnTypedIteratorCallback<Boolean> {
            override fun call(currentValue: Any?, index: Int, total: Boolean?, arr: Any?): Boolean {
                return currentValue is String
            }
        })
        println("jsArray2.some(): $r1")
        println("jsArray2: $jsArray2")
        val r2 = jsArray2.reduce(object : UnTypedIteratorCallback<String?>{
            override fun call(currentValue: Any?, index: Int, total: String?, arr: Any?): String {
                return "${currentValue};${total}"
            }
        })
        println("jsArray2.reduce(): $r2")
        val r3 = jsArray2.reduceRight(object : TypedIteratorCallback<Int?, Int?>{
            override fun call(currentValue: Int?, index: Int, total: Int?, arr: Any?): Int? {
                if (currentValue != null && total != null)
                    return currentValue + total
                return total
            }
        })
        println("jsArray2.reduceRight(): $r3")
        println("================测试完毕====================")
        println()

    }

    private fun testJsStringArray() {

    }

    private fun testJsDoubleArray() {

    }

    private fun testJSObjectArray() {

    }

    private fun testJsBooleanArray() {

    }
}