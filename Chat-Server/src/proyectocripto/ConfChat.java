package proyectocripto;
import java.net.*;
import java.io.*;
import java.math.BigInteger;
import java.util.Base64;
import java.util.StringTokenizer;
import static proyectocripto.VentanaChat.jTextArea1;

public class ConfChat extends Thread {
    
    private Socket s;
    private ServerSocket ss;
    private InputStreamReader entradaSocket;
    private DataOutputStream salida;
    private BufferedReader entrada;
    final int puerto = 8080;
    public static String msm;
    RSA rsa;
    private String eCliente;
    private String nCliente;
    private boolean checkKey = false;
    private boolean nickcheck = false;
    private String nickname;
    private String nicknameCliente;
    
    public ConfChat(String nombre, String nick){
        super(nombre);
        nickname = nick;
    }
    
    public void enviarMSG(String msg){
        try{
            msm=msg;
            //Convertir el mensaje aforma BigInteger para encriptarse
            System.out.println("\n"+"Enviando mensaje al cliente:  " + msg);
            String encryptedMessage = rsa.encriptaPUB(msg,eCliente,nCliente);//Encriptar el mensaje
            //BigInteger encryptedMessage = rsa.encripta( msg );       //Mensaje encriptado
            //System.out.println( encryptedMessage ); // Muestra el mensaje encriptado
            this.salida.writeUTF(encryptedMessage + "\n");
        }catch (IOException e){};
    }
    
    public void LLaves(BigInteger p, BigInteger q){ 
            //Se creal las llaves para ser enviados 
            try{                
                System.out.println("Generando Llave Publica y Llave Privada...");
                rsa = new RSA(p, q);    
                System.out.println(); 
                System.out.println("p = " + rsa.getP());
                System.out.println("q = " + rsa.getQ());
                System.out.println("n = " + rsa.getN());
                System.out.println("fi = " + rsa.getFi());
                System.out.println("e = " + rsa.getE());
                System.out.println("d = " + rsa.getD());
                System.out.println();  
                System.out.println("Nickname: " + nickname + "\n"); 
            }catch(Exception ex){
                System.out.println("Error en el generamiento de llaves");
            }
                
    }
    
    public void run() {
        String texto=null;
        try{
            this.ss = new ServerSocket(puerto);  //se setea el puerto por donde habra la comunicacion cliente-servidor
            this.s = ss.accept();
         
            this.entradaSocket = new InputStreamReader(s.getInputStream());
            this.entrada = new BufferedReader(entradaSocket);
            this.salida = new DataOutputStream(s.getOutputStream());
            
            while(true){
                //Mensaje recibido
                texto = this.entrada.readLine();
                String textEncrypt=(texto.trim());    
                            
                System.out.println(textEncrypt); 
                try{
                    if(nickcheck == false){
                        String n = textEncrypt.substring(0, 9);
                        String nickCli = textEncrypt.substring(9, textEncrypt.length());

                        if(n.equals("Nickname:")){
                            System.out.println("Nickname del cliente: " + nickCli);
                            nicknameCliente = nickCli;
                            nickcheck = true;
                        }
                    }
                }catch(Exception ex){
                    System.out.println("");
                }
                
               
                if(checkKey == false){
                    String pubkey = textEncrypt;             
                    try{
                        char char1 = pubkey.charAt(0);
                        char char2 = pubkey.charAt(1);

                        if(char1 == 'P' && char2 == 'K'){
                            int pubklen = pubkey.length();
                            char eln = pubkey.charAt(2);
                            char nln = pubkey.charAt(3);
                            int elen = Character.getNumericValue(eln);
                            int nlen = Character.getNumericValue(nln);
                            eCliente = pubkey.substring(4, 4+elen);
                            nCliente = pubkey.substring(4+elen, pubklen);
                            checkKey = true;        
                            
                            String eOwnPub = rsa.getE().toString(); 
                            String nOwnPub = rsa.getN().toString(); 
                            String llavePub = "PK" + eOwnPub.length() + ""  + nOwnPub.length() + ""  + eOwnPub + "" + nOwnPub;
                            enviarLlave(llavePub);
                            
                            enviarNickname(nickname); 
                            System.out.println("Llave pública del Cliente: " + pubkey);
                            System.out.println("Status Key: " + checkKey );
                            
                            jTextArea1.append("\n"+"Llaves Compartidas.  \nEnvia mesajes ahora\n\n");
                        }else{
                            //break;
                        }
                    }catch(Exception ex){
                        System.out.println("Error al obtener la llave");
                    }
                }
                
                if(checkKey == true && nickcheck == true){
                    String newStr = texto.substring(2, textEncrypt.length());  
                    System.out.println("\n" + "Mensaje recibido desde el cliente: " + newStr);

                    if(newStr != "" || newStr != null){
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
                                       //VentanaChat.jTextArea1.append("\n" + nicknameCliente +": "+ mensajeDecrypt); //Imprime el mensaje desencriptado en la pantalla
                                       VentanaChat.jTextArea1.append(String.format("\n%-30s %47s\n", "", nicknameCliente+": " + mensajeDecrypt));
                                   }
                                    catch(Exception ex){System.out.println("Error sl mostrar mensaje");}
                                }catch(Exception ex){System.out.println("Error al desencriptar");}                            
                            }catch(Exception ex){System.out.println("Error al obtener String de mensje");}
                        }catch(Exception ex){System.out.println("string no Base 64");}
                    }
                }
            }
        }catch (IOException ex){
            System.out.println("Algun tipo de error");            
        }
    }
    
    public String leerMSG(){
        try{
            return this.entrada.readLine();
        }catch(IOException e){};
        return null;
    }
    
    public void enviarLlave(String llavePublica){
        
        System.out.println("Enviando llave publica al Cliente.");
        try{
            System.out.println("Llave pública del Servidor: " + llavePublica);
            this.salida = new DataOutputStream(s.getOutputStream());//Crera flujo de salid            
            this.salida.writeUTF(llavePublica+"\n");//Enviamos mensaje
        }
        catch (IOException e){
            System.out.println("Problema al enviar llave publica :c");
        };
    }
    public void enviarNickname(String nick){
        
        System.out.println("Enviando llave publica al Cliente.");
        try{
            this.salida = new DataOutputStream(s.getOutputStream());//Crera flujo de salid            
            this.salida.writeUTF("Nickname:"+nick+"\n");//Enviamos mensaje
        }
        catch (IOException e){
            System.out.println("Problema al enviar nickname :c");
        };
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
