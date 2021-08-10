# 项目说明
一个将JavaScript中的Array对象映射为Kotlin（java）对象的库。该项目基于netscape.javascript.JSObject对象，映射了Javascript Array对象的大部分接口，
适用于使用WebEngine与底层Javascript代码进行交互的情景。

## API

### 接口：JsArrayInterface
```kotlin
// 从现有JSObject（必须指向一个js Array对象）创建实例
fun from(reference: JSObject): JsArrayInterface<T>
// 在Javascript上下文中创建一个新的Array对象
fun newInstance(env: JSObject, initLength: Int = 0): JsArrayInterface<T>
fun newInstance(env: WebEngine, initLength: Int = 0): JsArrayInterface<T>
// 存取值（重载了[]操作符，可以直接使用：obj[index]=value语法）
operator fun set(index: Int, value: T)
operator fun get(index: Int): T

// 数组操作
fun concat(other: JsArrayInterface<T>): JsArrayInterface<T>
fun join(separator: String = ","): String
fun pop(): T
fun push(vararg elements: T): Int
fun shift(): T
fun unshift(vararg elements: T): Int
fun slice(start: Int, end: Int? = null): JsArrayInterface<T>
fun splice(index: Int, count: Int, vararg items: T): JsArrayInterface<T>
fun fill(value: T, start: Int = 0, end: Int? = null): JsArrayInterface<T>    

// 与迭代有关的API
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
```

### 具体实现类型
#### JsStringArray
字符串数组

### JsIntArray
整数型数组

### JsDoubleArray
双精度浮点数数组

### JsBooleanArray
布尔型数组

### JsObjectArray
Javascript对象数组（即netscape.javascript.JSObject对象）

### JsAnyArray
任意型数组，包括字符串、整数、浮点数、boolean以及JavaScript中的任意对象（需要被映射为JSObject对象）

### 自定义
除了上述内置的几种类型，还可以自行实现JsArrayInterface<T>接口，进行任意的类型映射。