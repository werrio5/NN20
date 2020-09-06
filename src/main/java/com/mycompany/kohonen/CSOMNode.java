/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.kohonen;

import java.awt.Point;

/**
 *
 * @author werrio5
 */
public class CSOMNode {
    //веса
    private final Double[] weights;
    
    //позиция
    private final Point position;
    
    public CSOMNode(int vectorLength, Point position){
        //инициализация весов случ. значениями
        weights = new Double[vectorLength];
        for(int i=0; i <vectorLength; i++){
            weights[i] = Math.random();
        }
        
        //node pos
        this.position = position;
    }
    
    public double getDistance(Double[] inputVector){
        double distance = 0;
        
        for(int i=0; i<weights.length; i++){
            distance+= (inputVector[i] - weights[i]) * (inputVector[i] - weights[i]);
        }
        
        return distance;
    }
    
    public Point getPosition(){
        return position;
    }
    
    /**
     * формулы  
     * http://ai-junkie.com/ann/som/som4.html
     * W(t+1) = w(t) + Θ(t) * L(t) * (V(t) - W(t))
     *       
     *                      Θ - amount of influence a node's distance from the BMU has on its learning
     * @param dist          d - distance to BMU
     * @param curRadius     R - current radius
     * @param learningRate  L - learning rate
     * @param inputVector   V - input vector
     */
    public void adjustWeight(double dist, double curRadius, double learningRate, Double[] inputVector){
        //calc Θ
        double theta = calcTheta(dist, curRadius);
        
        //пересчет весов
        for(int i=0; i<weights.length; i++){
            double newWeight = weights[i] + theta * learningRate * (inputVector[i] - weights[i]);
            weights[i] = newWeight;
        }
    }
    
    /**
     * Θ = exp[ - dist^2/(2curRadius^2) ]
     * @param dist
     * @param curRadius
     * @return 
     */
    private double calcTheta(double dist, double curRadius){
        double theta = Math.exp(-Math.pow(dist, 2)/(2*Math.pow(curRadius, 2)));
        return theta;
    }
    
    public Double[] getWeights(){
        return weights;
    }
    
    public void draw(int scale){
        
    }
}
