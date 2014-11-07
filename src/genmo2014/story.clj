(ns genmo2014.engine
   (:require [clojure.pprint]
             ))


(defn valid? [story]
  true)

(defn make-story []
  {:characters []
   :scenes []
   :output []}
  )

(def a-story
  {:characters [{:name "Scheherazade" :tags {:stories [] :gender :female}} {:name "Shahryar" :tags {:gender :male}}]
   :scenes [{:current-character "Scheherazade" :scene :storytelling :storyteller "Scheherazade"}]
   })

(def events-list
  [{:predicates {:scene #(= % :storytelling)}
    :outcome []
    }])

(defn make-storylet [])
(defn make-event [])
;(defn make-action [])

(defn current-scene [story]
  (peek (:scenes story)))

(defn current-character [story]
  (first (filter #(= (:name %1) (:current-character (current-scene story))) (:characters story))))

(defn get-tags
  "Walks through the structure, getting all of the current tags for the current moment."
  [story]
  (let [scene-tags (current-scene story)
        character-tags (:tags (current-character story))
        ]
    (merge scene-tags character-tags)))

(get-tags a-story)

(defn process [])

;(defn make-effect [])

(defn valid-effect? [effect]
  true)

;(defn apply-effect [state effect]
;  (if (valid-effect? effect)
;    (effect state event)
;    state))
