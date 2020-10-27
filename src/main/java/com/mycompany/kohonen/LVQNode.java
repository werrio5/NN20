/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.kohonen;

/**
 *
 * @author werrio5
 */
public class LVQNode {
    
    private Double[] weights;
    private String description;
    
    public LVQNode(){
        description = "";
    }
    
    public LVQNode(int weigthsCount){
        //weights init
        weights = new Double[weigthsCount];
        for(int i=0; i<weigthsCount; i++){
            weights[i] = Math.random();
        }
        
        //
        description = "";
    }
    
    public double getSqrDistance(double x, int weightIndex){
        return Math.pow(x - weights[weightIndex], 2);
    }
    
    public void adjustWeights(int BMUIndex, double x, double learningRate){
        for(int i=0; i<weights.length; i++){
            if(i == BMUIndex){
                weights[i] = weights[i] + learningRate * (x - weights[i]);
            }
            else{
                weights[i] = weights[i] - learningRate * (x - weights[i]);
            }
        }
    }
    
    public void addDescription(String description){
        this.description += description+" ";
    }
    
    public String getDescription(){
        return description;
    }
}
