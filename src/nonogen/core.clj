(ns nonogen.core
   (:require [clojure.pprint]
             [nonogen.generators :as gens]
             [nonogen.stories.nights :as nights]
             ;[caesium.crypto.generichash]
             ;[com.github.emboss/siphash-java]

       )
  (:use [clojure.pprint]
        [clojure.java.io]
        )
  (:gen-class))

(println "\nRunning...")


(defn make-book []
  (:output
   (nth
      (iterate gens/process
  (gens/insert (gens/make-generator-stack)
               (nights/make-basic-story
               )))
        600)))


(defn make-novel []
  (time
    (spit "texts\\output\\NoNoGen2014-004.markdown"
          (apply str (flatten (make-book)))))
  (print "\nDone\n"))

(make-novel)

;(caesium.crypto.generichash/blake2b (.getBytes "Test"))
(.getBytes "test")
