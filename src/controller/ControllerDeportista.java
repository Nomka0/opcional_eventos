package controller;

import dao.DeportistaDAO;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import view.VistaPrincipal;
import model.Deportista;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import static java.util.Collections.reverse;
import static java.util.Collections.sort;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

public class ControllerDeportista {
	private VistaPrincipal ventana;
	//private Deportista deportista;
	private String rutaArchivo;

	public String opcionSeleccionada;
	private List<Deportista> datosTabla;
	private List<Deportista> datosTablaCopia;// para que no se repitan datos
	private int contadorDatos;
	private JTable tabla;
	private DefaultTableModel modeloTabla;
	private DeportistaDAO deportistaDao;
	private DeportistaDAO deportistaDaoPers;
	private DeportistaDAO deportistaTotalesDAO;

	// private List<Usuario> usuariosTotales;//Aquí se almacenarán los persistentes,
	// y los que se crean en la sesión, para manejarlos todos
	private int filaSeleccionada;
	private ArrayList<Integer> indexadorTabla;// tendrá los indices actualizados de la tabla en una lista, para después
	// no borrar cosas que no se supone debían ser borradas
	private int contadorTotal;
	private int contadorLista;
	private ArrayList<Integer> datosAEliminar;

	public ControllerDeportista(VistaPrincipal ventana) {
		this.ventana = ventana;
		//this.deportista = deportista;
		deportistaDao = new DeportistaDAO();
		contadorDatos = 0;
		// tabla = ventana.getTabla();
		// modeloTabla = ventana.getModeloTabla();
		deportistaTotalesDAO = new DeportistaDAO();
		rutaArchivo = "src/deportistas/info.csv"; // archivo de texto con el id del usuario
		indexadorTabla = new ArrayList<>();
		deportistaDaoPers = new DeportistaDAO();
		datosAEliminar = new ArrayList<>();

		String[] deportes = {"Fútbol","Natación","Voleyball","Tenis","Karate"};


		for(String deporte : deportes) {
			ventana.getComboBox().addItem(deporte);
		}
		leerArchivos();

		// ventana.habilitarEditar(false);
		ventana.setVisible(true);
		ventana.setLocationRelativeTo(null);

		// ventana.jTableListener(new ManejadoraDeMouse());
		// ventana.btnListarListener(new ListarListener());
		ventana.guardarBtnListener(new GuardarListener());
		ventana.salirBtnListener(new SalirListener());
		ventana.seleccionListener(new listaSeleccion());
		// ventana.addComboBoxListener(new ComboBoxListener());
		ventana.eliminarBtnListener(new EliminarListener());
		// ventana.btnOkListener(new OkListener());
	}

	public void eliminarDatosEnArchivo() {
		File archivo = new File(rutaArchivo);
		BufferedWriter writer;
		try {
			writer = new BufferedWriter(new FileWriter(archivo));
			writer.write("");
			writer.close();
		} catch (IOException ex) {
			Logger.getLogger(ControllerDeportista.class.getName()).log(Level.SEVERE, null, ex);
		}
	}


	public void GuardarEnArchivo() {
		try (BufferedWriter escritor = new BufferedWriter(new FileWriter(rutaArchivo, true))) {
			//eliminarDeportistasTotales();
			eliminarDatosEnArchivo();
			System.out.println("usuarios totales: " + deportistaTotalesDAO.obtenerTodos());
			for (Deportista deportista : deportistaTotalesDAO.obtenerTodos()) {
				int ID = deportista.getId();
				String nombre = deportista.getNombre();
				String apellido = deportista.getApellido();
				String deporte = deportista.getDeporte();
				Object[] datos = { ID, apellido, nombre, deporte};
				String datosString = Arrays.toString(datos);
				String datosStringRecortado = datosString.substring(1, datosString.length() - 1);
				escritor.write(datosStringRecortado);
				escritor.newLine();
				//contadorDatos++;
			}

			// else ventana.displayErrorMessage("Error: ¡No hay ningún usuario para
			// guardar!");
			contadorLista = deportistaTotalesDAO.obtenerTodos().size() - 1;
			datosAEliminar.clear();
			reiniciarIndexador();
			// ventana.deshabilitarGuardar();
			// postGuardar();
			// ventana.displayErrorMessage("Guardado");

			// }
		} catch (IOException e) {
			System.out.println("Ocurrió un error al crear el archivo.");
		}

	}

