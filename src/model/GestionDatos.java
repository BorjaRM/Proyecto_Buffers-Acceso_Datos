package model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class GestionDatos {

	public GestionDatos() {

	}
	
	public boolean compararContenido (String fichero1, String fichero2) throws FileNotFoundException, IOException{
		File f1 = new File(fichero1);
		File f2 = new File(fichero2);
		boolean sonIguales = false;
		
		// Comprobamos si existe algún fichero con esos nombres
		if (!ficheroExiste(f1))
			throw new FileNotFoundException(f1.getName());
		
		if (!ficheroExiste(f2))
			throw new FileNotFoundException(f2.getName());
			
		// Comparamos el tamaño de los ficheros
		if (volumenFichero(f1) == volumenFichero(f2)) {
			// Abrimos los ficheros
			BufferedReader br1 = abrirFichero(f1);
			BufferedReader br2 = abrirFichero(f2);
			sonIguales = compararLineas(br1, br2);
			cerrarFichero(br1);
			cerrarFichero(br2);
		}
		return sonIguales;
	}
	
	private BufferedReader abrirFichero(File fichero) throws FileNotFoundException, IOException {
		if(puedeLeerFichero(fichero)) 
			return new BufferedReader(new FileReader(fichero));
		else
			//Lanzamos una excepcion en caso de no tener permisos para abrir el fichero
			throw new IOException(); 
	}
	
	private void cerrarFichero(BufferedReader bf) throws IOException {
		bf.close();
	}
	
	private boolean compararLineas(BufferedReader br1, BufferedReader br2) throws IOException {
		String lineaF1;
		String lineaF2;
		
		// Recorremos los ficheros linea a linea y comparamos su contenido
		while (((lineaF1 = br1.readLine()) != null) && ((lineaF2 = br2.readLine())!= null)) {
			if (!lineaF1.equals(lineaF2))
				return false;  
			} 
		return true;
	}
	
	public int buscarPalabra (String fichero1, String palabra, boolean primera_aparicion) throws IOException{
		File f1 = new File(fichero1);
		String linea;
		int contadorLinea = 0;
		int ultima_aparicion = 0;

		if (!ficheroExiste(f1))
			throw new FileNotFoundException(f1.getName());
		
		// Comprobamos si ha introducido una palabra a buscar
		if (palabra.length() == 0)
			return 0;
		
		// Abrimos el fichero para ver su contenido
	    BufferedReader br1 = abrirFichero(f1);
	    
	    // Recorremos el fichero linea a linea buscando coincidencias
	    while ((linea = br1.readLine()) != null) {
			contadorLinea++;
			if (linea.equalsIgnoreCase(palabra)) {
				System.out.println("hi");
				if (primera_aparicion) {
					return contadorLinea;
				} else {
					// Cada vez que encontramos la palabra, guardamos el numero de linea en una variable
					ultima_aparicion = contadorLinea;
				}
			} else if (ultima_aparicion == 0){
				ultima_aparicion = -1;	    
			}
	    }
	    return ultima_aparicion;
	}	
	
	private long volumenFichero(File f) {
		return f.length();
	}
	
	public boolean ficheroExiste(File fichero) {
		return fichero.exists();
	}
	
	private boolean puedeLeerFichero(File fichero) {
		return fichero.canRead();
	}
	
	public int copiarFichero(String origen, String destino) throws IOException {
        FileInputStream fis;       
        FileOutputStream fos;
        int bytesLeidos = 0;
        int bytesCopiados = 0;
        //byte[] buffer;
        
        // Creamos un stream que nos permita leer los bytes del fichero de origen
        fis = new FileInputStream(origen);
        // Creamos un stream de escritura sobre el fichero de destino
        fos = new FileOutputStream(destino);
        
        /* 
         * Para leer o escibir streams de caracteres tambien se puede utilizar FileReader y FileWriter
         * 
         * En caso de querer leer grupos de bytes utilizamos un array
         * buffer = new byte[4096];
         */
        
        // Mientras haya datos en el stream de lectura los copiamos en el fichero de destino   
        while( (bytesLeidos = fis.read()) != -1 ){
        		bytesCopiados = bytesLeidos;
        		fos.write(bytesLeidos);
        }
        
        // Cerramos los streams
        fis.close();
        fos.close();  
        
        return bytesCopiados;
	}
	
	public void guardar_libro(String identificador, String titulo, String autor, String año, String editor, String paginas) throws IOException {
		LibroVO libro;
		FileOutputStream fos = null;
		ObjectOutputStream salida = null;
		
		// Creamos el objeto libro
		libro = new LibroVO(identificador, titulo, autor, año, editor, paginas);
		// Se crea el fichero donde guardaremos el libro
		fos = new FileOutputStream("libros\\"+identificador);
		// Guardamos el objeto libro en el fichero
		salida = new ObjectOutputStream(fos);
		salida.writeObject(libro);
	
        // Cerramos los streams
		if (fos != null)
			fos.close();
		if (salida != null)
			salida.close();
	}
	
	public LibroVO recuperar_libro(String identificador) throws IOException, ClassNotFoundException, FileNotFoundException {
		FileInputStream fis = null;
		ObjectInputStream entrada = null;
		LibroVO libro = null;

		// Obtenemos el fichero donde esta guardado el libro
		fis = new FileInputStream("libros\\"+identificador);
		// Creamos un stream que nos permite reconstruir el objeto libro guardado anteriormente con el ObjectOutputStream 
		entrada = new ObjectInputStream(fis);
		// Leemos la información del objeto
		libro = (LibroVO) entrada.readObject();
	
        // Cerramos los streams
		if (fis != null)
			fis.close();
		if (entrada != null)
			entrada.close();	
	
		return libro;
	}
	
	public ArrayList<LibroVO> recuperar_todos() throws ClassNotFoundException, FileNotFoundException, IOException {
		File file = new File("libros");
		File[] ficheros;
		ArrayList<LibroVO> libros = new ArrayList<LibroVO>();
				
		// Obtenemos el listado de ficheros y los guardamos en un array
		ficheros = file.listFiles();
		for (int i=0; i<ficheros.length; i++){
			libros.add(recuperar_libro(ficheros[i].getName()));
		}
		return libros;		
	}
	
	/*
	public BufferedImage rotarImagen90Grados(BufferedImage imagen) {
        double sin = Math.abs(Math.sin(90));
        double cos = Math.abs(Math.cos(90));
        int ancho = imagen.getWidth();
        int alto = imagen.getHeight();
        int nuevoAncho = (int) Math.floor(ancho * cos + alto * sin);
        int nuevoAlto = (int) Math.floor(alto * cos + ancho * sin);
        
        BufferedImage imgRotada = new BufferedImage(nuevoAncho, nuevoAlto, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = imgRotada.createGraphics();
       
        return imgRotada;		
	}
	
	public void saveImagen(BufferedImage img) throws IOException {
		 ImageIO.write(img, "jpg", new File("RotatedImg.jpg"));
	}*/
}