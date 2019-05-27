package proyectocripto;

/**
 *
 * @author xrworm
 */
public class Chat {
    
    public static ConfChat servidor,cliente;
    
    public static void main (String[] args){ // Se crea un nuevo objeto para la interfaz
        VentanaChat vc = new VentanaChat();
        vc.show();
    }
    
    public static void initServer(String nick){    //Se prepara un objeto para la configuracion del servidor
        servidor = new ConfChat("hilos", nick);
        servidor.start();
    }
    
}