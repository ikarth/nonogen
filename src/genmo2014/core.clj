(ns genmo2014.core
   (:require [clojure.pprint]
             [opennlp.nlp :as nlp]
             [opennlp.treebank :as treebank]
             [opennlp.tools.filters :as nlp-filters]
             [opennlp.tools.train :as nlp-train]
             [clj-wordnet.core :as wordnet]
             [markov.core]
             [genmo2014.clean :as clean]
             ;[clj-wordnet.similarity.algo.hso :as hso]
             ;[bigml.sampling.simple :as sample-simple]
             (bigml.sampling [simple :as sample-simple]
                             [reservoir :as sample-reservoir]
                             [stream :as sample-stream])
             ;(bigml.sampling.test [stream :as stream-test])
             [rhyme-finder.core]
       )
  (:use [clojure.pprint]
        [clojure.java.io]
        )
  (:gen-class))

(defn foo
  "I don't do a whole lot."
  [x]
  (println x "Hello, World!"))

(pprint "Running...")

;(def get-sentences (nlp/make-sentence-detector "models/en-sent.bin"))
;(def tokenize (nlp/make-tokenizer "models/en-gutenberg-base-token.bin"))
;(def detokenize (nlp/make-detokenizer "models/english-detokenizer.xml"))
;(def pos-tag (nlp/make-pos-tagger "models/en-pos-maxent.bin"))
;(def name-find (nlp/make-name-finder "models/namefind/en-ner-person.bin"))
;(def chunker (treebank/make-treebank-chunker "models/en-chunker.bin"))
;(def parser (treebank/make-treebank-parser "models/en-parser-chunking.bin"))

;(def wordnet (clj-wordnet.core/make-dictionary "texts/dict/"))

;(map :lemma (clj-wordnet.core/synonyms (first (wordnet "dog" :noun))))

;(use 'markov.core)
