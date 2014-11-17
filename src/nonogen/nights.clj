(ns nonogen.nights
   (:require [clojure.pprint]
             [nonogen.generators :as gens]
             [nonogen.storyon]))

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
      :inplace story-generator
      :inward (into [] story-generator (:subgenerator (:state story-generator))) ;todo: other generator goes here
      :forward (:inward (:state story-generator)))))

(defn story [story-generator]
  (let [story-gen (nonogen.storyon/generate-story story-generator)]
    (let [exit (:exit (:state story-gen))]
      (if exit
        {:output (:output (:state story-gen))
         :generator (exit-state story-gen)
         :feedback nil}
        story-gen;(recur story-gen)
      )
      (println story-gen)
      {:output (:output (:state story-gen))
       :generator {};(exit-state story-gen)
       :feedback nil}

      )))



;; --- Get Tags ---
;; Go through all the structures in the story-state
;;   Get their :tags
;;   Merge them into one unified map
;;   Return that

(defn get-tags [story]
  (into {}
        ))


;(defn process-event [story-generator]
;  (let [events (:events (:state story-generator))]
;    ))

(defn make-story []
  (gens/make-generator
   {:state {:characters []
            :scenes []
            :events []
            :output []
            }
    :generator (fn [g] (story g))}))

;; todo: Randomly generate some characters, or create characters from a set of templates
(defn make-characters
  "Returns a vector of newly-created characters."
  []
  [{:name "Scheherazade" :tags {:stories [] :gender :female :can-tell-stories? true}} {:name "Shahryar" :tags {:gender :male}}])






;;;
;;; Sketching
;;;

;(def example-story (assoc (make-story)
;  :state {:characters [{:name "Scheherazade" :tags {:stories [] :gender :female}} {:name "Shahryar" :tags {:gender :male}}]
;;          :scenes [{:current-character "Scheherazade" :scene :storytelling :storyteller "Scheherazade"}]
;          :output []
;          }))
;example-story



;((get example-story :generator) example-story)
;(genmo2014.storyon/generate-story example-story)


;(def gen-stack (genmo2014.generators/make-generator-stack))
;(gens/insert gen-stack example-story)
;(gens/process (gens/insert gen-stack example-story))
;(gens/process (gens/insert (gens/insert gen-stack example-story) example-story))
;(gens/process (gens/process (gens/insert (gens/insert gen-stack example-story) example-story)))

;(nth (iterate gens/process (gens/insert gen-stack example-story)) 15)

;(clojure.pprint/pprint (nth (iterate gens/process (gens/insert gen-stack example-story)) 15))

