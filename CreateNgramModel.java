import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import com.example.victoria.myapplication.Ngram;

public class CreateNgramModel {

    public static Ngram readFromFile(String path) {
        Ngram ngram;
        BufferedReader reader = null;
        try {

          // read everything
          reader = new BufferedReader(new FileReader(path));
          ArrayList<String> sentences = new ArrayList<>();
          String sentence = reader.readLine();

          while (sentence != null) {
            sentences.add(sentence);
            sentence = reader.readLine();
          }

          ngram = new Ngram(4);
          
          for (String str : sentences) {
            ngram.update(str);
        }
          return ngram;
          
        } catch (IOException e) {
       //   e.printStackTrace();
        }

        // close the reader
        if (reader != null) {
          try {
            reader.close();
          } catch (IOException e) {
            e.printStackTrace();
          }
        }

        return null;
      }
}