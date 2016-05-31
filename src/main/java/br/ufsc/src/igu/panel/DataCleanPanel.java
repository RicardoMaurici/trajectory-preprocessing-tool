package br.ufsc.src.igu.panel;

import static javax.swing.GroupLayout.Alignment.BASELINE;
import static javax.swing.GroupLayout.Alignment.LEADING;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.DefaultCellEditor;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import br.ufsc.src.control.ServiceControl;
import br.ufsc.src.control.Utils;
import br.ufsc.src.control.dataclean.ConfigTrajBroke;
import br.ufsc.src.control.exception.BrokeTrajectoryException;
import br.ufsc.src.persistencia.exception.AddColumnException;
import br.ufsc.src.persistencia.exception.DBConnectionException;
import br.ufsc.src.persistencia.exception.GetTableColumnsException;

public class DataCleanPanel extends AbstractPanel{
	
	private static final long serialVersionUID = 1L;
	private JLabel tableLabel, sampleTimeLabel, speedLabel, accuracyLabel, distanceLabel;
	private JTextField tableTF, sampleTimeTF, speedTF, accuracyTF, distanceTF;
	private JButton tableBtn;
	private JTable table1;
	private JScrollPane table;
	private JCheckBox booleanStatusCB;

	public DataCleanPanel(ServiceControl controle) {
		
		super("Data Clean", controle, new JButton("Start"));
		defineComponents();
		adjustComponents();
	}

	@Override
	public void defineComponents() {
		
		processButton.setBackground(Color.DARK_GRAY);
		
		tableLabel = new JLabel("Table name");
		tableTF = new JTextField();
		tableBtn = new JButton("Find");
		tableBtn.addActionListener(this);
		
		sampleTimeLabel = new JLabel("Sample interval");
		sampleTimeTF = new JTextField();
		sampleTimeTF.setToolTipText("Set sample time interval in seconds");
		
		booleanStatusCB = new JCheckBox("Broke by status boolean");
		
		accuracyLabel = new JLabel("Accuracy");
		accuracyTF = new JTextField();
		speedLabel = new JLabel("Delete speed up");
		speedTF = new JTextField();
		
		distanceLabel = new JLabel("Max Distance");
		distanceTF = new  JTextField();
		distanceTF.setToolTipText("Set, in meters, max distance between two points");
		
		Object [] columnNames = new Object[]{ "Column", "Kind" };
        Object [][] data        = new Object[][]{};
        
        DefaultTableModel tab = new MyTableModel( data,columnNames, true );
        table1 = new JTable(tab);
        table = new JScrollPane(table1);
        table1.setRowHeight( 25 );
        setUpColumnComboBox(table1, table1.getColumnModel().getColumn(1));
        

		
	}
	
