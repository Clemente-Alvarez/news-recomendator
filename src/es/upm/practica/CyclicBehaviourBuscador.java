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
			Utils.enviarMensaje(this.myAgent, "operador", respuesta);

		} catch (UnreadableException e) {
			e.printStackTrace();
		}
	}

	public List<Noticia> buscarCadena(List<String> tokens) {

		// Definimos la lista de sitios web que vamos a utilizar
		String sitios[] = { "https://www.elpais.com", "https://www.elpais.com/internacional",
				"https://www.elpais.com/opinion", "https://www.elpais.com/espana", "https://www.elpais.com/economia",
				"https://www.elpais.com/sociedad", "https://elpais.com/clima-y-medio-ambiente",
				"https://elpais.com/ciencia/"

		};
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

					String cuerpo = hasText(titulo, url, tokens); // si tiene en algun lado el token buscado se devuelve el
																	// cuerpo de la noticia
					if (!titulo.isEmpty() && cuerpo != null) {
						System.out.println("_------------------------_");
						System.out.println("TITULO Encontrado!!: " + titulo + " -- " + url);
						lista.add(new Noticia(titulo, url,cuerpo));
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

	private String hasText(String titulo, String href, List<String> tokens) {
		try {
			Document doc = Jsoup.connect(href).get();
			doc.select("a").remove();
			Elements Body = doc.select("h2, h3, h4, p");
			String cuerpo = Body.text();

			for (String token : tokens) {
				if (titulo.toLowerCase().contains(token.toLowerCase())) {
					return cuerpo;
				} // Si esta en titulo
			}

			// ahora en el cuerpo
			System.out.println(href + " Cuerpo: " + cuerpo);
			for (String token : tokens) {
				if (cuerpo.toLowerCase().contains(token)) {
					return cuerpo;
				}
			}
		} catch (Exception err) {
			System.out.println(err.getMessage());
			return null;
		}
		return null;
	}

}
