/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.kohonen;

import java.awt.Color;
import java.awt.Image;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author werrio5
 */
public class SOMMap {

    private final List<CSOMNode> nodes;
    private final Set<Double[]> trainingSet;

    //начальные параметры    
    private final double startLearningRate;
    private final double initRadius;
    private final double timeConstant;
    private final int iterationLimit;
    private final int width;
    private final int height;

    //итерационные параметры
    private int iteration;
    private double learningRate;
    private double curRadius;

    public SOMMap(int width, int height, int vectorLength, double startLearningRate, int iterationLimit) {
        //начальные параметры       
        //ширина и высота
        this.width = width;
        this.height = height;
        
        //начальная скорость обучения
        this.startLearningRate = startLearningRate;
                
        //текущий № итерации
        iteration = 0;
        
        //всего итераций
        this.iterationLimit = iterationLimit;
        
        //половина наибольшего значения (ширины либо высоты)
        initRadius = width > height ? width / 2 : height / 2;

        //постоянная времени
        timeConstant = this.iterationLimit/Math.log(initRadius);
        
        //nodes init
        nodes = new ArrayList<>();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Point position = new Point(x, y);
                CSOMNode node = new CSOMNode(vectorLength, position);
                nodes.add(node);
            }
        }
        
        //training set
        trainingSet = new HashSet<>();
    }

    /**
     * поиск Node с наибольшим значением
     *
     * @return
     */
    private CSOMNode getBMU(Double[] inputVector) {
        CSOMNode BMU = nodes.get(0);
        for (CSOMNode node : nodes) {
            double distance = node.getDistance(inputVector);
            if (distance < BMU.getDistance(inputVector)) {
                BMU = node;
            }
        }
        return BMU;
    }

    /**
     * вычислить область изменения весов 
     */
    private void calcBMURadius(){
        curRadius = initRadius * Math.exp(-(double)iteration/timeConstant);
        System.out.println("rad = "+curRadius);
    }
        
    /**
     * вычислить текущий коэффициент скорости обучения
     */
    private void calcLearningRate(){
        learningRate = startLearningRate * Math.exp(-(double)iteration/iterationLimit);
        System.out.println("LR = "+learningRate);
    }
    
    /**
     * поиск попадающих в область элементов и изменение их
     * весовых коэффициентов
     * 
     * @param BMUPos 
     */
    private void adjustWeights(Point BMUPos, Double[] inputVector) {
        for (CSOMNode node : nodes) {
            //положение текущего
            Point curPos = node.getPosition();
            
            //расстояние до BMU
            double distToBMU = Math.sqrt(Math.pow(curPos.x - BMUPos.x, 2) + Math.pow(curPos.y - BMUPos.y, 2));
            
            //расстояние не больше радиуса -> меняем весовые коэффициенты
            if (distToBMU <= curRadius) {                
                //изменение весовых коэффициентов 
                node.adjustWeight(distToBMU, curRadius, learningRate, inputVector);
            }
        }
    }

    private void calcIterationParameters(){
        calcLearningRate();
        calcBMURadius();
    }
    
    private void iterate(Double[] inputVector) {
        //параметры для итерации
        calcIterationParameters();
                
        //best matching unit
        CSOMNode BMU = getBMU(inputVector);
        
        //расположение BMU в пространстве
        Point BMUPosition = BMU.getPosition();

        //изменить веса
        adjustWeights(BMUPosition, inputVector);

        //номер следующей итерации
        iteration++;
    }
    
    public void addTrainVector(Integer[] vector){
        Double[] dVector = new Double[vector.length];
        for(int i = 0; i < vector.length; i++){
            dVector[i] = vector[i].doubleValue() / 255d;
        }
        trainingSet.add(dVector);
    }
    
    private Double[] getRandomTrainingVector(){
        //random index
        int index = (int) Math.round(Math.random() * (trainingSet.size() - 1));
        
        //random vector
        Double[] randomVector = (Double[]) trainingSet.toArray()[index];
        return randomVector;
    }
    
    public void train(){
        for(int i=0; i<iterationLimit; i++){
            Double[] inputVector = getRandomTrainingVector();
            iterate(inputVector);
        }
    }
    
    public List<CSOMNode> getNodes(){
        return nodes;
    }
    
    public Image weightsToImage(int scale){        
        //image
        BufferedImage im = new BufferedImage(width*scale, height*scale, BufferedImage.TYPE_INT_RGB);
        for(CSOMNode node:nodes){
            //pos
            Point pos = node.getPosition();
            
            //weights
            Double[] weights = node.getWeights();
            
            //weights to color       
            Color color = new Color(weights[0].floatValue(), weights[1].floatValue(), weights[2].floatValue());
            
            //draw pixels
            for(int x = 0; x < scale; x++){
                for(int y = 0; y < scale; y++){                
                    im.setRGB(pos.x * scale + x, pos.y * scale + y, color.getRGB());
                }
            }
        }
        return im;
    }
}
