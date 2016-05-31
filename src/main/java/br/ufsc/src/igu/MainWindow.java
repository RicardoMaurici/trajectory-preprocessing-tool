package br.ufsc.src.igu;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.UIManager;

import br.ufsc.src.control.ServiceControl;
import br.ufsc.src.igu.panel.AbstractPanel;
import br.ufsc.src.igu.panel.DataCleanPanel;
import br.ufsc.src.igu.panel.PainelAbrir;
import br.ufsc.src.igu.panel.ConnectionPanel;


public class MainWindow extends JFrame implements ActionListener {
	private static final long serialVersionUID = 1L;
	ServiceControl control;

	public MainWindow(ServiceControl control) {
		super("Clean Data GPS");
		this.control = control;
		configure();
	}

	private void configure() {
		this.setInterfaceLayout();
		JLabel text;
		text = new JLabel();
		//texto.setText("-- Data GPS --");
		text.setFont(new Font(Font.DIALOG_INPUT, Font.BOLD, 40));
		text.setHorizontalAlignment(NORMAL);
		add(text);
		getContentPane().setBackground(Color.LIGHT_GRAY);
		setJMenuBar(new MenuBar(this));
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setPreferredSize(new Dimension(645, 600));
		pack();
		setLocationRelativeTo(null);
	}

	public void interact() {
		setVisible(true);
	}
	
	private void setInterfaceLayout() {
		try{
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
		} catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	public void actionPerformed(ActionEvent e) {
		EnumMenuOption option = EnumMenuOption.valueOf(e.getActionCommand());
		AbstractPanel panel = null;

		switch (option) {
		case OPTIONCONECTION:
			panel = new ConnectionPanel(control);
			break;
		case OPTIONOPEN:
			panel = new PainelAbrir(control);
			break;
		case OPTIONDATACLEAN:
			panel = new DataCleanPanel(control);
			break;
		} 
		setContentPane(panel);
		pack(); 
	}
}