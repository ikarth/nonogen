(ns nonogen.stories.nights
   (:require [clojure.pprint]
             [clojure.inspector]
             [nonogen.generators :as gens]
             [nonogen.stories.events]
             [nonogen.stories.effects]
             [nonogen.stories.storyon-library]
             ))

;;;;;
;;;;; Generators to output stories
;;;;;


;;;
;;; Story Generation Process
;;;

(defn clear-output [story-generator]
  (assoc-in story-generator [:state :output] []))

(defn generate-story [story-generator]
  (let [story-gen (clear-output story-generator)]
    (nonogen.stories.effects/call-effects story-gen
                                          (nonogen.stories.events/events-to-effects story-gen nonogen.stories.storyon-library/example-storyons))))

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
      :inward ;[(assoc-in story-generator [:state :subgenerator] nil)];
      [(assoc-in story-generator [:state :subgenerator] nil) (:subgenerator (:state story-generator)) ]
            ;[(:subgenerator (:state story-generator))
              ; (assoc-in story-generator [:state :subgenerator] nil)] ;todo: other generator goes here
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
  ([{:keys [characters scenes events output]} generator]
   (gens/make-generator
    {:state {:characters characters
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
             {:tags {:storytelling-beginning true}}))






















;;;
;;; Sketching
;;;


(clojure.inspector/inspect-tree
 (nth
 (iterate gens/process
 (gens/insert (gens/make-generator-stack)
              (add-event
               (add-scene (make-story (make-characters)) {:tags {:storyteller "Scheherazade"}})
               {:tags {:storytelling-beginning true}}
              )))
  15))



;; (let [astory (make-story (make-characters))
;;       tags (nonogen.stories.predicates/get-story-tags astory)]
;;   (nonogen.stories.storyon/select-storyons
;;    (nonogen.stories.storyon/filter-storyons nonogen.stories.storyon-library/example-storyons
;;                                                                                     astory)
;;                                            astory)

;;   )



;; (nonogen.stories.predicates/get-story-tags (make-story (make-characters)))


;; (nonogen.stories.predicates/get-story-tags
;;  (add-scene (make-story (make-characters)) {:tags {:storyteller "Scheherazade"}}))

;; (let [gen-story (add-scene (make-story (make-characters)) {:tags {:storyteller "Scheherazade"}})]
;;   (nonogen.stories.effects/call-effects gen-story
;;    (nonogen.stories.events/events-to-effects
;;     gen-story
;;   nonogen.stories.storyon-library/example-storyons)))



;; ;(def example-story (assoc (make-story)
;; ;  :state {:characters [{:name "Shahryar" :tags {:gender :male}}
;; ;   {:name "Scheherazade" :tags {:stories [] :gender :female :can-tell-stories? true}}]
;; ;          :scenes [{:current-character "Scheherazade" :scene :storytelling :storyteller "Scheherazade"}]
;; ;          :output []
;; ;          }))
;; (def example-story (add-scene (make-story (make-characters)) {:tags {:storyteller "Scheherazade"}}))
;; ((get example-story :generator) example-story)


















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

