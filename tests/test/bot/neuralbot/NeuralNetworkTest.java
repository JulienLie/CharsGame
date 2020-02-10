package test.bot.neuralbot;

import bot.neuralbot.NeuralNetwork;
import chars.Chars;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.*;
import java.util.function.BiFunction;

import static org.junit.jupiter.api.Assertions.*;

class NeuralNetworkTest {

    private static NeuralNetwork nn;
    private static List<List<Double>> trainSet;
    private static List<List<Double>> expectedSet;

    @BeforeAll
    static void setup(){
        nn = new NeuralNetwork(NeuralNetwork.ActivationFunction.Sigmoid, 15, 11, 5, 6);
        trainSet = new ArrayList<>();
        expectedSet = new ArrayList<>();
        try {
            LinkedList<Double> data = new LinkedList<>();
            BufferedReader reader = new BufferedReader(new FileReader("info.txt"));
            String line;
            for(int i = 0; (line = reader.readLine()) != null; i+=3){
                String[] vals = line.split(" ");
                data.add(Double.parseDouble(vals[0])/600);
                data.add(Double.parseDouble(vals[1])/600);
                data.add(Double.parseDouble(vals[2])/600);

                ArrayList<Double> in = new ArrayList<>(15);
                for(int j = 14; j >= 0; j--){
                    int pos = i+2-j;
                    if(pos < 0) in.add(0.);
                    else in.add(data.get(pos));
                }
                trainSet.add(in);

                ArrayList<Double> res = new ArrayList<>();
                for(Chars.Action a : Chars.Action.values()){
                    if(a.toString().equals(vals[3])) res.add(1.);
                    else res.add(0.);
                }
                expectedSet.add(res);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @BeforeEach
    public void reloadNetwork(){
        nn = new NeuralNetwork(NeuralNetwork.ActivationFunction.Sigmoid, 15, 16, 10, Chars.Action.values().length);
    }

    @RepeatedTest(value = 10)
    public void train(){
        double accuracy = 0.8;
        double prec = nn.train(1, accuracy, trainSet, expectedSet, (calc, expected) -> {
            int maxCalc = 0;
            int maxExpe = 0;
            for(int i = 0; i < calc.length; i++){
                if(calc[maxCalc] < calc[i]) maxCalc = i;
                if(expected[maxExpe] < expected[i]) maxExpe = i;
            }
            System.out.println("calc: " + Arrays.toString(calc));
            System.out.println("expected: " + Arrays.toString(expected));
            System.out.println(maxCalc == maxExpe);
            System.out.println();
            return maxCalc == maxExpe;
        });
        System.out.println(prec);
        assertTrue(prec >= accuracy, "" + prec);
    }
}