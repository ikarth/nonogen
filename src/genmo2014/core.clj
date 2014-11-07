(ns genmo2014.core
   (:require [clojure.pprint]
             [opennlp.nlp :as nlp]
             [opennlp.treebank :as treebank]
             [opennlp.tools.filters :as nlp-filters]
             [opennlp.tools.train :as nlp-train]
             [clj-wordnet.core :as wordnet]
             [markov.core]
             [genmo2014.clean :as clean]
             [clj-wordnet.similarity.algo.hso :as hso]
             ;[bigml.sampling.simple :as sample-simple]
             (bigml.sampling [simple :as sample-simple]
                             [reservoir :as sample-reservoir]
                             [stream :as sample-stream])
             ;(bigml.sampling.test [stream :as stream-test])
             [rhyme-finder.core]
             [incanter [core stats charts]]
             [incanter.stats]
             [clojure.data.generators]
             [cemerick.pprng :as rng]
             [caesium.crypto.generichash]
             [caesium.util]
             ;[org.clojars.ikarth.libsodium]
             ;[com.climate.prng.generators.mersenne-twister :as mt]
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

;(println (seq (.getURLs (java.lang.ClassLoader/getSystemClassLoader))))

;(cemerick.pprng/int (cemerick.pprng/rng 9989779))

;(def example-uuid (clojure.data.generators/uuid))

(defn rand-seq [seed]
  (let [r (java.util.Random. seed)]
    (repeatedly #(binding [clojure.data.generators/*rnd* r]
                  (inc (clojure.data.generators/uniform 0 10))))))

(take 10 (rand-seq 8))

(def empty-string-digest
  (caesium.util/unhexify "786a02f742015903c6c6fd852552d272912f4740e15847618a86e217f71f5419d25e1031afee585313896444934eb04b903a685b1448b755d56f701afe9be2ce"))

(byte-array [90])
;(caesium.crypto.generichash/blake2b (byte-array [90]))
;(caesium.crypto.generichash/blake2b (byte-array []))


 ;(caesium.util/unhexify "4884256d056fb76f83f10ab85c127682d447d126d99dee526883488f57951fffb576a16d8a7fd391420e23b7c0cf14b413878de095dc3d84bcaecba0bc657c77")
 ;       )

;(incanter.stats/sample (take 10 (rand-seq 8)) :size 2 :replacement :false)
(println (. System getProperty "java.library.path"))

(defn xors [max-x max-y]
  (for [x (range max-x) y (range max-y)]
    [x y (bit-xor x y)]))

(xors 4 4)

(def frame (java.awt.Frame.))

(for [meth (.getMethods java.awt.Frame)
      :let [name (.getName meth)]
      :when (re-find #"Vis" name)]
  name)
