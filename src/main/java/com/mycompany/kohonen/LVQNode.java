/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.kohonen;

import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author werrio5
 */
public class LVQNode {
    
    private double[] weights;
    private String description;
    private List<String> debug;
    
    public LVQNode(){
        description = "";
    }
    
    public LVQNode(int weigthsCount, double[] weights){
        //weights init
        this.weights = weights;
        for(int i=0; i<weigthsCount; i++){
            weights[i] = Math.random();
        }
        
        //
        description = "";
        
        debug = new LinkedList<>();
        debug.add(weights[0]+"");
    }
    
    public double getSqrDistance(double x, int weightIndex){
        return Math.pow(x - weights[weightIndex], 2);
    }
    
    public void adjustWeights(int BMUIndex, double x, double learningRate){
        double d = 0;
        for(int i=0; i<weights.length; i++){
            d = learningRate * (x - weights[i]);
            if(i == BMUIndex){
                weights[i] = weights[i] + d;
            }
            else{
                //weights[i] = weights[i] - d;
            }
        }
        debug.add(weights[0]+", d="+d);
    }
    
    public void addDescription(String description){
        this.description += description+" ";
    }
    
    public String getDescription(){
        return description;
    }
    
    public double[] getWeigths(){
        return weights;
    }
}
