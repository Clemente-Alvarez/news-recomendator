package es.upm.practica;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import jade.content.lang.sl.SLCodec;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.FIPAAgentManagement.Envelope;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import jade.util.leap.HashSet;

public class CyclicBehaviourBuscador extends CyclicBehaviour {
	private static final long serialVersionUID = 1L;
	public static final int LIMITES = 50;

	public void action() {
		// Espera de mensajes en modo bloqueante y con un filtro de tipo
		ACLMessage msg = this.myAgent.blockingReceive(MessageTemplate.MatchPerformative(ACLMessage.REQUEST));
		try {
			// Imprimimos por pantalla el texto a buscar
//			System.out.println(msg.getSender().getName() + ":" + (String) msg.getContentObject());
			// Creamos una lista de respuestas y llamamos a nuestro método buscarCadena(),
			// que utilizamos para realizar la búsqueda
			List<Noticia> respuesta = buscarCadena((List<String>) msg.getContentObject());
			// Cuando la búsqueda ha finalizado, enviamos un mensaje de respuesta
			ACLMessage aclMessage = new ACLMessage(ACLMessage.INFORM);
			aclMessage.addReceiver(msg.getSender());
			aclMessage.setOntology("ontologia");
			aclMessage.setLanguage(new SLCodec().getName());
			aclMessage.setEnvelope(new Envelope());
			aclMessage.getEnvelope().setPayloadEncoding("ISO8859_1");
			aclMessage.setContentObject((Serializable) respuesta);
			this.myAgent.send(aclMessage);
		} catch (UnreadableException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public List<Noticia> buscarCadena(List<String> tokens) {

		// Definimos la lista de sitios web que vamos a utilizar
		String sitios[] = {  "https://www.elpais.com" };     
//"https://www.elmundo.es",
		List<Noticia> lista = new ArrayList<>();
	

		// Buscamos en cada una de las páginas web si hay coincidencia con el texto que
		// ha enviado el cliente
		// Consider adding URL deduplication
		
		for (int i = 0; i < sitios.length; i++) {
			try {
				Document doc = Jsoup.connect(sitios[i]).get();

	            Elements newsLinks = doc.select("h2 > a");
				System.out.println("LINKs: " + newsLinks);
				
				for (Element link : newsLinks) {
					String titulo = link.text().trim();
	                String url = link.absUrl("href");

					if(!titulo.isEmpty() && hasText(titulo, url, tokens)) {
						System.out.println("_------------------------_");
						System.out.println("TITULO: " + titulo + " -- " + url);
						lista.add(new Noticia(titulo,url));
					}
					
				}	
//
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return lista;
	}

	  private boolean containsIgnoreCase(String text, String search) {
	        return text.toLowerCase().contains(search.toLowerCase());
	    }
	private boolean hasText(String titulo, String href, List<String> tokens) {
//		System.out.println("Titulo: " + titulo + " -- " + href );
		for(String token : tokens) {
			System.out.println("token: " + token);
			if (titulo.toLowerCase().contains(token.toLowerCase())) {
				return true;
			} //Si esta en titulo
		}

		// ahora en el cuerpo
		try {
			Document doc = Jsoup.connect(href).get();
			doc.select("a").remove(); 
			Elements Body = doc.select("h2, h3, h4, p");
			String cuerpo = Body.text();

//			System.out.println("Cuerpo: " + cuerpo);
			
			for(String token : tokens) {
				if(cuerpo.toLowerCase().contains(token)) {
					return true;
				}
			}
			
		} catch (Exception err) {
			System.out.println(err.getMessage());
			return false;
		}
		return false;
	}

}
