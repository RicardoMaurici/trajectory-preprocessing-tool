package br.ufsc.src.principal;

import javax.swing.SwingUtilities;

import br.ufsc.src.control.ServiceControl;
import br.ufsc.src.igu.MainWindow;
import br.ufsc.src.persistencia.InterfacePersistencia;
import br.ufsc.src.persistencia.Persistencia;

public class Principal {
	
	public static void main(String[] args) {
		
		SwingUtilities.invokeLater(new Runnable() {
			InterfacePersistencia persistencia = new Persistencia();
			ServiceControl control = new ServiceControl(persistencia);
			MainWindow mainWindow = new MainWindow(control);
			public void run() {
				mainWindow.interact();
			}
		});
	}
}