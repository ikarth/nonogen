(ns nonogen.random
   (:require [clojure.pprint]
                          (bigml.sampling [simple :as simple]
                             [reservoir :as reservoir]
                             [stream :as stream])
             ))

(import java.util.UUID)


(UUID/randomUUID)

(defn seed []
  (rand 999999999)
  )

(defn get-random-seed []
  376;(rand 99999999)
  )

(defn die-roll [sides seed]
  (first (simple/sample (range sides) :seed seed)))

(defn string-to-seed
  "Takes a string and turns it into a seed value."
  [string]
  (hash string))

(defn data-to-seed
  "Takes a collection and turns it into a seed value."
  [data]
  (hash data))


(defn shuffle-randomly [to-shuffle seed-source]
  (simple/sample to-shuffle :seed seed-source))

(defn variation [a-coll a-seed]
  (loop [coll a-coll seed a-seed]
    (if (not (vector? coll))
      (if (not (list? coll))
        coll
        (into [] (map #(variation % seed) coll)))
      (recur (first (nonogen.random/shuffle-randomly coll seed)) seed))))

;(print (str seed " "))
;(print (str (into [] (take 1 (simple/sample [1 2 3 4 5 6 7 8 9 0] :seed seed))) " "))


;(println
; (frequencies
;(flatten
; (pmap
;  (fn [s] (take 1 (simple/sample [0 1 2 3 4 5 6 7 8 9] :seed s)))
;  (range 1000000)))))
