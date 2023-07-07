package app;

import controller.ControllerDeportista;
import view.VistaPrincipal;

public class Main {
	public static void main(String[] args) {
		VistaPrincipal vista = new VistaPrincipal();
		ControllerDeportista controller = new ControllerDeportista(vista);
	}
}
