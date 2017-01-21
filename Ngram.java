package com.example.victoria.myapplication;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by Victoria on 1/21/2017.
 */

public class Ngram {

    private final int n;
    private Map<List<String>, Integer> contextCount;
    private Map<List<String>, List<String>> contextToTokens;
    private Map<Tuple<List<String>, String>, Integer> ngramCount;

    public Ngram(int n) {
        this.n = n;
        contextCount = new HashMap<>();
        contextToTokens = new HashMap<>();
        ngramCount = new HashMap<>();
    }

    private String[] tokenize(String text) {
        String punctuations = "`~!@#$%^&*()_+{}|:\"<>?-=[];'.\\/,";
        for (char punc : punctuations.toCharArray()) {
            text = text.replace(punc, ' ');
        }
        return text.split("\\s+");
    }

    private List<Tuple<List<String>, String>> ngrams(String[] tokens) {
        List<Tuple<List<String>, String>> l = new LinkedList<>();
        for (int i = 0; i < tokens.length; i++) {
            List<String> context = new LinkedList<>();
            for (int j = 0; j < n - 1; j++) {
                if (i - j - 1 < 0) {
                    context.add(0, "<START>");
                } else {
                    context.add(0, tokens[i - j - 1]);
                }
            }
            l.add(new Tuple<List<String>, String>(context , tokens[i]));
        }
        List<String> context = new LinkedList<>();
        for (int i = 0; i < n - 1; i++) {
            if (i < tokens.length) {
                context.add(0, tokens[-i-1]);
            } else {
                context.add(0, "<START>");
            }
        }
        l.add(new Tuple<List<String>, String>(context , "<END>"));
        return l;
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
