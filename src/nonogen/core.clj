(ns nonogen.core
   (:require [clojure.pprint]
             [nonogen.generators :as gens]
             [nonogen.stories.nights :as nights]
             [nonogen.stories.story]
             [clojure.inspector]
             [nonogen.random]
             ;[caesium.crypto.generichash]
             ;[com.github.emboss/siphash-java]

       )
  (:use [clojure.pprint]
        [clojure.java.io]
        )
  (:gen-class))

(println "\nRunning...")


(defn make-book
  ([]
  (let [book (nth
      (iterate gens/process
               (gens/insert
                (gens/make-generator-stack)
                (nights/a-thousand-and-one-nights)))
    7000)]
    (clojure.inspector/inspect-tree book)
   (:output book
   )))
  ([length]
   (let [seed (nonogen.random/seed)
         book (nth
      (iterate gens/process
               (gens/insert
                (gens/make-generator-stack)
                (nights/a-thousand-and-one-nights seed)))
    length)]
     (println seed)
     (clojure.inspector/inspect-tree book)
     (:output book))))

Double/POSITIVE_INFINITY


(/ Double/POSITIVE_INFINITY 50000M)
(/ 5665188M 50000M)

(defn make-novel []
  (time
    (spit "texts\\output\\NoNoGen2014-009.markdown"
          (apply str (flatten (make-book 5000000)))))
  (print "\nDone\n"))

(make-novel)

;(caesium.crypto.generichash/blake2b (.getBytes "Test"))
;(.getBytes "test")

;[The Infinite Garden of One Thousand and One Stories]
;5,665,188 words, with a seed of 5.16746766851914E8 and a running time of 123337.824542 msecs.
;
