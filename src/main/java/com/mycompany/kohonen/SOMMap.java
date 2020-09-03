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
    private final int mapWidth;
    private final int mapHeight;
    private final double startLearningRate;
    private final double initRadius;
    
    //итерационные параметры
    private int iteration; 
    private double timeConstant;
    private double learningRate;
    private double curRadius;
     
    
    public SOMMap(int width, int height, int vectorLength, double startLearningRate){
        //начальные параметры
        mapWidth = width;
        mapHeight = height;
        this.startLearningRate = startLearningRate;
        initRadius = width > height ? width/2 : height/2;
        
        //№ итерации
        iteration = 0;  
        //nodes
        nodes = new ArrayList<>();
        for(int x=0; x<width; x++){
            for(int y=0; y<height; y++){
                Point position = new Point(x,y);
                CSOMNode node = new CSOMNode(vectorLength, position);
                nodes.add(node);
            }
        }
    }
    
    /**
     * поиск Node с наибольшим значением 
     * @return 
     */
    private CSOMNode getBMU(Double[] inputVector){
        CSOMNode BMU = nodes.get(0);
        for(CSOMNode node:nodes){
            double distance = node.getDistance(inputVector);
            if(distance>BMU.getDistance(inputVector)){
                BMU = node;
            }
        }
        return BMU;
    }
    
    /**
     * вычислить область изменения весов
     * @param width
     * @param height
     * @return 
     */
    private void calcBMURadius(){
        //текущий
        curRadius = initRadius * Math.exp(-(double)iteration/timeConstant);
    }
    
    private void calcTimeConstant(){
        timeConstant = iteration/Math.log(curRadius);
    }
    
    private void calcLearningRate(){
        learningRate = startLearningRate * Math.exp(-iteration/timeConstant);
    }
    
    private void adjustWeights(double BMURadius, Point BMUPos){
        for(CSOMNode node:nodes){
            //положение текущего
            Point curPos = node.getPosition();
            //расстояние до BMU
            double distToBMU = Math.sqrt(Math.pow(curPos.x - BMUPos.x, 2) + Math.pow(curPos.y - BMUPos.y, 2));
            //расстояние не больше радиуса -> меняем веса
            if(distToBMU<=BMURadius){
                //величина изменения в зависимости от расстояния до BMU
                double influence = calcInfluence(distToBMU, BMURadius);
                node.adjustWeight();
            }
        }
    }
    
    private double calcInfluence(double distToBMU, double curRadius){
        double influence = Math.exp(-(Math.pow(distToBMU, 2)) / (2*Math.pow(curRadius, 2)));
        return influence;
    }
    
    private void calcIterationParameters(){
        calcTimeConstant();
        calcLearningRate();
        calcBMURadius();
    }
    
    public void iterate(Double[] inputVector){    
        //новые параметры в итерации
        calcIterationParameters();
        //best matching unit
        CSOMNode BMU = getBMU(inputVector);
        //расположение BMU в пространстве
        Point BMUPosition = BMU.getPosition();
        //изменить веса
        //List<CSOMNode> neighbourNodes = getNeighbourNodes(BMURadius,BMUPosition);
    }
}
