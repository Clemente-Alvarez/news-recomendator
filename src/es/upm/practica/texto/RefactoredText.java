package es.upm.practica.texto;

import java.security.KeyStore.Entry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;

import java.util.HashSet;
import java.util.List;
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

	public RefactoredText(String text) {

		text = text.replaceAll("[^a-zA-Z0-9\s]", "");// quitamos los caracteres que no sean letras
		text = " " + text + " "; 
		for (String word : redundantWords) {// eliminar palabras redundantes
			text = text.replaceAll(" " + word + " ", " ");
		}
		text = text.toLowerCase();// pasar a minuscula
		String words[] = text.split(" ");

		repetitions = new HashMap<String, Double>();

		for(int i = 0; i < words.length; i++){
			if (repetitions.containsKey(words[i]))
				repetitions.put(words[i], repetitions.get(words[i]) + 1);
			else
				repetitions.put(words[i], 1.0);
		}

		//genVectorNormL1();
		//genVectorNormL2();
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

	private void genVectorNormL1(List<List<Double>> vectors) {
//		Collection<Double> temp = repetitions.values();
//		double norm = 0.0;
//		for (Double value : temp) {
//			norm += value;// los valores son simepre positivos por eso me ahorro el valor absoluto
//		}
//		Set<String> words = repetitions.keySet();
//		for (String word : words) {
//			repetitions.put(word, repetitions.get(word)/norm);
//		}
		for(List<Double> vector : vectors) {
			Double sum = .0;
			for (Double d : vector) {
				sum += d*d;
			}
			sum = Math.sqrt(sum);
			for (int i = 0; i<vector.size(); i++) {
				vector.set(i, vector.get(i)/sum);
			}
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

	private List<List<Double>> getWeights(List<RefactoredText> list){
		Map<String,Double> N = new HashMap<>();
		Map<String,Double> df = new HashMap<>();
		Set<String> words = new HashSet<>();
		words.addAll(getWords());
		for (RefactoredText rt : list) {
			words.addAll(rt.getWords());
		}
		for (String word : words) {
			for (RefactoredText rt : list) {
				if(rt.containsWord(word)) {
					if(!df.containsKey(word)) df.put(word, 1.0);
					else df.put(word, df.get(word)+1);
				}
			}
		}
		for (RefactoredText rt : list) {
			for (String word : words) {
				if(!N.containsKey(word)) {
					if(rt.containsWord(word)) N.put(word, rt.getRepetitions(word));
					else N.put(word, .0);
				}
				else {
					if(rt.containsWord(word)) N.put(word, N.get(word)+rt.getRepetitions(word));
				}
			}
		}
		List<List<Double>> w = new ArrayList<>();
		w.add(new ArrayList<>());
		for (String word : words) {
			if(containsWord(word))
				w.get(0).add(Math.log10(1+getRepetitions(word))+Math.log10(N.get(word)/df.get(word)));
			else 
				w.get(0).add(Math.log10(N.get(word)/df.get(word)));
		}
		int j = 1;
		for (RefactoredText rt : list) {
			w.add(new ArrayList<>());
			for (String word : words) {
				if(rt.containsWord(word))
					w.get(j).add(Math.log10(1+rt.getRepetitions(word))+Math.log10(N.get(word)/df.get(word)));
				else 
					w.get(j).add(Math.log10(N.get(word)/df.get(word)));
			}
			j++;
		}
//		double[][] result = new double[2][words.size()];
//		double[] v1 = new double[words.size()];
//		double[] v2 = new double[words.size()];

//		int i = 0;
//		for (String word : words) {
//			if (containsWord(word))
//				v1[i] = getRepetitions(word);
//			else
//				v1[i] = 0.;
//
//			if (t2.containsWord(word))
//				v2[i] = t2.getRepetitions(word);
//			else
//				v2[i] = 0.;
//
//			i++;
//		}
//
//		result[0] = v1;
//		result[1] = v2;

//		return result;
		return w;
	}

	// cheaper distance calculator
	public List<Double> getDistanceL1(List<RefactoredText> list){
		List<Double> result = new ArrayList<>();
		List<List<Double>> vs = getWeights(list);
		genVectorNormL1(vs);
		List<Double> v1 = vs.get(0);
		int size = v1.size();
		for (int i = 0; i<list.size(); i++) {
			Double dist = .0;
			for(int j = 0; j<size; j++) {
				dist += Math.abs(v1.get(j)-vs.get(i+1).get(j));
			}
			 result.add(dist);
		}
		
//		for (int a = 0; a < v1.length; a++) {
//			result = +Math.abs(v1[a] - v2[a]);
//		}
//		System.out.println("result: " + result);
		return result;
	}

	// More precise distance calculator
//	public Double getDistanceL2(RefactoredText t2){
//		double[][] vs = compareToVector(t2);
//
//		double[] v1 = vs[0];
//		double[] v2 = vs[1];
//
//		double result = 0.0;
//
//		for (int i = 0; i < v1.length; i++) {
//			result = +Math.abs((v1[i] * v1[i]) - (v2[i] * v2[i]));
//		}
//
//		return Math.sqrt(result);
//	}
}
