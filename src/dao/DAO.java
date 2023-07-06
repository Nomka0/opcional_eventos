package dao;

import java.util.List;

public interface DAO<T> {
    
	void crear(T entidad);
	
	void crearVarios(List<T> entidad);
    
    T obtener(int index);
    
    List<T> obtenerTodos();
    
    void actualizar(int index, T entidad);
    
    void eliminar(int index);
}
