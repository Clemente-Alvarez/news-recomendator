package es.upm.practica;

import java.io.Serializable;

public class Noticia implements Serializable {
	private static final long serialVersionUID = 1L;
	private String titulo;
	private String url;

	public Noticia(String titulo, String url) {
		this.titulo = titulo;
		this.url = url;
	}

	public String getTitulo() {
		return titulo;
	}

	public String getUrl() {
		return url;
	}

	@Override
	public String toString() {
		return titulo + " [" + url + "]";
	}
}
