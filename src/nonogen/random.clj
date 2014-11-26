(ns nonogen.random
   (:require [clojure.pprint]
                          (bigml.sampling [simple :as simple]
                             [reservoir :as reservoir]
                             [stream :as stream])
             ))

(import java.util.UUID)


(UUID/randomUUID)

(defn get-random-seed []
  (rand 99999999)
  )


(defn get-seed-from-value [])

(defn shuffle-randomly [to-shuffle seed-source]
  (simple/sample to-shuffle :seed seed-source)
  )

;(print (str seed " "))
;(print (str (into [] (take 1 (simple/sample [1 2 3 4 5 6 7 8 9 0] :seed seed))) " "))


;(println
; (frequencies
;(flatten
; (pmap
;  (fn [s] (take 1 (simple/sample [0 1 2 3 4 5 6 7 8 9] :seed s)))
;  (range 1000000)))))
