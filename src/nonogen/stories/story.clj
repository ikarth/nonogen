(ns nonogen.stories.story
   (:require [clojure.pprint]
             [clojure.inspector]
             [nonogen.generators :as gens]
             [nonogen.stories.events]
             [nonogen.stories.effects]
             [nonogen.stories.storyon]
             [nonogen.random :as random]
             ))

;;;;;
;;;;; Generators to output stories
;;;;;




;; --- Process story ---
;; Passed a story (& maybe the storyon list?)
;;  Figure out the current tag set
;;   Call Process Event Queue
;;   Call Process Effects
;; Check the state of the story
;;   If the story's state includes an exit command, return (with :generator [] set appropreately)
;;   Otherwise, recur

(defn exit-state [story-generator]
  (let [exit (:exit (:state story-generator))]
    (case exit
      :outward nil
      :inplace [story-generator]
      :inward [(assoc-in story-generator [:state :subgenerator] nil)
               (assoc-in (get-in story-generator [:state :subgenerator]) [:state :seed] (get-in story-generator [:state :seed])) ] ; the order is important!
      :forward [(:inward (:state story-generator))]
      [story-generator])))

(defn story [story-generator]
  (let [output
    (let [story-gen (generate-story story-generator)]
      ;(clojure.pprint/pprint story-gen)
      {:output (:output (:state story-gen))
       :generator (exit-state story-gen)
       :feedback nil})]
    output))

;;;
;;; Stories
;;;

(defn make-story
  ([]
   (make-story {:characters [] :scenes [] :events [] :output []} (fn [g] (story g))))
  ([characters]
   (make-story {:characters characters :scenes [] :events [] :output []} (fn [g] (story g))))
  ([{:keys [characters scenes events output depth]} generator]
   (gens/make-generator
    {:state {
             :seed (random/get-random-seed)
             :characters characters
             :scenes scenes
             :events events
             :output output
             :exit nil             }
     :generator generator})))

(defn add-generator [story generator]
  (assoc story :generator generator))

(defn add-character [story character]
  (assoc-in story [:state :characters] (conj (:characters (:state story)) character)))

(defn add-to-story [story thing-type thing]
  (assoc-in story thing-type (conj (get-in story thing-type) thing)))

(defn add-event [story event]
  ((partial add-to-story story [:state :events]) event))

(defn add-tag [story thing]
  (assoc-in story [:state :tags] (merge (get-in story [:state :tags]) thing)))

(defn add-scene [story scene]
  ((partial add-to-story story [:state :scenes]) scene))

; todo: Randomly generate some characters, or create characters from a set of templates
(defn make-characters
  "Returns a vector of newly-created characters."
  []
  [{:name "Shahryar" :tags {:gender :male}}
   {:name "Scheherazade" :tags {:stories [] :gender :female :can-tell-stories? true}} ])

(defn make-basic-story []
  (add-event (add-scene (make-story (make-characters))
                        {:tags {:storyteller "Scheherazade"}})
             {:tags {:event :story-introduction}}))



(defn make-event [event-map]
  event-map)
  ;(merge event-map {:seed 7}));(hash (str event-map))}))
