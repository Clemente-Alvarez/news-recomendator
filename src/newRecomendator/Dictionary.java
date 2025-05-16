import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Dictionary {
    //el nombre de diccionario es algo incorrecto porque un diccionario implica una entrada y su definición o traducción
    Map<String, Integer> wordsMap;
    Map<Integer, String> dicitionaryEntrys;

    Dictionary(String dictioanryPath){
        wordsMap = new HashMap<>();
        dicitionaryEntrys = new HashMap<>();
        String filePath = "yourfile.txt";

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            int lineNumber = 0;

            while ((line = reader.readLine()) != null) {
                wordsMap.put(line, lineNumber);
                dicitionaryEntrys.put(lineNumber, line);
                lineNumber++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    String contains(String word){//TODO tiene que comprobar si esa palabra existe en el diccionario y devolver la raiz de esta
        return null;
    }
    
}

