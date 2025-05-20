package es.upm.practica;

import jade.content.lang.sl.SLCodec;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

public class AgenteBuscador extends Agent {
	public void setup() {
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setName("Buscador");
		
		sd.setType("buscar");
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
		addBehaviour(new CyclicBehaviourBuscador());
	}
}
