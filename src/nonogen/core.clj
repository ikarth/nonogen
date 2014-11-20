(ns nonogen.core
   (:require [clojure.pprint]
             [nonogen.generators :as gens]
             [nonogen.stories.nights :as nights]
       )
  (:use [clojure.pprint]
        [clojure.java.io]
        )
  (:gen-class))

(println "\nRunning...")

(print (apply str "\n\n"
              (:output
 (nth (iterate gens/process
 (gens/insert (gens/make-generator-stack)
              (nights/add-event (nights/add-scene (nights/make-story (nights/make-characters))
                                                  {:tags {:storyteller "Scheherazade"}})
                                {:tags {:storytelling-beginning true}})))
     30))))

