package es.upm.practica;

import java.io.Serializable;

public class Noticia implements Serializable {
	private static final long serialVersionUID = 1L;
	private String titulo;
	private String url;
	private String cuerpo;

	public Noticia(String titulo, String url, String cuerpo) {
		this.titulo = titulo;
		this.url = url;
		this.cuerpo = cuerpo;
	}

	public String getTitulo() {
		return titulo;
	}

	public String getUrl() {
		return url;
	}
	
	public String getCuerpo() {
		return cuerpo;
	}

	@Override
	public String toString() {
		return titulo + " [" + url + "]";
	}
}
