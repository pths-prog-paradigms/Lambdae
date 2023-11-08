fun <T, S> foldList(list: List<T>, initial: S, operation: (S, T) -> S): S {
    var state = initial
    for (element in list) {
        state = operation(state, element)
    }
    return state
}

// Example:
// fun sum(list: List<Int>) = foldList(list, 0, { a, b -> a + b })

// Second example:
// fun <T> joinToString(list: List<T>) = foldList(list, StringBuilder(), { builder, el ->
//     builder.append(el)
//     builder // Returns builder from lambda
// }).toString()

fun <T> containsElement(list: List<T>, element: T): Boolean = TODO()

fun <T> filterList(list: List<T>, predicate: (T) -> Boolean): List<T> = TODO()

fun fibonacci(n: Int) : Int = -1

