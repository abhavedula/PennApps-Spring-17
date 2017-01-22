package com.example.victoria.myapplication;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

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
                context.add(0, tokens[tokens.length-i-1]);
            } else {
                context.add(0, "<START>");
            }
        }
        l.add(new Tuple<List<String>, String>(context , "<END>"));
        return l;
    }

    public String getCorrected(String in) {
        String corrected = "";

        return corrected;
    }
    
    private String randomToken(List<String> context) {
        if (! contextCount.containsKey(context)) {
            return "";
        }
        double r = (new Random()).nextDouble();
        
        int currentSum = 0;
        
        for(int i = 0; i < contextToTokens.get(context).size(); i++){
            currentSum += prob(context, contextToTokens.get(context).get(i));
            if(currentSum > r){
                return contextToTokens.get(context).get(i);
            }
        }
        return contextToTokens.get(context).get(contextToTokens.get(context).size() - 1);
    }
    
    public String randomText(int tokenCount){
        List<String> context = new ArrayList<String>();
        for(int i = 0; i< n-1; i++){
            context.add("<START>");
        }
        
        String text = "";
        for(int i = 0; i < tokenCount; i++) {
            String token = randomToken(context);
            if(text.length() > 0){
                text += "";
            }
            text += token;
            if(token.equals("<END>")){
                for(int j = 0; j< n-1; j++){
                    context.add("<START>");
                }
            }
            else if(n > 1){
                context.remove(0);
                context.add(token);
            }
        }
        return text;
    }
    
    
    public void update(String sentence) {
        for (Tuple<List<String>, String> g : ngrams(tokenize(sentence))) {
            if (ngramCount.containsKey(g)) {
                contextCount.put(g.x, contextCount.get(g.x) + 1);
                ngramCount.put(g, ngramCount.get(g) + 1);
            } else {
                if (contextCount.containsKey(g.x)) {
                    contextCount.put(g.x, contextCount.get(g.x) + 1);
                    ngramCount.put(g, 1);
                    contextToTokens.get(g.x).add(g.y);
                } else {
                    contextCount.put(g.x, 1);
                    ngramCount.put(g, 1);
                    List<String> l = new LinkedList<>();
                    contextToTokens.put(g.x, l);
                }
            }
        }
    }
    
    private double prob(List<String> context, String token){
        Tuple<List<String>, String> ct = new Tuple<>(context, token);
        System.out.println(context + ", " + ngramCount.containsKey(ct));
        if(ngramCount.containsKey(ct)){
            return ((double) ngramCount.get(ct))/contextCount.get(context);
        }
        else{
            return 0;
        }
    }
    
    public String getMostLikelySugg(List<String> context, String[] sugg){
        if(sugg == null || sugg.length == 0) {
            return "";
        }
        String token = sugg[0];
        
        double max = -1;
        
        for(int i = 0; i < sugg.length; i++){
            if(prob(context, sugg[i]) > max) {
                max = prob(context, sugg[i]);
                token = sugg[i];
            }
        }
        return token;
    }
    
    
    private class Tuple<S, T> {
        private final S x;
        private final T y;
        
        public Tuple(S x, T y) {
            this.x = x;
            this.y = y;
        }
        
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            
            Tuple<?, ?> tuple = (Tuple<?, ?>) o;
            
            if (x != null ? !x.equals(tuple.x) : tuple.x != null) return false;
            return y != null ? y.equals(tuple.y) : tuple.y == null;
            
        }
        
        @Override
        public int hashCode() {
            int result = x != null ? x.hashCode() : 0;
            result = 31 * result + (y != null ? y.hashCode() : 0);
            return result;
        }
    }
    
    
}
