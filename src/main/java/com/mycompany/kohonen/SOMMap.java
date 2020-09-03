/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.kohonen;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author werrio5
 */
public class SOMMap {

    private final List<CSOMNode> nodes;

    //начальные параметры    
    private final double startLearningRate;
    private final double initRadius;
    private final double timeConstant;
    private final int iterationLimit;

    //итерационные параметры
    private int iteration;
    private double learningRate;
    private double curRadius;

    public SOMMap(int width, int height, int vectorLength, double startLearningRate, int iterationLimit) {
        //начальные параметры        
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
            if (distance > BMU.getDistance(inputVector)) {
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
    }
        
    /**
     * вычислить текущий коэффициент скорости обучения
     */
    private void calcLearningRate(){
        learningRate = startLearningRate * Math.exp(-iteration/timeConstant);
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
    
    public void iterate(Double[] inputVector) {
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
}
