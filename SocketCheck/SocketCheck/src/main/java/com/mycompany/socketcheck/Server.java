/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.socketcheck;

import java.io.File;
import java.io.PrintWriter;
import java.util.TreeSet;


public class Server {
   
    
    public static void main(String[] args) {
        try{
            File myObj = new File("clients.txt");
            if (myObj.createNewFile()) {
              System.out.println("File created: " + myObj.getName());
            } else {
                PrintWriter writer = new PrintWriter(myObj);
                writer.print("");
                writer.close();
                System.out.println("File already exists.");
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        
        System.out.println("Called");
        ServerSide sside=new ServerSide();
        sside.start();
    }
}
