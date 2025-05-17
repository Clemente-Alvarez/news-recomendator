package es.upm.practica;

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

import javax.swing.JOptionPane;

public class AgenteMostrador extends Agent {

	ComportamientoUsuario cu;

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
			
		
			Scanner scanner = new Scanner(System.in);		
			String s = JOptionPane.showInputDialog(null, "Introduzca el texto a buscar", "Input Dialog", JOptionPane.PLAIN_MESSAGE);
			List<String> tokenized = tokenize(s);
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
					//	System.out.println("Título: " + noticia.getTitulo());
					//	System.out.println("Enlace: " + noticia.getUrl());
					//	System.out.println("----------------------------");
						text += noticia.toString() + "\n";
					}
				}
				JOptionPane.showMessageDialog(null, text, "Message Dialog", JOptionPane.PLAIN_MESSAGE);

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
