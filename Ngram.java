package com.example.victoria.myapplication;

/**
 * Created by Victoria on 1/21/2017.
 */

public class Ngram {
    
    private class Tuple<S, T> {
        public final S x;
        public final T y;
        
        public Tuple(S x, T y) {
            this.x = x;
            this.y = y;
        }
    }

    private final int n;

    public Ngram(String corpus, int n) {
        this.n = n;
    }

    private void tokenize(String text) {
        //for ()
    }
    
    private int prob(this, context, token){
        Tuple<String, String> ct = new Tuple(context, token);
        float returned;
        if(this.ngrams_count.containsKey(ct)){
            returned = this.ngrams_count.get(ct)/this.contex_count.get(context);
            return returned;
        }
        else{
            return 0;
        }
    }
    
    

    if (context, token) in self.ngrams_count:
        return float(self.ngrams_count[(context, token)]) / self.context_count[context]
        else:
            return 0

            

}
