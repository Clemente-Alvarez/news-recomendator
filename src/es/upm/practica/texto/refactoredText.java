package es.upm.practica.texto;

import java.util.Arrays;
import java.util.HashMap;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class refactoredText {

	Map<String, Integer> repetitions;
	private Dictionary d;

	private final String[] redundantWords = {
			// preposiciones
			"a", "ante", "bajo", "cabe", "con", "contra", "de", "desde", "durante", "en", "entre", "hacia", "hasta",
			"mediante", "para", "por", "según", "sin", "so", "sobre", "tras", "versus", "vía",
			// articulos
			"el", "los", "la", "las", "un", "unos", "una", "unas",
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
			// subordinantes

	};



	public refactoredText(String text, Dictionary d) {

		this.d = d;
		text = text.replaceAll("[^a-zA-Z]", "");// quitamos los caracteres que no sean letras
		for (String word : redundantWords) {// eliminar palabras redundantes
			text = text.replaceAll(" " + word + " ", " ");
		}
		text = text.toLowerCase();// pasar a minuscula
		String words[] = text.split(" ");

		repetitions = new HashMap<String, Integer>();

		String rWord = d.contains(words[0]);
		if (!repetitions.get(rWord).equals(null))
			repetitions.put(rWord, repetitions.get(rWord) + 1);
		else
			repetitions.put(rWord, 1);
	}

	public Set<String> getWords() {
		return repetitions.keySet();
	}

	public Integer getRepetitions(String word) {
		return repetitions.get(word);
	}

	boolean usingDictioanry(Dictionary dic) {
		return dic.equals(d);
	}

	double[] genVectorNormL1(double[] unNormalicedVector) {
		double[] result = new double[unNormalicedVector.length];
		double norm = 0.0;
		for (int i = 0; i < unNormalicedVector.length; i++) {
			double value = unNormalicedVector[i];
			norm = +value;// los valores son simepre positivos por eso me ahorro el valor absoluto
			result[i] = value;
		}
		for (int i = 0; i < result.length; i++) {
			result[i] = result[i] / norm;
		}
		return result;
	}

	double[] genVectorNormL2(double[] unNormalicedVector) {
		double[] result = new double[unNormalicedVector.length];
		Double norm = 0.0;
		for (int i = 0; i < unNormalicedVector.length; i++) {
			double value = unNormalicedVector[i];
			norm = +(value * value);// los valores son simepre positivos por eso me ahorro el valor absoluto
			result[i] = value;
		}
		norm = Math.sqrt(norm);
		for (int i = 0; i < result.length; i++) {
			result[i] = result[i] / norm;
		}
		return result;
	}

	private double[][] compareToVector(refactoredText t2) throws Exception {
		if (!t2.usingDictioanry(d))
			throw new Exception("non compatible dictionaries");

		Set<String> words = new HashSet<>();
		words.addAll(getWords());
		words.addAll(t2.getWords());

		double[][] result = new double[2][words.size()];
		double[] v1 = new double[words.size()];
		double[] v2 = new double[words.size()];

		int i = 0;
		for (String word : words) {
			if (getRepetitions(word).equals(null))
				v1[i] = 0.;
			else
				v1[i] = getRepetitions(word);

			if (t2.getRepetitions(word).equals(null))
				v2[i] = 0.;
			else
				v2[i] = t2.getRepetitions(word);

			i++;
		}

		result[0] = v1;
		result[1] = v2;

		return result;
	}

	// cheaper distance calculator
	public double getDistanceL1(refactoredText t2) throws Exception {
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
	public Double getDistanceL2(refactoredText t2) throws Exception {
		double[][] vs = compareToVector(t2);

		double[] v1 = vs[0];
		double[] v2 = vs[1];

		double result = 0.0;

		for (int i = 0; i < v1.length; i++) {
			result = +Math.abs((v1[i] * v1[i]) - (v2[i] * v2[i]));
		}

		return result;
	}
}