	@Override
	public void adjustComponents() {
		
		GroupLayout layout = new GroupLayout(this);
		this.setLayout(layout);

		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
		layout.setHorizontalGroup(layout
				.createSequentialGroup()
				.addGroup(layout.createParallelGroup(LEADING)
						.addComponent(tableLabel))
				.addGroup(layout.createParallelGroup(LEADING)
						.addComponent(tableTF)
						.addComponent(table)
						.addGroup(layout.createSequentialGroup()
								.addGroup(layout.createParallelGroup(LEADING)
										.addComponent(accuracyLabel)
										.addComponent(speedLabel)
								)
								.addGroup(layout.createParallelGroup(LEADING)
										.addComponent(accuracyTF)
										.addComponent(speedTF)
								)
								.addGroup(layout.createParallelGroup(LEADING)
										.addComponent(sampleTimeLabel)
										.addComponent(distanceLabel)
								)
								.addGroup(layout.createParallelGroup(LEADING)
										.addComponent(sampleTimeTF)
										.addComponent(distanceTF)
								)
								.addGroup(layout.createParallelGroup(LEADING)
										.addComponent(booleanStatusCB)
								)
								
						)
				)
				.addGroup(layout.createParallelGroup(LEADING)
						.addComponent(tableBtn)
						.addComponent(processButton)
						)

		);

		layout.setVerticalGroup(layout
				.createSequentialGroup()
				.addGroup(layout.createParallelGroup(BASELINE)
						.addComponent(tableLabel)
						.addComponent(tableTF)
						.addComponent(tableBtn))
				.addGroup(layout.createParallelGroup(BASELINE)
						.addComponent(table))
				.addGroup(layout.createParallelGroup(BASELINE)
						.addGroup(layout.createParallelGroup()
								.addComponent(accuracyLabel)
								.addComponent(accuracyTF)
								.addComponent(sampleTimeLabel)
								.addComponent(sampleTimeTF)
								.addComponent(booleanStatusCB)
						)
				)
				.addGroup(layout.createParallelGroup(BASELINE)
						.addGroup(layout.createParallelGroup()
								.addComponent(speedLabel)
								.addComponent(speedTF)
								.addComponent(distanceLabel)
								.addComponent(distanceTF)
						)
				)
				.addGroup(layout.createParallelGroup(BASELINE)
						.addComponent(processButton))
		);
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == tableBtn){
			try {
				if(tableTF.getText().length() != 0){
					ArrayList<String> columns = control.getTableColumns(tableTF.getText());
					DefaultTableModel model = (DefaultTableModel) table1.getModel();
					for (String col : columns) {
						String cl = isKind(col) ? col.toUpperCase() : null; 
						model.addRow(new Object[]{col,cl,null,null});
					}
				}
			} catch (DBConnectionException e1) {
				JOptionPane.showMessageDialog(null,"DB connection error: "+e1.getMsg(),"Data Clean", JOptionPane.ERROR_MESSAGE);
				return;
			} catch (GetTableColumnsException e1) {
				JOptionPane.showMessageDialog(null,"Error to get columns name: "+e1.getMsg(),"Data Clean", JOptionPane.ERROR_MESSAGE);
				tableTF.requestFocus(true);
				return;
			}
		}else if(e.getSource() == processButton){
			ConfigTrajBroke configTrajBroke = getDataFromWindow();
			if(configTrajBroke != null){
				try {
					long startTime = System.currentTimeMillis();
					control.brokeTraj(configTrajBroke);   
					long endTime   = System.currentTimeMillis();
					long totalTime = endTime - startTime;
					JOptionPane.showMessageDialog(null, "Broken Trajectories \n"+Utils.getDurationBreakdown(totalTime),
							"Data Clean",
							JOptionPane.INFORMATION_MESSAGE);
				} catch (DBConnectionException e1) {
					JOptionPane.showMessageDialog(null, "DB connection error: "+e1.getMsg(),
							"Data Clean", JOptionPane.ERROR_MESSAGE);
				} catch (AddColumnException e1) {
					JOptionPane.showMessageDialog(null, "Error adding column: "+e1.getMsg(),
							"Data Clean", JOptionPane.ERROR_MESSAGE);
				} catch (SQLException e1) {
					JOptionPane.showMessageDialog(null, "DB error: "+e1.getMessage(),
							"Data Clean", JOptionPane.ERROR_MESSAGE);
				} catch (BrokeTrajectoryException e1) {
					JOptionPane.showMessageDialog(null, "Error breaking trajectories: "+e1.getMsg(),
							"Data Clean", JOptionPane.ERROR_MESSAGE);
				}
				clearWindow();
			}
		}
	}
	
	 private ConfigTrajBroke getDataFromWindow() {
		Object[][] tableData = getTableData();
		
		if(Utils.isStringEmpty(tableTF.getText())){
			JOptionPane.showMessageDialog(null,"Table name is empty","Data Clean", JOptionPane.ERROR_MESSAGE);
			tableTF.requestFocus(true);
			return null;
		}
		if(tableData.length == 0){
			JOptionPane.showMessageDialog(null,"You should click Find to list the table's columns","Data Clean", JOptionPane.ERROR_MESSAGE);
			tableBtn.requestFocus(true);
			return null;
	 	}
		if(Utils.isStringEmpty(sampleTimeTF.getText()) && !booleanStatusCB.isSelected() && Utils.isStringEmpty(distanceTF.getText())){
			JOptionPane.showMessageDialog(null,"You should choice a method to broke trajectories, \n by sample interval, max distance or status boolean","Data Clean", JOptionPane.ERROR_MESSAGE);
			sampleTimeTF.requestFocus(true);
			return null;
		}
		if(!Utils.isStringEmpty(sampleTimeTF.getText()) && !Utils.isNumeric(sampleTimeTF.getText())){
			JOptionPane.showMessageDialog(null,"Sample interval should be a number in seconds","Data Clean", JOptionPane.ERROR_MESSAGE);
			sampleTimeTF.requestFocus(true);
			return null;
		}
		if(!Utils.isStringEmpty(accuracyTF.getText()) && !Utils.isNumeric(accuracyTF.getText())){
			JOptionPane.showMessageDialog(null,"Accuracy should be a number","Data Clean", JOptionPane.ERROR_MESSAGE);
			accuracyTF.requestFocus(true);
			return null;
		}
		if(!Utils.isStringEmpty(speedTF.getText()) && !Utils.isNumeric(speedTF.getText())){
			JOptionPane.showMessageDialog(null,"Accuracy should be a number","Data Clean", JOptionPane.ERROR_MESSAGE);
			speedTF.requestFocus(true);
			return null;
		}
		if(!Utils.isStringEmpty(distanceTF.getText()) && !Utils.isNumeric(distanceTF.getText())){
			JOptionPane.showMessageDialog(null,"Max distance should be a number in meters","Data Clean", JOptionPane.ERROR_MESSAGE);
			distanceTF.requestFocus(true);
			return null;
		}
		
		String tableName = tableTF.getText();

		String accuracy = Utils.isNumeric(accuracyTF.getText()) ? accuracyTF.getText() : null;
		String speed = Utils.isNumeric(speedTF.getText()) ? speedTF.getText() : null;
		int sample = 0;
		double distance = 0;
		boolean status = booleanStatusCB.isSelected();
		
		try{
			sample = Integer.parseInt(sampleTimeTF.getText());
		}catch(Exception e){
			sample = 0;
		}try{
			distance = Double.parseDouble(distanceTF.getText());
		}catch(Exception e){
			distance = 0;
		}
		
		ConfigTrajBroke configTrajBroke = new ConfigTrajBroke(tableData, tableName, sample, distance, status);
		configTrajBroke.setAccuracy(accuracy);
		configTrajBroke.setSample(sample);
		configTrajBroke.setSpeed(speed);
		
		return configTrajBroke;
	}
	 
	private Object[][] getTableData () {
		DefaultTableModel dtm = (DefaultTableModel) table1.getModel();
		int nRow = dtm.getRowCount(), nCol = dtm.getColumnCount();
		Object[][] tableData = new Object[nRow][nCol];
		for (int i = 0 ; i < nRow ; i++)
			for (int j = 0 ; j < nCol ; j++)
				tableData[i][j] = dtm.getValueAt(i,j);
		return tableData;
	}

	public void setUpColumnComboBox(JTable table, TableColumn column) {
			//Set up the editor for the sport cells.
		 		
			JComboBox comboBox = new JComboBox(getKinds());
			column.setCellEditor(new DefaultCellEditor(comboBox));
			//Set up tool tips for the cells.
			DefaultTableCellRenderer renderer =
			new DefaultTableCellRenderer();
			renderer.setToolTipText("Click for combo box");
			column.setCellRenderer(renderer);
		}

	private String[] getKinds() {
		String[] kinds = new String[] {
				"GID"
				,"TID" 			 	
			 	,"TIMESTAMP"
			 	,"LAT"
			 	,"LON"
			 	,"GEOM"
			 	,"BOOLEAN STATUS"
			 	,"ACCURACY"
			 	,"SPEED"
			 	,""
		};
		return kinds;
	}
	
	private boolean isKind(String kind){
		String[] kinds = getKinds();
		for (String kd : kinds) {
			if(kd.equalsIgnoreCase(kind))
				return true;
		}
		return false;
	}

}