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

public class ConfChat extends Thread {
    
    private Socket s;
    private InputStreamReader entradaSocket;
    private DataOutputStream salida;
    private BufferedReader entrada;
    final int puerto = 8080; //Puerto que se usara para conectarse al servidor (debe ser el mimso puerto configurado en el servidor)
    public RSA rsa;//Creamos el objeto de RSA con lo metodo de RSA
    BigInteger publicKey ;//Nustra llave publica
    BigInteger privateKey;//Nuestra lave privad
    BigInteger modulus ;//Modulo
    

    public ConfChat(String ip,String pl, String pr, String m){
        try{
            rsa = new RSA(publicKey, privateKey, 10);
            //EL socket permite la comunicacion entre los programas
            this.s = new Socket(ip,this.puerto); //Se asigna la ip"dada por el usuario" y el puerto "Default" al socket             
            this.entradaSocket = new InputStreamReader(s.getInputStream());
            this.entrada = new BufferedReader(entradaSocket);              
        }catch (Exception e){};
    }

    public void run(){
        String texto;
        while(true){
            try{
                texto = entrada.readLine();//Resive el mensaje cifrado del servidor              
                String pkey = texto.trim();                 
                System.out.println(texto);
                //Leer el mensjae de entrada
                
                for(int i=0; i<pkey.length();i++){
                    char char1 = pkey.charAt(1);
                    char char2 = pkey.charAt(2);
                    if(char1 == 'P' && char2 == 'K'){
                        int pklen = pkey.length();
                        String substring = pkey.substring(4, pklen);
                        System.out.println("llava Publica del cliente: " + substring);
                    }else{
                        break;
                    }
                }
                
                if(texto != "" || texto != null){
             
                    // (11234) ( 1234 ) (\n) = ( ) 
                 
                    String mensajeD;//Desencriptamos el mensaje   
                    
                    String letra = "";
                    byte[] digitosDecrypt = Base64.getDecoder().decode(texto);
                    String decrypt = new String(digitosDecrypt);            
                    System.out.println("Texto desencriptado: " + decrypt);

                    StringTokenizer st=new StringTokenizer(decrypt);
                    BigInteger[] textoCifrado = new BigInteger[st.countTokens()];

                    for(int i=0;i<textoCifrado.length;i++){
                        letra = st.nextToken();
                        textoCifrado[i]=new BigInteger(letra);
                        System.out.println(i +" "+ letra +"  "+ textoCifrado[i]);
                    }  
                    
                    try{
                        //Excepcion para cuando se reciba un espacio en blanco  el progrma no se dentega
                        mensajeD=rsa.desencripta(textoCifrado);//Desencriptamos el mensaje lamando a una clase del objeto rsa
                        VentanaChat.jTextArea1.append("\n"+"Servidor: "+ mensajeD); //Imprime el mensaje desencriptado en la pantalla
                    }
                    catch(Exception e){
                        System.out.println("Error de conversion");
                    }
                }                                                   //Error al desenecriptar
            }catch(IOException e){};
        }
    }
    
 
    public void enviarMSG(String msg){
        
        System.out.println("Enviado");
        try{
            System.out.println( "Mensaje trado de enviar por el cliente: "+msg);
            String encryptedMessage = rsa.encripta(msg);//Encriptar el mensaje
            System.out.println("Mensaje encriptado: " + encryptedMessage);           
            //Se trnaforma el mensaje encriptado a string para el envio del mensaje
            //String mensajeE=String.valueOf(encryptedMessage);//Convertir dato de tipo BigInteger a String
            //System.out.println("Mensaje encriptado en string por el cliente: "+mensajeE); 
            this.salida = new DataOutputStream(s.getOutputStream());//Crera flujo de salid            
            this.salida.writeUTF(encryptedMessage+"\n");//Enviamos mensaje
        }
        catch (IOException e){
            System.out.println("Problema al enviar mensaje :c");
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