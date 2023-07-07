package dao;

import java.util.ArrayList;
import java.util.List;
import model.Deportista;

public class DeportistaDAO implements DAO<Deportista>{

	private List<Deportista> pacientes;
	
	public DeportistaDAO() {
		this.pacientes = new ArrayList<>();
	}
	
	@Override
	public void crear(Deportista deportista) {
		// TODO Auto-generated method stub
		pacientes.add(deportista);
	}

	@Override
	public Deportista obtener(int index) {
		// TODO Auto-generated method stub
		return pacientes.get(index);
	}

	@Override
	public List<Deportista> obtenerTodos() {
		// TODO Auto-generated method stub
		return pacientes;
	}

	@Override
	public void actualizar(int index, Deportista deportista) {
		// TODO Auto-generated method stub
		pacientes.set(index, deportista);
	}
	

    @Override
    public void eliminar(int index) {
        pacientes.remove(index);
    }
	

}
