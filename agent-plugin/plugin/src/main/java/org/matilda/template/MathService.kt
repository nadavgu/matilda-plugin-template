package org.matilda.template

import org.matilda.commands.MatildaCommand
import org.matilda.commands.MatildaService
import org.matilda.template.protobuf.Exercise
import org.matilda.template.protobuf.Operation
import java.util.stream.Collectors

@MatildaService
class MathService {
    @MatildaCommand
    fun square(number: Int): Int {
        return number * number
    }

    @MatildaCommand
    fun sum(first: Int, second: Int): Int {
        return first + second
    }

    @MatildaCommand
    fun div(first: Int, second: Int): Int {
        return first / second
    }

    @MatildaCommand
    fun multiSum(values: List<Int>): Int {
        return values.stream().reduce(0) { first: Int, second: Int -> this.sum(first, second) }
    }

    @MatildaCommand
    fun factorize(value: Int): List<Int> {
        val values: MutableList<Int> = ArrayList()
        val maxFactor = Math.sqrt(value.toDouble()).toInt()
        var remainingValue = value
        var i = 2
        while (i <= maxFactor && i < remainingValue) {
            while (remainingValue % i == 0) {
                values.add(i)
                remainingValue /= i
            }
            i++
        }
        if (remainingValue > 1) {
            values.add(remainingValue)
        }
        return values
    }

    @MatildaCommand
    fun map(function: FunctionService, values: List<Int?>): List<Int> {
        return values.stream().map { value: Int? ->
            function.apply(
                value!!
            )
        }.collect(Collectors.toList())
    }

    @MatildaCommand
    fun createAdder(amount: Int): FunctionService {
        return object : FunctionService {
            override fun apply(value: Int): Int {
                return value + amount
            }
        }
    }

    @MatildaCommand
    fun solveExercise(exercise: Exercise): Double {
        return when (exercise.operation) {
            Operation.ADD -> exercise.firstOperand + exercise.secondOperand
            Operation.SUBTRACT -> exercise.firstOperand - exercise.secondOperand
            Operation.MULTIPLY -> exercise.firstOperand * exercise.secondOperand
            Operation.DIVIDE -> exercise.firstOperand / exercise.secondOperand
            else -> throw IllegalArgumentException("Unsupported operation: " + exercise.operation)
        }
    }
}
