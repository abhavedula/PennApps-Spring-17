############################################################
# CIS 521: Homework 7
############################################################

student_name = "Victoria Huang"

############################################################
# Imports
############################################################

import string
import random
import math

############################################################
# Section 1: Markov Models
############################################################

def tokenize(text):
    for punc in string.punctuation:
        text = text.replace(punc, ' '+ punc +' ')
    return text.split();

def ngrams(n, tokens):
    l = []
    for i in xrange(len(tokens)):
        context = []
        for j in xrange(n-1):
            if i-(j+1) < 0:
                context.insert(0,'<START>')
            else:
                context.insert(0,tokens[i-(j+1)])
        l.append((tuple(context), tokens[i]))
    context = []
    for i in xrange(n-1):
        if i < len(tokens):
            context.insert(0, tokens[-(i+1)])
        else:
            context.insert(0, '<START>')
    l.append((tuple(context), '<END>'))
    return l

class NgramModel(object):

    def __init__(self, n):
        self.n = n
        self.ngrams_count = {}
        self.context_count = {}
        self.context_to_tokens = {}

    def update(self, sentence):
        for g in ngrams(self.n, tokenize(sentence)):
            if g in self.ngrams_count:
                self.context_count[g[0]] += 1
                self.ngrams_count[g] += 1
            else:
                if g[0] in self.context_count:
                    self.context_count[g[0]] += 1
                    self.ngrams_count[g] = 1
                    self.context_to_tokens[g[0]].append(g[1])
                else:
                    self.context_count[g[0]] = 1
                    self.ngrams_count[g] = 1
                    self.context_to_tokens[g[0]] = [g[1]]

    def prob(self, context, token):
        if (context, token) in self.ngrams_count:
            return float(self.ngrams_count[(context, token)]) / self.context_count[context]
        else:
            return 0

    def random_token(self, context):
        if not context in self.context_count:
            return ''
        r = random.random()
        self.context_to_tokens[context].sort()
        curr_sum = 0
        
        for i in xrange(len(self.context_to_tokens[context])):
            curr_sum += self.prob(context, self.context_to_tokens[context][i])
            if curr_sum > r:
                return self.context_to_tokens[context][i]
                
    def random_text(self, token_count):
        context = ['<START>' for i in xrange(self.n-1)]
        text = ""
        
        for i in xrange(token_count):
            token = self.random_token(tuple(context))
            if len(text) > 0:
                text += " "
            text += token
            if token == '<END>':
                context = ['<START>' for i in xrange(self.n-1)]
            elif self.n > 1:
                context.pop(0)
                context.append(token)
        return text

    def perplexity(self, sentence):
        tokens = tokenize(sentence)
        total_prob = math.log(1)
        
        for i in xrange(len(tokens)):
            context = []
            for j in xrange(self.n-1):
                if i-(j+1) < 0:
                    context.insert(0,'<START>')
                else:
                    context.insert(0,tokens[i-(j+1)])
            total_prob += math.log(1/self.prob(tuple(context), tokens[i]))
            
        context = []
        for i in xrange(self.n-1):
            if i < len(tokens):
                context.insert(0, tokens[-(i+1)])
            else:
                context.insert(0, '<START>')
        total_prob += math.log(1/self.prob(tuple(context), '<END>'))
        total_prob = math.exp(total_prob)
        total_prob = math.pow(total_prob, 1/float(len(tokens) + 1))
        return total_prob

def create_ngram_model(n, path):
    f = open(path)
    sentences = f.readlines()
    f.close()
    ngram = NgramModel(n)
    for sentence in sentences:
        ngram.update(sentence)
    return ngram

############################################################
# Section 2: Feedback
############################################################

feedback_question_1 = """
3 hours
"""

feedback_question_2 = """
The most challenging aspect was understanding how to calculate the perplexity.
There were no significant stumbling blocks.
"""

feedback_question_3 = """
I like being able to use what we learned in class and see what it actually does.
I would not change anything.
"""