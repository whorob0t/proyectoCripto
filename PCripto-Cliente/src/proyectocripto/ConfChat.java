package proyectocripto;

/**
 *
 * @author xrworm
 */

import java.net.*;
import java.io.*;
import java.math.BigInteger;
import java.util.Base64;
import java.util.StringTokenizer;
import java.net.Socket;
import static proyectocripto.VentanaChat.jTextArea1;

public class ConfChat extends Thread {
    
    private Socket s;
    private InputStreamReader entradaSocket;
    private DataOutputStream salida;
    private BufferedReader entrada;
    final int puerto = 8080; //Puerto que se usara para conectarse al servidor (debe ser el mimso puerto configurado en el servidor)
    private RSA rsa;//Creamos el objeto de RSA con lo metodo de RSA
    private String eServer;
    private String nServer;
    private boolean checkKey = false;
    

    public ConfChat(String ip,BigInteger pp, BigInteger qq){
        try{
            rsa = new RSA(pp, qq); //Metodo global en la aplicacion RSA para obtener llave publica y llave privada
            //EL socket permite la comunicacion entre los programas
            this.s = new Socket(ip,this.puerto); //Se asigna la ip"dada por el usuario" y el puerto "Default" al socket             
            this.entradaSocket = new InputStreamReader(s.getInputStream());
            this.entrada = new BufferedReader(entradaSocket);
            String eOwnPub = rsa.getE().toString(); //Obtenemos e del algoritmo RSA declarado anteriormente
            String nOwnPub = rsa.getN().toString(); //Obtenemos n del algoritmo RSA declarado anteriormente
            //  La llave publica consta de e y n 
            String llavePub = "PK" + eOwnPub.length() + ""  + nOwnPub.length() + ""  + eOwnPub + "" + nOwnPub;
            System.out.println("Llave pública del Cliente: " + llavePub);
            enviarLlave(llavePub); // enviamos la llave publica al servidor
            
        }catch (Exception e){};
    }

    public void run(){
        String texto;
        while(true){
            try{
                texto = entrada.readLine();//Resive el mensaje cifrado enviado desde el servidor  
                                
                String textEncrypt=(texto.trim());   //Quita los espacion que pueda haber en el texto   
                String newStr = texto.substring(2, textEncrypt.length()+1); //Quita el primer elemento que se agrega
                             
                if(checkKey == false){
                    String pubkey = textEncrypt;             
                    try{
                        System.out.println("Llave pública del Servidor: " + pubkey);
                        
                        char char1 = pubkey.charAt(0);
                        char char2 = pubkey.charAt(1);

                        if(char1 == 'P' && char2 == 'K'){
                            int pubklen = pubkey.length();
                            char eln = pubkey.charAt(2);
                            char nln = pubkey.charAt(3);
                            int elen = Character.getNumericValue(eln);
                            int nlen = Character.getNumericValue(nln);
                            eServer = pubkey.substring(4, 4+elen); 
                            nServer = pubkey.substring(4+elen, pubklen);
                            
                            checkKey = true;
                            System.out.println("Status Keys: " + checkKey);
                            jTextArea1.append("\n" + "Llaves compartidas. \nEnvia mensajes ahora.");
                        }
                    }catch(Exception ex){
                        System.out.println("Error al obtener la llave");
                    }
                }else{
                    System.out.println("\n\nMensaje BASE 64 Encriptado:  " + newStr);
                    if(texto != "" || texto != null){                  
                        String mensajeDecrypt = null;   

                        try{
                            byte[] digitosDecrypt = Base64.getDecoder().decode(newStr);
                            try{
                                String decrypt = new String(digitosDecrypt);            
                                //System.out.println("Texto desencriptado: " + decrypt);
                                try{
                                    StringTokenizer st=new StringTokenizer(decrypt);
                                    BigInteger[] textoCifrado = new BigInteger[st.countTokens()];
                                    int countBloc = st.countTokens();
                                    
                                    for(int i=0; i<countBloc; i++){
                                        BigInteger letr = new BigInteger(st.nextToken());
                                        textoCifrado[i] = letr;      
                                    }
                                    
                                    mensajeDecrypt=rsa.desencripta(textoCifrado);//Desencriptamos el mensaje lamando a una clase del objeto rsa

                                   try{
                                       System.out.println("Mensaje Desencriptado: " + mensajeDecrypt);
                                       VentanaChat.jTextArea1.append("\n"+"Servidor: "+ mensajeDecrypt); //Imprime el mensaje desencriptado en la pantalla
                                    }
                                    catch(Exception ex){System.out.println("Error sl mostrar mensaje");}
                                }catch(Exception ex){System.out.println("Error al desencriptar");}                            
                            }catch(Exception ex){System.out.println("Error al obtener String de mensje");}
                        }catch(Exception ex){System.out.println("string no Base 64");}
                    }        
                }                                             
            }catch(IOException e){System.out.println("Error en el main de la comunicacion");};
        }
    }
    
 
    public void enviarMSG(String msg){        
        //System.out.println("Enviado");
        try{
            System.out.println( "\nMensaje trado de enviar por el cliente: "+msg);
            String encryptedMessage = rsa.encriptaPUB(msg,eServer,nServer);
            System.out.println("Mensaje encriptado: " + encryptedMessage);           
            this.salida = new DataOutputStream(s.getOutputStream());//Crera flujo de salid            
            this.salida.writeUTF(encryptedMessage+"\n");//Enviamos mensaje
        }
        catch (IOException e){
            System.out.println("Problema al enviar mensaje :c");
        };
    }
    public void enviarLlave(String llavePub){
        
        System.out.println("Enviando llave publica");
        try{
            this.salida = new DataOutputStream(s.getOutputStream());//Crera flujo de salid            
            this.salida.writeUTF(llavePub+"\n");//Enviamos mensaje
        }
        catch (IOException e){
            System.out.println("Problema al enviar llave publica :c");
        };
    }
    
    
    public String leerMSG(){
        try{
            return entrada.readLine();
        }
        catch(IOException e){};
        return null;
    }
}