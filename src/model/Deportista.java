package model;



public class Deportista {
	private int id;
	private String apellido;
	private String nombre;
	private String deporte;

	public Deportista(int id, String apellido, String nombre, String deporte) {
		this.id = id;
		this.apellido = apellido;
		this.nombre = nombre;
		this.deporte = deporte;
	}
	
	public Deportista() {

	}
	

	public String getNombre() {
		return nombre;
	}
	

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public long getTelefono() {
		return telefono;
	}

	public void setTelefono(long telefono) {
		this.telefono = telefono;
	}

	public String getDireccion() {
		return direccion;
	}

	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}

	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}

	public String getApellido() {
		return apellido;
	}
	
	public void setApellido(String apellido) {
		this.apellido = apellido;
	}
	
}


