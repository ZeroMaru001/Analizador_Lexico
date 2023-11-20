
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.io.InputStreamReader;



public class Main {
    //variable global tipo boolean que indica los errores
    static boolean existenErrores = false;
    public static void main(String[] args) throws IOException{

        //Verificar si el analizador analizara una linea o un archivo

        if (args.length > 1 ){
            //Solo permite analizar un archivo por ejecucion
            System.out.println("Uso correcto: 'Main nombreArchivo' o 'Main'");

        }else if( args.length == 1) {
            //Analizar archivo
            leerArchivo(args[0]);
        }else {
            //Analizar una cadena dada por el usuario
            leerCadena();
        }
    }

    /**
     * Metodo para leer cadenas dadas por el usuario
     * No tiene parametros
     */
    private static void leerCadena() throws IOException {
        InputStreamReader entrada = new InputStreamReader(System.in);
        BufferedReader leer = new BufferedReader(entrada);

        while(true){
            System.out.print(">");
            String linea = leer.readLine();
            if(linea == null) break; // Presionar Ctrl + D
            ejecutar(linea);
            existenErrores = true;
        }
    }

    /**
     * Metodo para leer archivos de entrada
     * @param ruta del archivo a analizar
     */

    private static void leerArchivo(String ruta) throws IOException{

        try{
            String linea, codigo = "";
            FileReader archivo = new FileReader(ruta);
            BufferedReader leer = new BufferedReader(archivo);

            while ((linea = leer.readLine()) != null) {
                // Lee línea por línea, omitiendo los saltos de línea
                codigo += linea + "\n" ;
            }
            //eliminar Salto de linea
            codigo = codigo.substring(0, codigo.length() -1);
            ejecutar(codigo);

        }catch(IOException e){
            System.out.println("Error leyendo archivo: " + e.getMessage());
        }
    }

    /**
     * Metodo para analizar el lexico de un codigo
     * @param codigo a analizar de un archivo o dado por el usuario
     */
    private static void ejecutar(String codigo) throws  IOException{
        try{
            Scanner scanner = new Scanner(codigo);
            List<Token> tokens = scanner.scan();

            for(Token token : tokens){
                System.out.println(token.toString());
            }

            Parser parser = new ASDR(tokens);
            parser.parse();
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }

    static void error(int linea, String mensaje){
        reportar(linea, mensaje);
    }

    private static void reportar(int linea, String mensaje){
        System.err.println(
                "[linea " + linea + "] Error : " + mensaje
        );
        existenErrores = true;
    }
}