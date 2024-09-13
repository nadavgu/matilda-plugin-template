package org.matilda.template;

import org.matilda.commands.MatildaCommand;
import org.matilda.commands.MatildaService;
import org.matilda.template.protobuf.Exercise;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@MatildaService
public class MathService {
    @MatildaCommand
    public int square(int number) {
        return number * number;
    }

    @MatildaCommand
    public int sum(int first, int second) {
        return first + second;
    }

    @MatildaCommand
    public int div(int first, int second) {
        return first / second;
    }

    @MatildaCommand
    public int multiSum(List<Integer> values) {
        return values.stream().reduce(0, this::sum);
    }

    @MatildaCommand
    public List<Integer> factorize(int value) {
        List<Integer> values = new ArrayList<>();
        int maxFactor = (int) Math.sqrt(value);
        int remainingValue = value;
        for (int i = 2; i <= maxFactor && i < remainingValue; i++) {
            while (remainingValue % i == 0) {
                values.add(i);
                remainingValue /= i;
            }
        }
        if (remainingValue > 1) {
            values.add(remainingValue);
        }
        return values;
    }

    @MatildaCommand
    public List<Integer> map(FunctionService function, List<Integer> values) {
        return values.stream().map(function::apply).collect(Collectors.toList());
    }

    @MatildaCommand
    public FunctionService createAdder(int amount) {
        return (value) -> value + amount;
    }

    @MatildaCommand
    public double solveExercise(Exercise exercise) {
        switch (exercise.getOperation()) {
            case ADD:
                return exercise.getFirstOperand() + exercise.getSecondOperand();
            case SUBTRACT:
                return exercise.getFirstOperand() - exercise.getSecondOperand();
            case MULTIPLY:
                return exercise.getFirstOperand() * exercise.getSecondOperand();
            case DIVIDE:
                return exercise.getFirstOperand() / exercise.getSecondOperand();
            default:
                throw new IllegalArgumentException("Unsupported operation: " + exercise.getOperation());
        }
    }
}
