package bot.neuralbot;

import com.sun.corba.se.impl.orbutil.ObjectWriter;

import java.io.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

public class NeuralNetwork implements Serializable {

    private static float serialVersionUID = 42;

    private double[][] layers;
    private double[][][] weigth;
    private Function<Double, Double> activation;

    protected NeuralNetwork(){
    }

    public NeuralNetwork(ActivationFunction activationFunction, int... sizes){
        if(sizes.length < 2) throw new IllegalArgumentException();

        activation = activationFunction.function;

        layers = new double[sizes.length][];
        weigth = new double[sizes.length-1][][];
        for(int i = 0; i < layers.length; i++){
            if(sizes[i] < 1) throw new IllegalArgumentException();
            if(i == layers.length-1) layers[i] = new double[sizes[i]];
            else layers[i] = new double[sizes[i]+1];
            if(i == 0) continue;
            weigth[i-1] = new double[layers[i].length][];
            for(int j = 0; j < layers[i].length; j++){
                weigth[i-1][j] = new double[layers[i-1].length];
                for(int k = 0; k < weigth[i-1][j].length; k++){
                    weigth[i-1][j][k] = Math.random()*2-1;
                }
            }
        }
    }

    public double[] calculate(Double... inputs){
        return calculate(Arrays.asList(inputs));
    }

    public double[] calculate(List<Double> inputs){
        if (inputs.size() != layers[0].length-1) throw new IllegalArgumentException(
                String.format("Size of %d required but size of %d given", layers[0].length-1, inputs.size())
        );

        layers[0][0] = 1;
        for(int i = 1; i < layers[0].length; i++){
            layers[0][i] = inputs.get(i-1);
        }

        for(int i = 1; i < layers.length; i++){
            layers[i][0] = 1;
            int j = i == layers.length-1 ? 0 : 1;
            for(; j < layers[i].length; j++){
                double sum = 0;
                for(int k = 0; k < layers[i-1].length; k++){
                    double l = layers[i-1][k];
                    double w = weigth[i-1][j][k];
                    sum += l * w;
                }
                layers[i][j] = activation.apply(sum);
            }
        }

        return Arrays.copyOf(layers[layers.length-1], layers[layers.length-1].length);
    }

    public double train(int maxIter, double learningRate, double accuracy, List<List<Double>> inputs, List<List<Double>> expected, BiFunction<double[], double[], Boolean> accuracyFunc) {
        if(accuracy < 0 || accuracy > 1) throw new IllegalArgumentException();
        if(inputs.size() != expected.size()) throw new IllegalArgumentException();

        double acc = 0;
        for (int i = 0; i < maxIter && acc < accuracy ; i++){
            acc = 0;
            for(int j = 0; j < inputs.size(); j++){
                double[] rep = calculate(inputs.get(j));
                List<Double> exp = expected.get(j);
                double[] caca = new double[exp.size()];
                for(int k = 0; k < caca.length; k++){
                    caca[k] = exp.get(k);
                }
                if(accuracyFunc.apply(rep, caca)) acc++;
                backPropagation(learningRate, exp);
            }
            acc /= expected.size();
        }

        return acc;
    }

    private void backPropagation(double learningRate, List<Double> expected){
        double[][] error = new double[layers.length][];
        for(int l = 0; l < error.length; l++){
            error[l] = new double[layers[l].length];
        }

        for(int i = 0; i < layers[layers.length-1].length; i++){
            double y = layers[layers.length-1][i];
            error[error.length-1][i] = y*(1-y)*(y-expected.get(i));
        }

        for(int n = layers.length-1; n > 0; n--){ // n
            for(int j = 0; j < layers[n-1].length; j++){ // j
                double sum = 0;
                for(int i = 0; i < error[n].length; i++){ // i
                    sum += weigth[n-1][i][j]*error[n][i];
                }
                double y = layers[n-1][j];
                error[n-1][j] = y*(1-y)*sum;
            }
        }

        for(int l = 0; l < weigth.length; l++){
            for(int i = 0; i < weigth[l].length; i++){ // i
                for(int j = 0; j < weigth[l][i].length; j++){ // j
                    double precWeight = weigth[l][i][j];
                    double err = error[l+1][i];
                    double val = layers[l][j];
                    weigth[l][i][j] = precWeight - learningRate * err*val;
                }
            }
        }
    }

    public double train(double learningRate, double accuracy, List<List<Double>> inputs, List<List<Double>> expected, BiFunction<double[], double[], Boolean> accuracyFunc){
        return this.train(1000, learningRate, accuracy, inputs, expected, accuracyFunc);
    }

    public void saveNetwork(File f) throws IOException {
        FileOutputStream fileOut = new FileOutputStream(f);
        ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
        objectOut.writeObject(this);
        objectOut.close();
    }

    public static NeuralNetwork loadNetwork(File f) throws IOException, ClassNotFoundException {
        FileInputStream fileIn = new FileInputStream(f);
        ObjectInputStream objectIn = new ObjectInputStream(fileIn);
        NeuralNetwork nn = (NeuralNetwork) objectIn.readObject();
        objectIn.close();
        return nn;
    }

    public enum ActivationFunction{
        Sigmoid(aDouble -> 1/(1+Math.exp(-aDouble))),
        Heavyside(aDouble -> {
            if(aDouble < 0) return 0.;
            else return 1.;
        });

        public final Function<Double, Double> function;

        ActivationFunction(Function<Double, Double> function){
            this.function = function;
        }
    }
}
