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
    private ServerSocket ss;
    private InputStreamReader entradaSocket;
    private DataOutputStream salida;
    private BufferedReader entrada;
    final int puerto = 8080;
    public static String msm;
    RSA rsa;
    BigInteger publicKey ;
    BigInteger privateKey;
    BigInteger modulus ;
    
    public ConfChat(String nombre){
        super(nombre);
    }
    
    public void enviarMSG(String msg){
             try{
            msm=msg;
            //Convertir el mensaje aforma BigInteger para encriptarse
            System.out.println("\n"+"Mensaje enviado desde el Servidor: " + msg);
            String encryptedMessage = rsa.encripta(msg);//Encriptar el mensaje
            //BigInteger encryptedMessage = rsa.encripta( msg );       //Mensaje encriptado
            //String mensajeE=String.valueOf(encryptedMessage);
            System.out.println( encryptedMessage ); // Muestra el mensaje encriptado
            
            this.salida.writeUTF(encryptedMessage+"\n");
        }catch (IOException e){};
    }
    
    public void LLaves(BigInteger publicKey, BigInteger privateKey, BigInteger modulus ){ 
            
            System.out.println("Configurando llaves");
            this.publicKey=publicKey ;
            this.privateKey=privateKey;
            //Se creal las llaves para ser enviados                
            rsa = new RSA( this.publicKey, this.privateKey, 10);
            System.out.println(rsa.getN());
            System.out.println("Llaves listas para la comunicacion");
    }
    
    public void run() {
        String text=null;
        try{
            this.ss = new ServerSocket(puerto);  //se setea el puerto por donde habra la comunicacion cliente-servidor
            this.s = ss.accept();
         
            this.entradaSocket = new InputStreamReader(s.getInputStream());
            this.entrada = new BufferedReader(entradaSocket);
            this.salida = new DataOutputStream(s.getOutputStream());
            
            while(true){
                //Mensaje recibido
                text = this.entrada.readLine();
                //Se toma el mensaje que va a estar cifrar
                String newStr=(text.trim()); //Quita los espacion que pueda haber en el texto
                System.out.println("\n"+"Mensaje Resivido del lado del servidor: \n"+newStr);
                                                  
                String mensajeD;             
                try{
                    
                    
                    String letra="";
                    byte[] digitosDecrypt = Base64.getDecoder().decode(newStr);
                    String decrypt = new String(digitosDecrypt);          
                    StringTokenizer st=new StringTokenizer(decrypt);
                    BigInteger[] textoCifrado = new BigInteger[st.countTokens()];
                    for(int i=0;i<textoCifrado.length;i++){
                        letra = st.nextToken();
                        textoCifrado[i]=new BigInteger(letra);
                        System.out.println(i +" "+ letra +"  "+ textoCifrado[i]);
                    }  
                    
                    mensajeD = rsa.desencripta(textoCifrado);
                    System.out.println("Mensaje Desencriptado: " + mensajeD);
                    VentanaChat.jTextArea1.append("\n"+"Cliente-"+mensajeD);
                }catch(Exception e){
                    System.out.println("Error de conversion");
                }
            }
        }catch (IOException e){
            System.out.println("Algun tipo de error");
        };
    }
    
    public String leerMSG(){
        try{
            return this.entrada.readLine();
        }catch(IOException e){};
        return null;
    }
    
    public void desconectar() {
        try{
            s.close();
        }catch(IOException e){};
        try{
            ss.close();
        }catch(IOException e){};
    }
}
