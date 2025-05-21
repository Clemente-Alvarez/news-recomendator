package es.upm.practica.texto;

import java.security.KeyStore.Entry;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class RefactoredText {

	Map<String, Double> repetitions;

	private final String[] redundantWords = {
			// preposiciones
			"a", "ante", "bajo", "cabe", "con", "contra", "de", "desde", "durante", "en", "entre", "hacia", "hasta",
			"mediante", "para", "por", "según", "sin", "so", "sobre", "tras", "versus", "vía",
			// determinantes 
				//articulos
				"el", "los", "la", "las", "un", "unos", "una", "unas", "lo", 
				//Posesivos
				"mi", "mis", "tu", "tus", "nuestro", "nuestra", "nuestros", "nuestras", "vuestros", "vuestras", "vuestro", "vuestra", "sus", "su",
				//demostrativos
				"este", "esta",  "estos", "estas", "ese", "esa", "esas", "esos",  "aquel", "aquella", "aquellos", "aquellas", 
				//indefinidos
				"alguno", "alguna", "algunos", "algunas", "cierto", "cierta", "ciertos", "ciertas", "bastante", "bastantes",
			//pronombres
			"yo", "nosotros", "vosotros", "ellos", "ellas",
				// conjunciones
				// coordinantes
				// copulativas
				"y", "e", "ni", "así", "como", "también",
				// Disyuntivas
				"o", "u", "bien", "si",
				// Adversativas
					"pero", "ahora", "sin", "embargo", "sino", "cambio", "obstante", "excepto", "salvo", "menos",
					// Distributivas
					"ora", "ya", "no", "solamente", "que",
					// Ilativas
					"pues", "tanto", "por", "consiguiente", "es", "tal", "ahí", "sea",
					// Continuativas
					"aún", "además", "más", "otra", "asimismo", "otro",
	};

	public RefactoredText() {
		repetitions = new HashMap<>();
		
	}

	public RefactoredText(String text) {

		text = text.replaceAll("[^a-zA-Z0-9]", "");// quitamos los caracteres que no sean letras
		text = " " + text + " "; 
		for (String word : redundantWords) {// eliminar palabras redundantes
			text = text.replaceAll(" " + word + " ", " ");
		}
		text = text.toLowerCase();// pasar a minuscula
		String words[] = text.split(" ");

		repetitions = new HashMap<String, Double>();

		for(int i = 0; i < words.length; i++){
			if (!repetitions.containsKey(words[i]))
				repetitions.put(words[i], repetitions.get(words[i]) + 1);
			else
				repetitions.put(words[i], 1.0);
		}

		genVectorNormL1();
		//genVectorNormL2();
	}

	public void setText(String text) {
		text = text.replaceAll("[^a-zA-Z0-9]", "");// quitamos los caracteres que no sean letras
		text = " " + text + " "; 
		for (String word : redundantWords) {// eliminar palabras redundantes
			text = text.replaceAll(" " + word + " ", " ");
		}
		text = text.toLowerCase();// pasar a minuscula
		String words[] = text.split(" ");
	}
	
	public Set<String> getWords() {
		return repetitions.keySet();
	}

	public double getRepetitions(String word) {
		return repetitions.get(word);
	}

	public boolean containsWord(String word) {
		return repetitions.containsKey(word);
	}

	private void genVectorNormL1() {
		Collection<Double> temp = repetitions.values();
		double norm = 0.0;
		for (Double value : temp) {
			norm =+value;// los valores son simepre positivos por eso me ahorro el valor absoluto
		}
		Set<String> words = repetitions.keySet();
		for (String word : words) {
			repetitions.put(word, repetitions.get(word)/norm);
		}
	}

	private void genVectorNormL2() {
		Collection<Double> temp = repetitions.values();
		Double norm = 0.0;
		for (Double value : temp) {
			norm =+(value * value);// los valores son simepre positivos por eso me ahorro el valor absoluto
		}
		norm = Math.sqrt(norm);
		Set<String> words = repetitions.keySet();
		for (String word : words) {
			repetitions.put(word, repetitions.get(word)/norm);
		}
	}

	private double[][] compareToVector(RefactoredText t2){

		Set<String> words = new HashSet<>();
		words.addAll(getWords());
		words.addAll(t2.getWords());

		double[][] result = new double[2][words.size()];
		double[] v1 = new double[words.size()];
		double[] v2 = new double[words.size()];

		int i = 0;
		for (String word : words) {
			if (containsWord(word))
				v1[i] = getRepetitions(word);
			else
				v1[i] = 0.;

			if (t2.containsWord(word))
				v2[i] = t2.getRepetitions(word);
			else
				v2[i] = 0.;

			i++;
		}

		result[0] = v1;
		result[1] = v2;

		return result;
	}

	// cheaper distance calculator
	public double getDistanceL1(RefactoredText t2){
		double[][] vs = compareToVector(t2);

		double[] v1 = vs[0];
		double[] v2 = vs[1];

		double result = 0.0;

		for (int a = 0; a < v1.length; a++) {
			result = +Math.abs(v1[a] - v2[a]);
		}

		return result;
	}

	// More precise distance calculator
	public Double getDistanceL2(RefactoredText t2){
		double[][] vs = compareToVector(t2);

		double[] v1 = vs[0];
		double[] v2 = vs[1];

		double result = 0.0;

		for (int i = 0; i < v1.length; i++) {
			result = +Math.abs((v1[i] * v1[i]) - (v2[i] * v2[i]));
		}

		return Math.sqrt(result);
	}
}
