package es.upm.practica;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;

import es.upm.practica.texto.RefactoredText;
import jade.content.lang.sl.SLCodec;
import jade.domain.DFService;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.Envelope;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import jade.core.AID;

class Pair{
	public Double score;
	public String link;
	
	public Pair(Double score, String link) {
		this.score = score;
		this.link = link;
	}
}

public class AgenteOperador extends Agent{
	private ComportamientoOperador co;
	
	public void setup() {
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setName("Operador");
		
		sd.setType("operador");
		sd.addOntologies("ontologia");
		sd.addLanguages(new SLCodec().getName());
		dfd.addServices(sd);
		
		try {
			// registro el servicio en el DF
			DFService.register(this, dfd);
		} catch (FIPAException e) {
			System.err.println("Agente " + getLocalName() + ": " + e.getMessage());
		}
		// añado un comportamiento cíclico para recibir mensajes que demandan búsquedas
		// y atenderlas
		addBehaviour(new ComportamientoOperador());
	}
	
	class ComportamientoOperador extends CyclicBehaviour{
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
			 	ACLMessage busqueda = blockingReceive(MessageTemplate.MatchPerformative(ACLMessage.REQUEST));
				ACLMessage articulos = blockingReceive(MessageTemplate.MatchPerformative(ACLMessage.REQUEST));
				String text = "";
				try {
					List<Noticia> resultados = (List<Noticia>) articulos.getContentObject();
					List<String> queryTok = (List<String>) busqueda.getContentObject();
					String query = String.join(" ", queryTok);
					// Mostramos los resultados
					if (resultados.isEmpty()) {
						//System.out.println("No se encontraron noticias con el texto buscado.");
						text = "No se encontraron noticias con el texto buscado" + "\n";
					} else {
						//System.out.println("\nResultados encontrados:");
						RefactoredText refactoredQuery = new RefactoredText(query);
						PriorityQueue<Noticia> pq = new PriorityQueue<>();
						for(Noticia resultado : resultados) {
							String resultadoS =  resultado.getCuerpo();
							RefactoredText refactoredText = new RefactoredText(resultadoS);
							Double dist = refactoredQuery.getDistanceL1(refactoredText);
							resultado.setScore(dist);
							pq.offer(resultado);
						}
						ACLMessage aclMessage = new ACLMessage(ACLMessage.INFORM);
						aclMessage.addReceiver(busqueda.getSender());
						aclMessage.setOntology("ontologia");
						aclMessage.setLanguage(new SLCodec().getName());
						aclMessage.setEnvelope(new Envelope());
						aclMessage.getEnvelope().setPayloadEncoding("ISO8859_1");
						aclMessage.setContentObject((Serializable) new ArrayList<Noticia>(pq));
						this.myAgent.send(aclMessage);
						
					}

				} catch (UnreadableException | IOException e) {
					e.printStackTrace();
				}

			}
}
}


