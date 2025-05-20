package es.upm.practica;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.MouseEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

public class MainGUI extends Thread {
	AgenteMostrador agente;
	private JProgressBar progressBar;
	private JTable table;
	private JScrollPane scrollPane;
	private JTextField searchField;
	private JButton searchButton;
	
	public MainGUI(AgenteMostrador agente) {
		this.agente = agente;
	}
	
	public void run() {
		JFrame frame = new JFrame();
		frame.setTitle("News Recommendator");
		
		frame.setResizable(true);
		frame.setSize(500, 500);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		frame.addWindowListener(new WindowListener() {
			@Override
			public void windowClosing(WindowEvent e) {
				agente.doDelete();
			}

			@Override
			public void windowOpened(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void windowClosed(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void windowIconified(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void windowDeiconified(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void windowActivated(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void windowDeactivated(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		progressBar = new JProgressBar();
		progressBar.setIndeterminate(true);
		progressBar.setVisible(false);
		progressBar.setPreferredSize(new Dimension(50, 10));
				
		table = new JTable(toTableModel(new ArrayList<Noticia>()));
		table.addMouseListener(new MouseAdapter() {
			 public void mouseClicked(MouseEvent e) {

			        int row = table.getSelectedRow();
			        int col = table.getSelectedColumn();
			       			        
			        if (Desktop.isDesktopSupported()) {
			            try {
			            	URI uri = new URI((String) table.getModel().getValueAt(row, 1));
			            	Desktop.getDesktop().browse(uri);
			            } 
			            catch (IOException exc) {exc.printStackTrace();}
			            catch (URISyntaxException exc) {exc.printStackTrace();}
			        }
			 }
		});		
	
		JPanel centerPane = new JPanel();
		centerPane.setLayout(new BoxLayout(centerPane, BoxLayout.LINE_AXIS));
		
		centerPane.add(Box.createRigidArea(new Dimension(50,0)));
		scrollPane = new JScrollPane(table);
		scrollPane.setVisible(false);
		centerPane.add(scrollPane);
		centerPane.add(progressBar);
		centerPane.add(Box.createRigidArea(new Dimension(50,0)));
		
		JPanel centerPaneY = new JPanel();
		centerPaneY.setLayout(new BoxLayout(centerPaneY, BoxLayout.PAGE_AXIS));
		centerPaneY.add(Box.createRigidArea(new Dimension(0,50)));
		centerPaneY.add(centerPane);
		centerPaneY.add(Box.createRigidArea(new Dimension(0,50)));
		
		
		frame.getContentPane().add(centerPaneY, BorderLayout.CENTER);
		
		JPanel searchPane = new JPanel();
		searchPane.setLayout(new BoxLayout(searchPane, BoxLayout.LINE_AXIS));
			
		searchField = new JTextField(10);
		searchButton = new JButton("Buscar");
		
		searchField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (searchField.getText().isBlank()) return;
				searchField.setEnabled(false);
				searchButton.setEnabled(false);
				scrollPane.setVisible(false);
				progressBar.setVisible(true);
				
				agente.setText(searchField.getText());
				agente.doWake();
			}
		});
		
		searchButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (searchField.getText().isBlank()) return;
				searchField.setEnabled(false);
				searchButton.setEnabled(false);
				scrollPane.setVisible(false);
				progressBar.setVisible(true);
				
				agente.setText(searchField.getText());
				agente.doWake();
			}
		});
		
		
		searchPane.add(Box.createRigidArea(new Dimension(50,0)));
		searchPane.add(searchField);
		searchPane.add(Box.createRigidArea(new Dimension(50,0)));
		searchPane.add(searchButton);
		searchPane.add(Box.createRigidArea(new Dimension(50,0)));
		
		JPanel searchPaneY = new JPanel();
		searchPaneY.setLayout(new BoxLayout(searchPaneY, BoxLayout.PAGE_AXIS));
		
		searchPaneY.add(Box.createRigidArea(new Dimension(0,25)));
		searchPaneY.add(searchPane);		
		
		frame.getContentPane().add(searchPaneY, BorderLayout.PAGE_START);
		frame.setVisible(true);
		
		agente.setComponents(searchField, searchButton, table, scrollPane, progressBar);
	}
	
    public static TableModel toTableModel(List<Noticia> list){
        DefaultTableModel model = new DefaultTableModel(new Object[] { "Título", "Link" }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        for (Noticia not : list) {
            model.addRow(new Object[] { not.getTitulo(), not.getUrl() });
        }
        
        return model;
    }
	   
    
}