	public void datosALista() {
		Deportista usuarioNuevo;
		int id;
		String nombre;
		String apellido;
		String deporte;

		usuarioNuevo = new Deportista();
		id = ventana.getID();
		nombre = ventana.getNombres();
		apellido = ventana.getApellidos();
		deporte = ventana.getDeporte();

		usuarioNuevo.setId(id);
		usuarioNuevo.setNombre(nombre);
		usuarioNuevo.setApellido(apellido);
		usuarioNuevo.setDeporte(deporte);
		deportistaDao.crear(usuarioNuevo);// añade el usuario con todos sus atributos a una lista de la
		// implementación de la interfaz DAO de usuario
		System.out.println(deportistaDao.obtenerTodos());
		ventana.addDatosTabla(usuarioNuevo);
		// nuevosDatos();
		// ventana.setCamposVacios();
		// GuardarEnArchivo();
		contadorTotal++;
		contadorDatos++;
		contadorLista = deportistaTotalesDAO.obtenerTodos().size() - 1;
		contadorLista++;

		indexadorTabla.add(contadorLista);
		// vista.setArea(modelo.getArea());
		// vista.activarControles(false);
		// ventana.habilitarGuardar();

		System.out.println("indexador tabla: " + indexadorTabla);
		System.out.println("usuarios Dao: " + deportistaDao.obtenerTodos());
		deportistaTotalesDAO.obtenerTodos()
		.add(deportistaDao.obtenerTodos().get(contadorDatos - 1));
	}

	public void leerArchivos() {
		try {
			BufferedReader lector = new BufferedReader(new FileReader(rutaArchivo));
			String lineaArchivo;
			try {
				while ((lineaArchivo = lector.readLine()) != null) {

					String[] datosReconstruidos = lineaArchivo.split(",");
					Deportista usuarioNuevo;
					int id;
					String nombre;
					String apellidos;
					String deporte;

					usuarioNuevo = new Deportista();

					id = Integer.parseInt(datosReconstruidos[0]);
					apellidos = datosReconstruidos[1];
					nombre = datosReconstruidos[2];
					deporte = datosReconstruidos[3];

					usuarioNuevo.setId(id);
					usuarioNuevo.setNombre(nombre);
					usuarioNuevo.setApellido(apellidos);
					usuarioNuevo.setDeporte(deporte);
					deportistaDaoPers.crear(usuarioNuevo);
					System.out.println(usuarioNuevo.getApellido());
				}

				datosPersistentes(deportistaDaoPers);
				deportistaTotalesDAO.obtenerTodos().addAll(deportistaDaoPers.obtenerTodos());
				contadorTotal = deportistaDaoPers.obtenerTodos().size();
				System.out.println(deportistaTotalesDAO.obtenerTodos());

				reiniciarIndexador();

			} catch (IOException ex) {
				Logger.getLogger(ControllerDeportista.class.getName()).log(Level.SEVERE, null, ex);
			}
		} catch (FileNotFoundException ex) {
			Logger.getLogger(ControllerDeportista.class.getName()).log(Level.SEVERE, null, ex);
			System.out.println("no se encontró el archivo.");
		}
	}

	public void datosPersistentes(DeportistaDAO deportista) {
		for (Deportista deportistas : deportista.obtenerTodos()) {
			ventana.addDatosTabla(deportistas);
		}
	}

	// reinicia indexador tabla, para volver a almacenar las filas que se quedan en
	// la tabla. Esto es para cuando empieza la ventana, o cuando se guardan datos
	public void reiniciarIndexador() {
		contadorTotal = deportistaDaoPers.obtenerTodos().size();
		indexadorTabla.clear();
		if (contadorTotal == 1) {
			indexadorTabla.add(0);
		} else {
			for (int i = 0; i < contadorTotal; i++) {
				indexadorTabla.add(i);
			}
		}

	}

