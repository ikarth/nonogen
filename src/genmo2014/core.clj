(ns genmo2014.core
   (:require [clojure.pprint]
             [opennlp.nlp]
             [clj-wordnet.core]
             [markov.core]
             [genmo2014.clean]
       )
  (:use [clojure.pprint]
        [markov.core :as markov]
        [opennlp.nlp :as nlp]
        [clojure.java.io]
        [genmo2014.clean :as clean]
        )
  (:gen-class))

(defn foo
  "I don't do a whole lot."
  [x]
  (println x "Hello, World!"))

(pprint "Running...")

;(def get-sentences (make-sentence-detector "models/en-sent.bin"))
;(def tokenize (make-tokenizer "models/en-gutenberg-base-token.bin"))
;(def detokenize (make-detokenizer "models/english-detokenizer.xml"))
;(def pos-tag (make-pos-tagger "models/en-pos-maxent.bin"))
;(def name-find (make-name-finder "models/namefind/en-ner-person.bin"))
;(def chunker (make-treebank-chunker "models/en-chunker.bin"))
;(def parser (make-treebank-parser "models/en-parser-chunking.bin"))

(def wordnet (clj-wordnet.core/make-dictionary "texts/dict/"))
