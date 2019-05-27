package proyectocripto;

import java.math.BigInteger;
import java.util.*;

public class RSA {

    int tamPrimo =10;
    private BigInteger n, q, p;
    private BigInteger fi;
    private BigInteger e, d;        //  e - Llave publica,    d - Llave privada 

    public RSA(BigInteger p,BigInteger q) {
        this.p=p;
        this.q=q;
        generaClaves();             //Genera e y d
    }


    public void generaClaves()
    {
        // n = p * q
        n = p.multiply(q);
        // fi = (p-1)*(q-1)
        fi = p.subtract(BigInteger.valueOf(1));
        fi = fi.multiply(q.subtract(BigInteger.valueOf(1)));
        // Elegimos un e coprimo de y menor que n
        do e = new BigInteger(2 * tamPrimo, new Random());
           while((e.compareTo(fi) != -1) ||
		 (e.gcd(fi).compareTo(BigInteger.valueOf(1)) != 0));
        // d = e^1 mod fi
        d = e.modInverse(fi);
         
    }
    
    public String desencripta(BigInteger[] encriptado) {
        BigInteger[] desencriptado = new BigInteger[encriptado.length];

        for(int i=0; i<desencriptado.length; i++)
            desencriptado[i] = encriptado[i].modPow(d,n); // Se Aplica RSA (modulo) a cada letra del mensaje

        char[] charArray = new char[desencriptado.length];
        for(int i=0; i<charArray.length; i++)
            charArray[i] = (char) (desencriptado[i].intValue()); // COnvertimos BigInteger a String 
        return(new String(charArray));
    }

    
    
    public String encriptaPUB(String mensaje, String eP, String nP)
    {
        BigInteger ePub = new BigInteger(eP);
        BigInteger nPub = new BigInteger(nP);
        int i;
        String texto = new String();  
        byte[] temp = new byte[1];
        byte[] digitos = mensaje.getBytes();
        BigInteger[] bigdigitos = new BigInteger[digitos.length]; 
        for(i=0; i<bigdigitos.length;i++){
            temp[0] = digitos[i];
            bigdigitos[i] = new BigInteger(temp);  //Leemos cada letra del mensaje y convertimos a biginteger
        }
        // Aplicando RSA
        BigInteger[] encriptado = new BigInteger[bigdigitos.length];
        for(i=0; i<bigdigitos.length; i++){
            encriptado[i] = bigdigitos[i].modPow(ePub,nPub);//Se aplica RSA (modulo de las llaves)  a cada letra del mensaje          
        }             
        
        //BASE 64                 
            //System.out.println("Base  64");
        for(i=0; i<encriptado.length; i++) {
            texto += encriptado[i] +"\t";
        }   
        
        byte[] bytes64 = texto.getBytes();
        String txt64 = Base64.getEncoder().encodeToString(bytes64);  // Convertimos el arreglo BigInteger a BAse 64 para poder transportar el mensaje 
        return(txt64);
    }
    
    
    public BigInteger getP() {return(p);}
    public BigInteger getQ() {return(q);}
    public BigInteger getFi() {return(fi);}
    public BigInteger getN() {return(n);}
    public BigInteger getE() {return(e);}
    public BigInteger getD() {return(d);}
}