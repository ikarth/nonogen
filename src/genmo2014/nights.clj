(ns genmo2014.nights
   (:require [clojure.pprint]
             [genmo2014.generators :as gens]))

;;;; Generators to output stories








(defn story [story-generator]
  {:output nil
   :generator nil
   :feedback nil
   })

;(fn [gen]
;                {:output (:state gen);(:generator gen)
;                 :generator (merge gen {:state (into [] (map inc (:state gen)))})
;                 :feedback []})
(defn make-story []
  (gens/make-generator
   {:state {:characters []
            :scenes []
            :output []}
    :generator story}))


(assoc (make-story)
  :state {:characters [{:name "Scheherazade" :tags {:stories [] :gender :female}} {:name "Shahryar" :tags {:gender :male}}]
          :scenes [{:current-character "Scheherazade" :scene :storytelling :storyteller "Scheherazade"}]
          :output []})

;(def example-story
;  (gens/make-generator
; {:state [0]
;  :generator story}))
