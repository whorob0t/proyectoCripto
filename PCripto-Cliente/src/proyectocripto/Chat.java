package proyectocripto;

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
    
    public static void initCliente(String ip,String pl, String pr, String m){   //ejecuta la cconfiguracion que se usara para conectarse al servidor
        cliente = new ConfChat(ip, pl, pr, m); //
        cliente.start();
    }
}