	public boolean compararIDsUsuarios(int usuarioID) {
		try (BufferedReader lector = new BufferedReader(new FileReader(rutaArchivo))) {
			String linea;
			while ((linea = lector.readLine()) != null) {
				String[] valores = linea.split(",");
				int numeroEnLinea = Integer.parseInt(valores[0].trim());
				if (usuarioID == numeroEnLinea) {
					return true; // El número se encuentra en el archivo
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false; // El número no se encuentra en el archivo
	}

	public void eliminarDeportista(int index) {

	}

	public void eliminarDeportistasTotales() {
		// prestamosTotalesDAO.obtenerTodosLosUsuarios() =
		// prestamosTotalesDAO.obtenerTodosLosUsuarios()DAO.obtenerTodosLosUsuarios();
		sort(datosAEliminar);
		reverse(datosAEliminar);
		for (int i = 0; i < datosAEliminar.size(); i++) {
			deportistaTotalesDAO.eliminar(datosAEliminar.get(i));
		}
		// prestamosTotalesDAO.obtenerTodosLosUsuarios() =
		// prestamosTotalesDAO.obtenerTodosLosUsuarios()DAO.obtenerTodosLosUsuarios();
	}

	public void editarElementos(int index) {
		Deportista deportistaEditado = new Deportista();
		int id = ventana.getID();
		String apellido = ventana.getApellidos();
		String nombre = ventana.getNombres();
		String deporte = ventana.getDeporte();

		deportistaEditado.setId(id);
		deportistaEditado.setApellido(apellido);
		deportistaEditado.setNombre(nombre);
		deportistaEditado.setDeporte(deporte);

		ventana.editarElementoTabla(index, deportistaEditado);
		deportistaTotalesDAO.actualizar(index, deportistaEditado);
	}

	public void intercambio_datos_actuales(int index) {
		//int index = averiguar_index(ventana.getID());
		System.out.println(index);
		editarElementos(index);
	}



	public int averiguar_index(int id) {
		List<Deportista> deportistaTotales = deportistaTotalesDAO.obtenerTodos();
		for (int i = 0; i < deportistaTotales.size(); i++) {
			Deportista deportista = deportistaTotales.get(i);
			if (deportista.getId() == id) {
				return i;
			}
		}
		return -1;
	}

	class listaSeleccion implements ListSelectionListener {
		JTable table = ventana.getTable();

		@Override
		public void valueChanged(ListSelectionEvent e) {
			if (!e.getValueIsAdjusting()) {  // Verificar si la selección ha terminado
				// Obtener el índice de la fila seleccionada
				filaSeleccionada = table.getSelectedRow();
				ventana.toggleEliminar(true);

				// Realizar acciones con la fila seleccionada
				if (filaSeleccionada != -1) {
					// Obtenemos los valores de la fila seleccionada
					Object[] rowData = new Object[table.getColumnCount()];
					for (int i = 0; i < table.getColumnCount(); i++) {
						rowData[i] = table.getValueAt(filaSeleccionada, i);
					}

					// Ejemplo: Imprimir los valores de la fila seleccionada
					for (Object value : rowData) {
						System.out.print(value + " ");
					}
					System.out.println(filaSeleccionada);
				}
			}
		}
	}

	class SalirListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getActionCommand().equalsIgnoreCase("Salir")) {
				ventana.dispose();
			}
		}
	}

	class EliminarListener implements ActionListener {
		int fila_eliminar = filaSeleccionada;
		@Override
		public void actionPerformed(ActionEvent e) {
			JTable table = ventana.getTable();
			DefaultTableModel modelo = (DefaultTableModel) table.getModel();
			//int index_reemplazar = 0;
			if (e.getActionCommand().equalsIgnoreCase("Eliminar")) {
				modelo.removeRow(fila_eliminar);
				System.out.println(fila_eliminar);
				deportistaTotalesDAO.eliminar(fila_eliminar);
				GuardarEnArchivo();
			}
		}
	}

	class GuardarListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			int index_reemplazar = 0;
			if (e.getActionCommand().equalsIgnoreCase("Guardar")) {
				// datosALista();
				try {
					if (compararIDsUsuarios(ventana.getID())) {
						ventana.displayErrorMessage("El deportista ya está registrado. Puede actualizarlo.");
						index_reemplazar = averiguar_index(ventana.getID());
						ventana.change_btn();

					} else {
						try {// lista datos
							// if(ventana.getEstaVacio() == false){
							datosALista();

							// }
						} catch (NumberFormatException ex) {
							// ventana.displayErrorMessage("¡Revisa los datos ingresados!");
							System.out.println("revisa los datos!");
						}
						GuardarEnArchivo();
						ventana.setCamposVacios();
					}
				} catch (NumberFormatException ex) {
					ventana.displayErrorMessage("Error: ¡Revisa los datos ingresados!");
				}
			} else {
				intercambio_datos_actuales(index_reemplazar);
				GuardarEnArchivo();
				ventana.change_btn();
			}
			System.out.println("Estos son los usuarios totales: " + deportistaTotalesDAO.obtenerTodos());
		}
	}
}
