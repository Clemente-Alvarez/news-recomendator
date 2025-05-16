package es.upm.practica;

import java.util.List;

import java.util.Scanner;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

public class AgenteMostrador extends Agent {

	ComportamientoUsuario cu;

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
			Scanner scanner = new Scanner(System.in);
			System.out.print("Introduzca el texto a buscar: ");
			String temp = scanner.nextLine();
			Utils.enviarMensaje(this.myAgent, "buscar", temp);
			ACLMessage msg = blockingReceive(MessageTemplate.MatchPerformative(ACLMessage.INFORM));
			// Cuando el agente AgenteBuscador responde, imprimimos su respuesta por
			// pantalla

			try {
				List<Noticia> resultados = (List<Noticia>) msg.getContentObject();
				// Mostramos los resultados
                if (resultados.isEmpty()) {
                    System.out.println("No se encontraron noticias con el texto buscado.");
                } else {
                    System.out.println("\nResultados encontrados:");
                    for (Noticia noticia : resultados) {
                        System.out.println("Título: " + noticia.getTitulo());
                        System.out.println("Enlace: " + noticia.getUrl());
                        System.out.println("----------------------------");
                    }
                }

			} catch (UnreadableException e) {
				e.printStackTrace();
			}

		}

	}

}
