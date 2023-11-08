@file:Suppress("unused")

class TestsUtil {
    val ignoreStackSince = Thread.getAllStackTraces()[Thread.currentThread()]!!
        .run { this[size - 2].className + "$" + this[4].methodName + "$" }
    val results = mutableMapOf<Pair<String, String>, Int>()
    val total = mutableMapOf<Pair<String, String>, Int>()
    var indent = 0
    fun println(x: Any?) {
        kotlin.io.println("    ".repeat(indent) + x)
    }

    private var blockName: String? = null
    fun testBlock(name: String, runnable: () -> Unit) {
        blockName = name
        println("$name:")
        indent++
        runnable()
        indent--
        blockName = null
    }

    private var suppressTesting = false
    fun testGroup(name: String, runnable: () -> Unit) = testGroup(name, listOf(), runnable)

    private var groupName: String? = null
    fun testGroup(name: String, needs: List<String>, runnable: () -> Unit) {
        groupName = name
        println("$name:")
        indent++
        if (needs.all { results[blockName to it] == total[blockName to it] }) {
            runnable()
        } else {
            println("Requires successful completion of tests ${needs.joinToString(", ")}")
            suppressTesting = true
            runnable()
            suppressTesting = false
        }
        indent--
        groupName = null
    }

    private val stackTraces = mutableListOf<List<StackTraceElement>>()

    private fun test(test: () -> String = { "test" }, runnable: () -> Boolean) {
        total[blockName!! to groupName!!] = (total[blockName to groupName] ?: 0) + 1
        if (!suppressTesting) {
            try {
                if (runnable()) {
                    results[blockName!! to groupName!!] = (results[blockName to groupName] ?: 0) + 1
                }
            } catch (e: Throwable) {
                println("Unexpected ${e.javaClass.name} : ${e.message} during ${test()}")
                val important = e.stackTrace.takeWhile { !it.className.startsWith(ignoreStackSince) }
                if (important !in stackTraces) {
                    e.printStackTrace(System.out)
                    stackTraces.add(important)
                }
            }
        }
    }

    fun <R> compare(
        correct: () -> R, tested: () -> R, funcName: String,
        silentRes: Boolean = false
    ) = test {
        val expected = correct()
        val got = tested()
        if (got != expected) {
            println(
                "Fail: Function $funcName" + if (silentRes) "" else "returned $got, but $expected expected"
            )
            false
        } else {
            println("Success")
            true
        }
    }

    fun <A, R> compare(
        correct: (A) -> R, tested: (A) -> R, a: A, funcName: String,
        silentArgs: Boolean = false,
        silentRes: Boolean = false
    ) = test {
        val expected = correct(a)
        val got = tested(a)
        if (got != expected) {
            println(
                "Fail: Function $funcName ${if (silentArgs) "" else "with arguments $a"} " +
                        if (silentRes) "" else "returned $got, but $expected expected"
            )
            false
        } else {
            println("Success")
            true
        }
    }

    fun <A, B, R> compare(
        correct: (A, B) -> R, tested: (A, B) -> R, a: A, b: B, funcName: String,
        silentArgs: Boolean = false,
        silentRes: Boolean = false
    ) = test({ "$funcName($a, $b)" }) {
        val expected = correct(a, b)
        val got = tested(a, b)
        if (got != expected) {
            println(
                "Fail: Function $funcName ${if (silentArgs) "" else "with arguments $a, $b"} " +
                        if (silentRes) "" else "returned $got, but $expected expected"
            )
            false
        } else {
            println("Success")
            true
        }
    }

    fun <A, B, C, R> compare(
        correct: (A, B, C) -> R,
        tested: (A, B, C) -> R,
        a: A, b: B, c: C,
        funcName: String,
        silentArgs: Boolean = false,
        silentRes: Boolean = false
    ) = test {
        val expected = correct(a, b, c)
        val got = tested(a, b, c)
        if (got != expected) {
            println(
                "Fail: Function $funcName ${if (silentArgs) "" else "with arguments $a, $b, $c"} " +
                        if (silentRes) "" else "returned $got, but $expected expected"
            )
            false
        } else {
            println("Success")
            true
        }
    }
}
