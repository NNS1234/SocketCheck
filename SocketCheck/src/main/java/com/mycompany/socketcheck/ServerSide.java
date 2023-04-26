//Nudrat Nawal Saber
// 1001733394
package com.mycompany.socketcheck;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;
import java.util.TreeSet;
import javax.swing.JFrame;


public class ServerSide extends javax.swing.JFrame {

    ServerSocket serverSocket = null;
    Socket socket = null;
    boolean ServerOn = true;
    static DataInputStream din;
    static DataOutputStream dout;
    private final int port = 5678;
    public static String currentClient;
    JFrame jf;
    HashSet<String> hset=new HashSet();  
    public ServerSide() {
        initComponents();
        this.setTitle("Server");
        this.setVisible(true);
        this.setResizable(false);
        jf=this;
        // For menubar cross button action
        jf.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                closeServer();
            }
        });
    }
    
    public void start(){
        taserver.setText("Connection Initiated"+"\nListening on port : "+port+ "\n"+"------------------------------\n");
        try
        {
            serverSocket=new ServerSocket(port);
            while(true)
            {
                try
                {
                    updateClients();
                    try{
                        socket=serverSocket.accept();
                        BufferedReader intemp = null; 
                        PrintWriter outtemp = null;
                        intemp = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        outtemp = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
                        String givenName = intemp.readLine();
                        // Removed client starts with !
                        if(givenName.charAt(0)=='!'){
                            removeClient(givenName);
                            updateClients();
                        // New client starts with ?
                        } else if(givenName.charAt(0)=='?'){
                            welcomeClient(givenName);
                            updateClients();
                        } else {
                            updateClients();
                            // Fork new thread for each new client
                            ClientServiceThread cliThread = new ClientServiceThread(socket,givenName);
                            cliThread.start();
                        }                       
                    }catch(Exception e){
                         e.printStackTrace();
                    }   
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }
        catch(IOException ioException){
            ioException.printStackTrace();
        }
    }
    
    private void welcomeClient(String username){
        String welcomeUsername = username.substring(1);
        hset.add(welcomeUsername);
        taserver.append("Client '"+welcomeUsername+"' entered\n------------------------------\n");
    }
    
    private void updateClients(){
        taconn.setText("");
        // Show current clients from clients.txt
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
   
    private void removeClient(String req){
        // Removing client from clients.txt
        // First stoing all names without removing name in temporary arraylist
        String removeUsername = req.substring(1);
        ArrayList<String> temp = new ArrayList<String>();
        try {
            File myObj = new File("clients.txt");
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
              String data = myReader.nextLine();
              if(!data.equals(removeUsername)){
                  temp.add(data);
              }
            }
            myReader.close();
        } catch (FileNotFoundException e) {
          System.out.println("An error occurred.");
          e.printStackTrace();
        }
        // Then clearing the txt file
        try{
            File myObj = new File("clients.txt");
            PrintWriter writer = new PrintWriter(myObj);
            writer.print("");
            writer.close();
            
        }catch(Exception e){
            e.printStackTrace();
        }
        
        // Then adding to the arratylist's name in txt file
        try{
            String filename= "clients.txt";
            FileWriter fw = new FileWriter(filename,true); //the true will append the new data
            for (int i = 0; i < temp.size(); i++) {
                fw.write(temp.get(i)+"\n");//appends the string to the file
              } 
            fw.close(); 
        }
        catch(IOException ioe){
            System.err.println("IOException: " + ioe.getMessage());
        }
        taserver.append("Client '"+removeUsername+"' left\n");
        taserver.append("------------------------------\n");
        hset.remove(removeUsername);
        updateClients();
    }
    class ClientServiceThread extends Thread { 
		Socket myClientSocket;
		boolean m_bRunThread = true;
		BufferedReader in = null; 
		PrintWriter out = null;
		String textFileLine, serverResponse="";
		ArrayList<String> clientWords;
                String curUsername;

		public ClientServiceThread() { 
                    super();     	
		} 
		
		ClientServiceThread(Socket s,String username) { 
			myClientSocket = s; 
                        curUsername = username;
		} 

		@Override
		public void run() { 
			try { 
				in = new BufferedReader(new InputStreamReader(myClientSocket.getInputStream()));
				out = new PrintWriter(new OutputStreamWriter(myClientSocket.getOutputStream()));
                                
				String clientRequest = in.readLine();
                                taserver.append("Client '"+curUsername+"' Request > " + clientRequest+ " \n");
				
                                // Spliting names from client's input
                                clientWords = new ArrayList<String>(Arrays.asList(clientRequest.split(" ")));

                                // Open the Dictionary File for checking all the words
                                FileInputStream fstream = new FileInputStream("lexicon.txt");
                                BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
                                //Read every lexicon words
                                while ((textFileLine = br.readLine()) != null)   {
                                        textFileLine = textFileLine.toLowerCase();
                                        String s[]=textFileLine.split("[ ]+");
                                        TreeSet<String> ts = new TreeSet<String>(); 
                                        for(int i=0;i<s.length;i++){
                                            ts.add(s[i]);
                                        }
                                        
                                        if(!clientWords.isEmpty()){

                                                for (String element : clientWords) {
                                                    // Adding the result to final string
                                                    if(ts.contains(element.toLowerCase())){
                                                        serverResponse+="["+element+"] ";
                                                    } else {
                                                        serverResponse+=element+" ";
                                                    }
                                                }
                                        }else{
                                                serverResponse = textFileLine;
                                                break;
                                        }
                                }
                                //Close the input stream
                                br.close();

                                // Show message in server window
                                taserver.append("Response > " + serverResponse+ " \n");
                                taserver.append("------------------------------\n");
                                // Send responce back to client
                                out.println(serverResponse);
                                out.flush();
                                // Clearing variables
                                serverResponse = "";
                                clientWords.clear();
				
			} catch(Exception e) { 
				e.printStackTrace(); 
			} 
			finally { 
                                // Closing input output
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
    private void closeServer(){
        try {
                if(socket == null ){
                        serverSocket.close();
                }else{     
                        socket.close();
                        serverSocket.close();
                }
                ServerOn = false;
                System.exit(0);
        } catch (IOException e) {
               
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
        serverexit = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        taserver.setEditable(false);
        taserver.setColumns(20);
        taserver.setFont(new java.awt.Font("Monospaced", 0, 14)); // NOI18N
        taserver.setRows(5);
        jScrollPane1.setViewportView(taserver);

        taconn.setEditable(false);
        taconn.setColumns(20);
        taconn.setFont(new java.awt.Font("Monospaced", 0, 14)); // NOI18N
        taconn.setRows(5);
        jScrollPane2.setViewportView(taconn);

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel1.setText("Server");

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel2.setText("Connected Clients");

        serverexit.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        serverexit.setText("Exit");
        serverexit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                serverexitActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 392, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(154, 154, 154)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 37, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(serverexit, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 224, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(36, 36, 36))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(17, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(serverexit, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 279, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void serverexitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_serverexitActionPerformed
        // TODO add your handling code here:
        closeServer();
    }//GEN-LAST:event_serverexitActionPerformed

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
                //new ServerSide().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JButton serverexit;
    private javax.swing.JTextArea taconn;
    private javax.swing.JTextArea taserver;
    // End of variables declaration//GEN-END:variables
}
