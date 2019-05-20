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
    
    public String encripta(String mensaje)
    {
        int i;
        String texto = new String();  
        byte[] temp = new byte[1];
        byte[] digitos = mensaje.getBytes();
        BigInteger[] bigdigitos = new BigInteger[digitos.length];
        for(i=0; i<bigdigitos.length;i++){
            temp[0] = digitos[i];
            bigdigitos[i] = new BigInteger(temp);
        }
        // Aplicando RSA
        BigInteger[] encriptado = new BigInteger[bigdigitos.length];
        for(i=0; i<bigdigitos.length; i++){
            //System.out.println(bigdigitos[i]);
            encriptado[i] = bigdigitos[i].modPow(e,n);
            //System.out.println(i + "    " +bigdigitos[i].modPow(e,n));           
        }             
        
        
        
        //BASE 64
        for(i=0; i<encriptado.length; i++) {
            texto += encriptado[i].toString() +"\t";
            //System.out.println(i + "  " + encriptado.length + "  " + texto );
        }   
        
        int pklen = texto.length();        
        String substring = texto.substring(5, pklen);  
        
        byte[] bytes64 = substring.getBytes();
        String txt64 = Base64.getEncoder().encodeToString(bytes64);  
        return(txt64);
    }
    
    public String desencripta(BigInteger[] encriptado) {
        BigInteger[] desencriptado = new BigInteger[encriptado.length];

        for(int i=0; i<desencriptado.length; i++)
            desencriptado[i] = encriptado[i].modPow(d,n);

        char[] charArray = new char[desencriptado.length];
        for(int i=0; i<charArray.length; i++)
            charArray[i] = (char) (desencriptado[i].intValue());
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
            bigdigitos[i] = new BigInteger(temp);
        }
        // Aplicando RSA
        BigInteger[] encriptado = new BigInteger[bigdigitos.length];
        for(i=0; i<bigdigitos.length; i++){
            //System.out.println("Texto normal: " + i + "  " + bigdigitos[i]);
            encriptado[i] = bigdigitos[i].modPow(ePub,nPub);
            //System.out.println("Texto Cifrado: " + i + "  " + encriptado[i]);           
        }             
        
        //BASE 64                 
            //System.out.println("Base  64");
        for(i=0; i<encriptado.length; i++) {
            texto += encriptado[i] +"\t";
            //System.out.println((i+1) + "  " + encriptado.length + "  " + texto );
        }   
        //System.out.println("texto final:   " + texto);
        //int pklen = texto.length();        
        //String substring = texto.substring(4, pklen);  
        //System.out.println(substring);
        
        byte[] bytes64 = texto.getBytes();
        String txt64 = Base64.getEncoder().encodeToString(bytes64);  
        return(txt64);
    }
    
    
    public BigInteger getP() {return(p);}
    public BigInteger getQ() {return(q);}
    public BigInteger getFi() {return(fi);}
    public BigInteger getN() {return(n);}
    public BigInteger getE() {return(e);}
    public BigInteger getD() {return(d);}
}