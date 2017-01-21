package com.example.victoria.myapplication;

/**
 * Created by Victoria on 1/21/2017.
 */

public class Ngram {

    private final int n;

    public Ngram(String corpus, int n) {
        this.n = n;
    }

    private String[] tokenize(String text) {
        String punctuations = "`~!@#$%^&*()_+{}|:\"<>?-=[];'.\\/,";
        for (char punc : punctuations.toCharArray()) {
            text = text.replace(punc, ' ');
        }
        return text.split("\\s+");
    }

    private double prob(String context, String token){
        Tuple<String, String> ct = new Tuple(context, token);
        if(ngrams_count.containsKey(ct)){
            return ngrams_count.get(ct)/contex_count.get(context);
        }
        else{
            return 0;
        }
    }

    private class Tuple<S, T> {
        private final S x;
        private final T y;

        public Tuple(S x, T y) {
            this.x = x;
            this.y = y;
        }
    }


}
