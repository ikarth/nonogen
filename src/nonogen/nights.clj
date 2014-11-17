(ns nonogen.nights
   (:require [clojure.pprint]
             [clojure.inspector]
             [nonogen.generators :as gens]
             [nonogen.storyon]))

;;;;;
;;;;; Generators to output stories
;;;;;

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
             }
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

(defn get-current-event [story]
  (peek (get-in story [:state :event])))

(defn get-current-character [story]
  (peek (get-in story [:state :characters]))) ; todo: pick current character by event

; todo: Randomly generate some characters, or create characters from a set of templates
(defn make-characters
  "Returns a vector of newly-created characters."
  []
  [{:name "Shahryar" :tags {:gender :male}}
   {:name "Scheherazade" :tags {:stories [] :gender :female :can-tell-stories? true}} ])



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
  (let [state (get story :state)
        scene (peek (get state :scenes))
        event (get-current-event story) ;todo: add support for event tags
        current-character (get-current-character story)
        ;event-tags ((:get-tags (peek (get state :events))))
        ]
    (merge
     (:tags state)
     (:tags scene)
     (:tags current-character))))

;(into {} [{:x 1} {:y 5} nil nil nil])
;(get-tags (make-story))
;(merge '({:x 1} {:y 2} nil))
;
;(get-tags
; (add-tag (add-tag (make-story (make-characters))
;                   {:test 1} )
;          {:xtest 2}))





;(defn process-event [story-generator]
;  (let [events (:events (:state story-generator))]
;    ))

;(defn make-story []
;  (gens/make-generator
;  {:state {:characters []
;            :scenes []
;            :events []
;            :output []
;            }
;    :generator (fn [g] (story g))}))







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

