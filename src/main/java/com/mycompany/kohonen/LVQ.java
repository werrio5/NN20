/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.kohonen;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import jdk.nashorn.internal.objects.Global;

/**
 *
 * @author werrio5
 */
public class LVQ {

    private final List<LVQNode> inputLayer;
    private final List<LVQNode> outputLayer;
    private final Set<Double[]> trainingSet;
    private final Map<Double[], String> labels;

    private final double startLearningRate;
    private final int iterationLimit;

    //итерационные параметры
    private int iteration;
    private double learningRate;

    public LVQ(int vectorLength, int numClasses, double startLearningRate, int iterationLimit) {
        //training set
        trainingSet = new HashSet<>();

        //labels
        labels = new HashMap<>();

        //начальная скорость обучения
        this.startLearningRate = startLearningRate;

        //текущий № итерации
        iteration = 0;

        //всего итераций
        this.iterationLimit = iterationLimit;

        //output layer
        outputLayer = new LinkedList<>();
        for (int i = 0; i < numClasses; i++) {
            outputLayer.add(new LVQNode());
        }

        //input layer
        inputLayer = new LinkedList<>();
        for (int i = 0; i < vectorLength; i++) {
            inputLayer.add(new LVQNode(outputLayer.size()));
        }

    }

    public void addTrainVector(Integer[] vector, String title) {
        Double[] dVector = new Double[vector.length];
        for (int i = 0; i < vector.length; i++) {
            dVector[i] = vector[i].doubleValue() / 255d;
        }
        trainingSet.add(dVector);
        labels.put(dVector, title);
    }

    private Double[] getRandomTrainingVector() {
        //random index
        int index = (int) Math.round(Math.random() * (trainingSet.size() - 1));

        //random vector
        Double[] randomVector = (Double[]) trainingSet.toArray()[index];
        return randomVector;
    }

    private void calcIterationParameters() {
        calcLearningRate();
    }

    /**
     * вычислить текущий коэффициент скорости обучения
     */
    private void calcLearningRate() {
        learningRate = startLearningRate * Math.exp(-(double) iteration / iterationLimit);
    }

    private LVQNode getBMU(Double[] inputVector) {
        LVQNode BMU = outputLayer.get(0);
        double curMinDistance = Global.Infinity;

        for (int i = 0; i < outputLayer.size(); i++) {
            double distance = 0;
            for (int j = 0; j < inputLayer.size(); j++) {
                distance += inputLayer.get(j).getSqrDistance(inputVector[j], i);
            }
            if (distance < curMinDistance) {
                BMU = outputLayer.get(i);
                curMinDistance = distance;
            }
        }
        return BMU;
    }

    private void adjustWeights(int BMUIndex, Double[] inputVector) {
        for (int i = 0; i < inputLayer.size(); i++) {
            inputLayer.get(i).adjustWeights(BMUIndex, inputVector[i], learningRate);
        }
    }

    private void iterate(Double[] inputVector) {
        //параметры для итерации
        calcIterationParameters();

        //best matching unit
        LVQNode BMU = getBMU(inputVector);
        int BMUIndex = outputLayer.indexOf(BMU);

        //изменить веса
        adjustWeights(BMUIndex, inputVector);

        //номер следующей итерации
        iteration++;
    }

    private void attachLabels() {

        //метки
        Set<String> labelsSet = new HashSet<>(labels.values());

        for (String label : labelsSet) {

            //самый часто встречающийся узел
            int[] count = new int[outputLayer.size()];
                    
            for (Double[] trainVector : trainingSet) {
                if (labels.get(trainVector).equals(label)) {

                    //индекс узла
                    LVQNode BMU = getBMU(trainVector);
                    int nodeIndex = outputLayer.indexOf(BMU);

                    //+1
                    count[nodeIndex]++;
                }
            }

            //поиск самого часто встречающегося узла
            int max = -1;
            LVQNode maxNode = null;
            for (int i=0;i<count.length;i++) {
                if (count[i] > max) {
                    max = count[i];
                    maxNode = outputLayer.get(i);
                }
            }

            maxNode.addDescription(label);
        }

    }

    /**
     * обучение
     */
    public void train() {

        for (int i = 0; i < iterationLimit; i++) {
            //случайный вектор из обучающего набора
            Double[] inputVector = getRandomTrainingVector();

            //следующая итерация
            iterate(inputVector);

            //
            System.out.println("iteration = " + iteration);
        }
        attachLabels();
        //
        System.out.println("=============");
        for(int i=0;i<outputLayer.size();i++){
            String descr = outputLayer.get(i).getDescription();
            System.out.println(i+") "+descr);
        }
        System.out.println("=============");
    }

    public void inputTestData(Collection<Integer[]> input, String fname){
        
        Integer[] results = new Integer[outputLayer.size()];
        int sum = 0;
        
        for(Integer[] vector:input){
            Double[] dvector = {(double) vector[0] / 255d, (double) vector[1] / 255d, (double) vector[2] / 255d};
            
            LVQNode BMU = getBMU(dvector);
            int index = outputLayer.indexOf(BMU);
            //+1
            results[index]++;
            sum++;
        }
        
        int maxIndex = 0;
        int max = -1;
        for(int i=0;i<results.length;i++){
            if(results[i]>max){
                max = results[i];
                maxIndex = i;
            }
        }
        
        String description = outputLayer.get(maxIndex).getDescription();
        System.out.println(input.size()+" из файла "+fname+" распознаны как "+description);
    }
}
