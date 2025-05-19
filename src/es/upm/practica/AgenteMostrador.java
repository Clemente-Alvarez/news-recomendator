package es.upm.practica;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;

public class AgenteMostrador extends Agent {

	ComportamientoUsuario cu;
	String searchText;
	MainGUI gui;
	private JTextField searchField;
	private JButton searchButton;
	private JTable table;
	private JScrollPane scrollPane;
	private JProgressBar progressBar;

	private static final Set<String> redundantes = new HashSet<>(Arrays.asList(
			// preposiciones
			"a", "ante", "bajo", "cabe", "con", "contra", "de", "desde", "durante", "en", "entre", "hacia", "hasta",
			"mediante", "para", "por", "según", "sin", "so", "sobre", "tras", "versus", "vía",
			// articulos
			"el", "los", "la", "las", "un", "unos", "una", "unas",
			// conjunciones
			"y", "e", "ni", "así", "como", "también", // copulativas
			"o", "u", "bien", "si", // disyuntivas
			"pero", "ahora", "sin", "embargo", "sino", "cambio", "obstante", "excepto", "salvo", "menos", // adversativas
			"ora", "ya", "no", "solamente", "que", // distributivas
			"pues", "tanto", "por", "consiguiente", "es", "tal", "ahí", "sea", // ilativas
			"aún", "además", "más", "otra", "asimismo", "otro" // continuativas
	));

	public void setup() {
		System.out.println("Agente Mostrador");
		cu = new ComportamientoUsuario();
		addBehaviour(cu);
		
		searchText = null;
		gui = new MainGUI(this);
		gui.start();
	}
	
	public void setText(String newText) {
		searchText = newText;
	}

	public void setComponents(JTextField searchField, JButton searchButton, JTable table, JScrollPane scrollPane, JProgressBar progressBar) {
		this.searchField = searchField;
		this.searchButton = searchButton;
		this.table = table;
		this.scrollPane = scrollPane;
		this.progressBar = progressBar;	
	}
	
	class ComportamientoUsuario extends CyclicBehaviour {

		@Override
		public void action() {

			// Leemos el texto que introduce el usuario por pantalla y lo enviamos al agente
			// Agente Buscador
			//Scanner scanner = new Scanner(System.in);
			//System.out.print("Introduzca el texto a buscar: ");
			//String temp = scanner.nextLine();

			//List<String> tokenized = tokenize(temp);
			//Utils.enviarMensaje(this.myAgent, "buscar", tokenized);
			//ACLMessage msg = blockingReceive(MessageTemplate.MatchPerformative(ACLMessage.INFORM));
			// Cuando el agente AgenteBuscador responde, imprimimos su respuesta por
			// pantalla

			this.myAgent.doWait();			
		
			List<String> tokenized = tokenize(searchText);
			Utils.enviarMensaje(this.myAgent, "buscar", tokenized);
			ACLMessage msg = blockingReceive(MessageTemplate.MatchPerformative(ACLMessage.INFORM));
					
			String text = "";
			try {
				List<Noticia> resultados = (List<Noticia>) msg.getContentObject();
				// Mostramos los resultados
				if (resultados.isEmpty()) {
					//System.out.println("No se encontraron noticias con el texto buscado.");
					text = "No se encontraron noticias con el texto buscado" + "\n";
				} else {
					//System.out.println("\nResultados encontrados:");
					for (Noticia noticia : resultados) {
						System.out.println("Título: " + noticia.getTitulo());
						System.out.println("Enlace: " + noticia.getUrl());
						System.out.println("----------------------------");
					}
				}

				searchField.setEnabled(true);
				searchButton.setEnabled(true);
				table.setModel(MainGUI.toTableModel(resultados));
				scrollPane.setVisible(true);
				progressBar.setVisible(false);				

			} catch (UnreadableException e) {
				e.printStackTrace();
			}

		}

		private List<String> tokenize(String temp) {
			String[] trimmed = temp.split(" ");
			List<String> tokens = new ArrayList<String>();
			for (String t : trimmed) {
				System.out.println(t);
				if (!redundantes.contains(t))
					tokens.add(t);
			}
			return tokens;
		}
		


	}

}
