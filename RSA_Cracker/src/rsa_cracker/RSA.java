package rsa_cracker;

import java.math.BigInteger;
import java.security.SecureRandom;

/*
 * @author PabloAndrés
 */

class RSA{

    BigInteger p;
    BigInteger q;
    public BigInteger publicKey, privateKey, modulus = new BigInteger( "-1" );

    
    int tamPrimo =10;
    
    
    public RSA() {
    }

        public static long Euler(long P, long Q) {
        return (P - 1) * (Q - 1);
        }
        
        public static long Módulo_Inverso(long Base, long M) {
        long X = 0, Y = 1, ÚltimaX = 1, ÚltimaY = 0, Módulo = M;
        long Cociente, Temp;
        
        while (Módulo != 0) {
        
            Cociente = Base / Módulo;
            
            Temp = Base;
            Base = Módulo;
            Módulo = Temp % Módulo;
            
            Temp = X;
            X = ÚltimaX - Cociente * X;
            ÚltimaX = Temp;
            
            Temp = Y;
            Y = ÚltimaY - Cociente * Y;
            ÚltimaY = Temp;
        }
        
        if (ÚltimaX < 0) {
            ÚltimaX += M;            
        }
        
        return ÚltimaX;
    }
            
        //"E" es la clave de la llave publica
            
        public static long Romper_Llave(long E, long N) {
        long Q = -1;
        long P;
        for (P = 2; P < N / 2; P++) {
            // Encontrar factores de N
            Q = N / P;
            if (P * Q == N) {
                break;
            }
        }
        if (Q == -1) {
            return -1;
        }
        //Calcula de la llave de la priva
        long M = Euler(P, Q);
        return Módulo_Inverso(E, M);
    }
    
    private static long Obtener_Primo(int N){
        long P = 1;
        while (N > 0){
            P++;
            if (Es_Primo(P)){
                N--;
            }
        }
        return P;
    }
    
    private static boolean Es_Primo(long P){
        if (P == 2 || P == 3) {
            return true;
        }
        
        if (Dividir(2, P) || Dividir(3, P)){
            return false;
        }
        
        long N = 1;
        
        while (6 * N - 1 <= Math.sqrt(P)) {
            if (Dividir(6 * N - 1, P) || 
                (6 * N + 1 <= Math.sqrt(P) && Dividir(6 * N + 1, P))) {
                return false;
            }
            N++;
        }
        return true;
    }
   
    private static boolean Dividir(long N, long P) {
        return (P / N) * N == P;
    }
    
    static void factor(int numero){
        int num = 2;

         while(numero!=1){
            while(numero%num==0){
                System.out.println(num);
                numero /= num;
            }
        }
    }

    
    
    public String desencripta(BigInteger[] encriptado, BigInteger d, BigInteger n) {
        BigInteger[] desencriptado = new BigInteger[encriptado.length];

        for(int i=0; i<desencriptado.length; i++){
            desencriptado[i] = encriptado[i].modPow(d,n);
        }
        
        char[] charArray = new char[desencriptado.length];
        
        for(int i=0; i<charArray.length; i++){
            charArray[i] = (char) (desencriptado[i].intValue());
            System.out.println(desencriptado[i].intValue());
        }
        
        return(new String(charArray));
    }
   
}