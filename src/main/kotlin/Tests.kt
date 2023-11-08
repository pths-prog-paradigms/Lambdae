import java.io.File
import java.util.Random
import kotlin.math.roundToLong

fun main() = TestsUtil().run {
    testBlock("Basic") {
        testGroup("Contains/Manual") {
            compare(List<*>::contains, ::containsElement, listOf(1, 2, 3), 1, "containsElement")
            compare(List<*>::contains, ::containsElement, listOf(), 1, "containsElement")
            compare(List<*>::contains, ::containsElement, listOf(1, 2, 3), 5, "containsElement")
        }
        val seed = 5663023956630239L
        val rnd = Random(seed)
        testGroup("Contains/Random light") {
            repeat(100) {
                val input = List(rnd.nextInt(10)) { rnd.nextInt(-10, 11) }
                compare(List<*>::contains, ::containsElement, input, rnd.nextInt(-10, 11), "containsElement")
            }
        }
        testGroup("Contains/Random heavy", listOf("Contains/Manual", "Contains/Random light")) {
            repeat(100) {
                val input = List(rnd.nextInt(100) + if (rnd.nextInt(10) > 0) 1 else 0) { rnd.nextInt(-100, 101) }
                compare(
                    List<*>::contains,
                    ::containsElement,
                    input,
                    rnd.nextInt(-10, 11),
                    "contains",
                    silentArgs = true, silentRes = true
                )
            }
        }

        fun <T> NamedPredicate(name: String, predicate: (T) -> Boolean) = object : (T) -> Boolean {
            override fun invoke(p1: T): Boolean = predicate(p1)
            override fun toString(): String = name
        }
        testGroup("filter/Manual") {
            compare(List<Int>::filter, ::filterList, (1..5).toList(), NamedPredicate("IsOdd") { it % 2 != 0 }, "filterList")
            compare(List<Int>::filter, ::filterList, listOf(), NamedPredicate("IsOdd") { it.toBigInteger().isProbablePrime(50) }, "filterList")
            compare(List<Int>::filter, ::filterList, (1..10).toList(), NamedPredicate("IsPrime") { it.toBigInteger().isProbablePrime(50) }, "filterList")
        }
        testGroup("filter/Random light") {
            repeat(100) {
                val input = List(rnd.nextInt(10)) { rnd.nextInt(-10, 11) }
                compare(List<Int>::filter, ::filterList, input, { it * it % 6 == it % 6 }, "filter")
            }
        }
        testGroup("filter/Random heavy", listOf("filter/Manual", "filter/Random light")) {
            repeat(100) {
                val input = List(rnd.nextInt(100) + if (rnd.nextInt(10) > 0) 1 else 0) { rnd.nextInt(-100, 101) }
                compare(
                    List<*>::filter,
                    ::filterList,
                    input,
                    { it.toString().toSet().size == it.toString().length },
                    "filter",
                    silentArgs = true, silentRes = true
                )
            }
        }
    }
    testBlock("Advanced") {
        Unit
    }
    testBlock("Bonus") {
        testGroup("Manual") {
            val time = System.currentTimeMillis()
            fun fibonacciCor(x: Int): Int = if (x < 2) x else fibonacciCor(x - 2) + fibonacciCor(x - 1)

            repeat(10) {
                compare(::fibonacciCor, ::fibonacci, it, "fibonacci")
            }
            repeat(10) {
                compare({ 1134903170 }, ::fibonacci, 45, "fibonacci")
                compare({ 1836311903 }, ::fibonacci, 46, "fibonacci")
            }
            println("Fibonacci took " + (System.currentTimeMillis() - time) + "ms")
        }
    }

    println()
    println("Results: ")
    fun res(key: String): Double {
        val passed = results.filterKeys { it.first == key }.values.sum()
        val of = total.filterKeys { it.first == key }.values.sum()
        return if (of == 0) 1.0 else passed * 1.0 / of
    }


    indent++
    println("Basic   : ${((res("Basic") * 1000).roundToLong() / 10)}%")
    println("Advanced: ${((res("Advanced") * 1000).roundToLong() / 10)}%")
    println("Bonus   : ${((res("Bonus") * 1000).roundToLong() / 10)}%")
    indent--
//    val file = File("res.txt")
//    if (file.exists()) file.delete()
//    file.createNewFile()
//    file.writeText(
//        "Basic   : ${((res("Basic") * 1000).roundToLong() / 10)}%" + "\n" +
//                "Advanced: ${((res("Advanced") * 1000).roundToLong() / 10)}%" + "\n" +
//                "Bonus   : ${((res("Bonus") * 1000).roundToLong() / 10)}%" + "\n"
//    )
}
