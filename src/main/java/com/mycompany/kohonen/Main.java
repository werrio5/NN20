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
public class Main {
    
    public static void main(String[] args) {
        //frame
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new DrawFrame().setVisible(true);
            }
        });         
    }
}
