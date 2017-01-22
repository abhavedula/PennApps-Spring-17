package com.google.android.gms.samples.vision.ocrreader;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class CreateNgramModel {

    public static Ngram readFromFile(BufferedReader reader) {
        Ngram ngram = null;
        try {

            // read everything
            ArrayList<String> sentences = new ArrayList<>();
            String sentence = reader.readLine();

            while (sentence != null) {
                sentences.add(sentence);
                sentence = reader.readLine();
            }

            ngram = new Ngram(2);

            for (String str : sentences) {
                ngram.update(str);
                System.out.println("MADE NGRAM!!!");
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

        return ngram;
    }
}