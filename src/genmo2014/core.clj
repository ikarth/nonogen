(ns genmo2014.core
   (:require [clojure.pprint]
             [genmo2014.generators :as gens]
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

(def gen-stack (genmo2014.generators/make-generator-stack))

(def example-generator
  (gens/make-generator
 {:state [0]
  :generator (fn [gen]
                {:output (:state gen);(:generator gen)
                 :generator (merge gen {:state (into [] (map inc (:state gen)))})
                 :feedback []})}))


(gens/insert gen-stack example-generator)

(nth (iterate gens/process (gens/insert gen-stack example-generator)) 15)
