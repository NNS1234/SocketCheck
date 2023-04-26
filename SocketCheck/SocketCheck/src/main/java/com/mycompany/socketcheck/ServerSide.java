/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.socketcheck;

import static com.mycompany.socketcheck.CreateClient.Username;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.TreeSet;


public class ServerSide extends javax.swing.JFrame {

    /**
     * Creates new form ServerSide
     */
    ServerSocket serverSocket = null;
    Socket socket = null;
    boolean ServerOn = true;
    static DataInputStream din;
    static DataOutputStream dout;
    private final int port = 5678;
    public ServerSide() {
        initComponents();
        this.setTitle("Server");
        this.setVisible(true);
    }
    public void start(){
        int i=0;
        taserver.setText("Connection Initiated"+"\nListening on port : "+port+ "\n");
        try
        {
            serverSocket=new ServerSocket(port);
            while(true)
            {
                try
                {
                    updateClients();
                    taserver.append("\nWaiting for Someone to Connect..."+i+"\n");
                    i++;
                    try{
                        socket=serverSocket.accept();
                        taserver.append("\nNow Connected to "+socket.getInetAddress().getHostName());
                        updateClients();
                        ClientServiceThread cliThread = new ClientServiceThread(socket);
			cliThread.start();
                        
                    }catch(Exception e){
                         e.printStackTrace();
                    }
                    
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }
        catch(IOException ioException)
        {
            ioException.printStackTrace();
        }
    }
    private void updateClients(){
        taconn.setText("");
        try {
                File myObj = new File("clients.txt");
                Scanner myReader = new Scanner(myObj);
                while (myReader.hasNextLine()) {
                  String data = myReader.nextLine();
                  taconn.append(data+"\n");
                }
                myReader.close();
            } catch (FileNotFoundException e) {
              System.out.println("An error occurred.");
              e.printStackTrace();
            }
    }
    class ClientServiceThread extends Thread { 
		Socket myClientSocket;
		boolean m_bRunThread = true;
		BufferedReader in = null; 
		PrintWriter out = null;
		String textFileLine, serverResponse="";
		ArrayList<String> clientWords;

		public ClientServiceThread() { 
			super(); 
		} 
		/* Constructor accpets the socket number on which data tranmission will be happening */
		ClientServiceThread(Socket s) { 
			myClientSocket = s; 
		} 

		@Override
		public void run() { 
			try { 
				in = new BufferedReader(new InputStreamReader(myClientSocket.getInputStream()));
				out = new PrintWriter(new OutputStreamWriter(myClientSocket.getOutputStream()));

				String clientRequest = in.readLine();
				taserver.append("Client Request >" + clientRequest+ " \n");
				
				/* Non-Case Sensitive Approach For Comparison */
				clientWords = new ArrayList<String>(Arrays.asList(clientRequest.toLowerCase().split(" ")));

				// Open the Dictionary File for checking all the words
				FileInputStream fstream = new FileInputStream("lexicon.txt");
				BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
				//Read Dictionary File Line By Line
				while ((textFileLine = br.readLine()) != null)   {
                                        
					textFileLine = textFileLine.toLowerCase();
                                        //System.out.println("Words : "+textFileLine);
                                        String s[]=textFileLine.split("[ ]+");
                                        TreeSet<String> ts = new TreeSet<String>(); 
                                        for(int i=0;i<s.length;i++)
                                        {
                                            ts.add(s[i]);
                                        }
                                        int sz=ts.size();
					/* Checking if all words are matched from client request */
					if(!clientWords.isEmpty()){

						for (String element : clientWords) {
                                                    if(ts.contains(element)){
                                                        serverResponse+="["+element+"] ";
                                                        System.out.print("["+element+"] ");
                                                    } else {
                                                        serverResponse+=element+" ";
                                                        System.out.print(element+" ");
                                                    }
                                                    
                                                  
						}
						System.out.println("");
						

					}else{
						serverResponse = textFileLine;
						break;
					}
				}
				//Close the input stream
				br.close();

				// Sending Response Back to the Client
				taserver.append("Server Response >" + serverResponse+ " \n");
				taserver.append("-------------------------------------- \n");
				out.println(serverResponse);
				out.flush();
				/* Clearing up variable Here*/
				serverResponse = "";
				clientWords.clear();
			} catch(Exception e) { 
				e.printStackTrace(); 
			} 
			finally { 
				try {
					in.close(); 
					out.close(); 
					myClientSocket.close(); 
				} catch(IOException ioe) { 
					ioe.printStackTrace(); 
				} 
			} 
		} 
	} 
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        taserver = new javax.swing.JTextArea();
        jScrollPane2 = new javax.swing.JScrollPane();
        taconn = new javax.swing.JTextArea();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        taserver.setColumns(20);
        taserver.setRows(5);
        jScrollPane1.setViewportView(taserver);

        taconn.setColumns(20);
        taconn.setRows(5);
        jScrollPane2.setViewportView(taconn);

        jLabel1.setText("Server");

        jLabel2.setText("Connected Clients");

        jButton1.setText("Exit");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 235, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 100, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap())
                    .addComponent(jButton1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 11, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 279, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 166, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE))))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(ServerSide.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ServerSide.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ServerSide.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ServerSide.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ServerSide().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextArea taconn;
    private javax.swing.JTextArea taserver;
    // End of variables declaration//GEN-END:variables
}
