package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import model.*;
import view.*;

public class GestionEventos {

	private GestionDatos model;
	private LaunchView view;
	private ActionListener actionListener_comparar, actionListener_buscar,actionListener_copiar,actionListener_guardar,
	actionListener_recuperar,actionListener_recuperarTodos,actionListener_rotar;

	public GestionEventos(GestionDatos model, LaunchView view) {
		this.model = model;
		this.view = view;
	}

	public void contol() {	
		// Gestionamos el evento del boton "Comparar contenido"
		actionListener_comparar = new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				call_compararContenido();
			}
		};
		view.getComparar().addActionListener(actionListener_comparar);

		// Gestionamos el evento del boton "Buscar palabra"
		actionListener_buscar = new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				call_buscarPalabra();
			}
		};
		view.getBuscar().addActionListener(actionListener_buscar);
		
		// Gestionamos el evento del boton "Copiar fichero"
		actionListener_copiar = new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				call_copiarFichero();
			}
		};
		view.getCopiar().addActionListener(actionListener_copiar);
		
		// Gestionamos el evento del boton "Guardar libro"
		actionListener_guardar = new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				call_guardarLibro();
			}
		};
		view.getBtnGuardar().addActionListener(actionListener_guardar);
		
		// Gestionamos el evento del boton "Recuperar libro"
		actionListener_recuperar = new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				call_recuperarLibro();
			}
		};
		view.getBtnRecuperar().addActionListener(actionListener_recuperar);
				
		// Gestionamos el evento del boton "Recuperar todos"
		actionListener_recuperarTodos = new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				call_recuperarTodos();
			}
		};
		view.getBtnRecuperarTodos().addActionListener(actionListener_recuperarTodos);
		
		// Gestionamos el evento del boton "Rotar imagen"
		actionListener_rotar = new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				//call_rotarImagen();
			}
		};
		view.getBtnRotar().addActionListener(actionListener_rotar);
	}

	private void call_compararContenido(){
		String fichero1 = view.getFichero1().getText();
		String fichero2 = view.getFichero2().getText();
		boolean ficherosIguales;

		try {
			ficherosIguales = model.compararContenido(fichero1, fichero2);			
			if (ficherosIguales) 
				view.getTextArea().setText("Los ficheros son iguales.");
			else
				view.getTextArea().setText("Los ficheros son diferentes.");
			
		} catch (FileNotFoundException e) {
			// El error se ha producido por dejar un campo vacio
			if (fichero1.length() == 0) {
					view.showError("Introduce un nombre de fichero en el campo \"Fichero 1\"");
			} else if (fichero2.length() == 0) {
					view.showError("Introduce un nombre de fichero en el campo \"Fichero 2\"");
			} else
				// El error se ha producido por un nombre inexistente
				view.showError("No existe ningún fichero llamado \""+e.getMessage()+"\"");
		} catch (IOException e) {
			view.showError("Se ha producido un error");
		}
	}

	private void call_buscarPalabra() {
		String fichero1 = view.getFichero1().getText();
		String busqueda = view.getPalabra().getText().trim();
		boolean primera_aparicion = view.getPrimera().isSelected();
		int resultado;
		
		try {
			resultado = model.buscarPalabra(fichero1, busqueda, primera_aparicion);	
			// Se ha producido alguna coincidencia
			System.out.println("res: " +resultado);
			if (resultado > 0) {
				if (primera_aparicion)
					view.getTextArea().setText("\""+busqueda+"\""+" aparece por primera vez en la linea "+resultado);
				else
					view.getTextArea().setText("\""+busqueda+"\""+" aparece por última vez en la linea "+resultado);
			} else if (resultado == -1) {
				view.getTextArea().setText("No se ha encontrado ninguna coincidencia");
			}
			
		}  catch (FileNotFoundException e) {
			// El error se ha producido por dejar un campo vacio
			if (fichero1.length() == 0)
				view.showError("Introduce un nombre de fichero en el campo \"Fichero 1\"");
			else
				// El error se ha producido por un nombre inexistente
				view.showError("No existe ningún fichero llamado \""+e.getMessage()+"\"");
		} catch (IOException e) {
			view.showError("Se ha producido un error");
		}
	}
	
	private void call_copiarFichero() {
		String fichero1 = view.getFichero1().getText();
		String fichero2 = view.getFichero2().getText();
		int bytesCopiados = 0;
		
		try {
			bytesCopiados = model.copiarFichero(fichero1, fichero2);
			view.getTextArea().setText("Nuevo fichero creado: \""+fichero2+"\". Se han copiado "+bytesCopiados+" bytes.");
		} catch (IOException e) {
			view.showError("Se ha producido un error");
		}
	}
	
	private void call_guardarLibro() {
		String titulo,autor,año,editor,paginas;
		boolean guardadoCorrecto = true;
		int opcion = 0;
		
		// Obtenemos el valor de los TextFields
		titulo = view.getTitulo().getText();
		autor = view.getAutor().getText();
		año = view.getAño().getText();
		editor = view.getEditor().getText();
		paginas = view.getPaginas().getText();
		
		// Consideramos "Titulo" el unico campo obligatorio porque tambien lo vamos a utilizar como identificador
		if (titulo.length() != 0) {	
			// Comprobamos si el fichero existe y en su caso si se quiere sobreescribir
			if(model.ficheroExiste(new File("libros\\"+titulo))) {
				opcion = sobreescribir();
				if (opcion == 0) { // Ha elegido sobreescribir
					guardadoCorrecto = guardaLibro(titulo, titulo, autor, año, editor, paginas);
				}	
			} else {
				guardadoCorrecto = guardaLibro(titulo, titulo, autor, año, editor, paginas);
			}
		} else
			view.showError("El campo \"Titulo\" es obligatorio para crear un nuevo libro");
		view.limpiarCampos();
		
		if(opcion == 0 && guardadoCorrecto)
			view.getTextArea().setText("El libro \""+titulo+"\" ha sido guardado correctamente");
	}
	
	private int sobreescribir() {
		int opcion = -1;
		opcion = JOptionPane.showConfirmDialog(view, "¿Quiere sobreescribir el fichero?", "El fichero ya existe", JOptionPane.YES_NO_OPTION);
		return opcion;	
	}
	
	private boolean guardaLibro(String id, String titulo, String autor,String año, String editor, String paginas) {
		boolean guardadoCorrecto = true;

		try {
			model.guardar_libro(id, titulo, autor, año, editor, paginas);
		} catch (IOException e) {
			guardadoCorrecto = false;
			view.showError("Se ha producido un error");
		}
		return guardadoCorrecto;
	}
	
	private void call_recuperarLibro() {
		String titulo;
		LibroVO libro;
		
		// Obtenemos el titulo/id del libro a buscar
		titulo = view.getTitulo().getText();
		if (titulo.length() != 0) {		
			try {
				// En caso de existir mostramos su informacion
				libro = model.recuperar_libro(titulo);
				view.getTextArea().setText(libro.toString());
			} catch (FileNotFoundException e) {
				view.showError("No se ha encontrado ninguna coincidencia");
			} catch (ClassNotFoundException | IOException e) {
				view.showError("Se ha producido un error");
			}
		}		
	}
	
	private void call_recuperarTodos() {
		ArrayList<LibroVO> libros = new ArrayList<LibroVO>();
		StringBuilder resultado = new StringBuilder();
		String encabezado;

		try {
			libros = model.recuperar_todos();
			// Comprobamos si hemos obtenido algun resultado
			encabezado = libros.size() == 0 ?  " NO HAY RESULTADOS" :  " "+libros.size()+" RESULTADOS:\n";
			resultado.append(encabezado);
			for (int i=0; i<libros.size(); i++){
				resultado.append("  - "+libros.get(i).getTitulo()+"\n");
			}
			view.getTextArea().setText(resultado.toString());
		} catch (Exception e) {
			view.showError("Se ha producido un error");
		}
	}
	
	
	/*
	 * NO FUNCIONA
	 * 
	 * private void call_rotarImagen() {
		File imagen = new File(view.getFichero1().getText());
		
		try {
			BufferedImage bImagen = ImageIO.read(imagen);
			BufferedImage newImage = new BufferedImage(bImagen.getWidth(), bImagen.getHeight(), BufferedImage.TYPE_INT_ARGB);
			model.saveImagen(model.rotarImagen90Grados(newImage));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}*/

}