package proyectocripto;

import java.math.BigInteger;

/**
 *
 * @author xrworm
 */

public class Chat {

    public static ConfChat servidor,cliente;
    
    public static void main (String[] args){ //Manda llamar la interfaz
        VentanaChat vc = new VentanaChat();
        vc.show();
    }
    
    public static void initCliente(String nickname, String ip,BigInteger p, BigInteger q){   //ejecuta la cconfiguracion que se usara para conectarse al servidor
        cliente = new ConfChat(nickname, ip, p, q); //
        cliente.start();
    }
}
