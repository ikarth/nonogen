(ns nonogen.stories.story
   (:require [nonogen.generators :as gens]
             [nonogen.stories.events]
             [nonogen.stories.effects]
             [nonogen.stories.characters]
             [nonogen.random :as random]
             [nonogen.stories.labyrinth]
             ))

;;;;;
;;;;; Generators to output stories
;;;;;

;;;
;;; Story Generation Process
;;;


(defn clear-state
  "Clears out bits of the story state that should not carry over from last time."
  [story-generator]
  (assoc-in (assoc-in story-generator [:state :output] [])
            [:state :exit]
            nil))

(defn generate-story
  "Runs the story generator "
  [story-generator storyon-lib]
  (let [story-gen (clear-state story-generator)]
    (nonogen.stories.effects/call-effects story-gen
                                          (nonogen.stories.events/events-to-effects story-gen storyon-lib))))

;; --- Process story ---
;; Passed a story (& maybe the storyon list?)
;;  Figure out the current tag set
;;   Call Process Event Queue
;;   Call Process Effects
;; Check the state of the story
;;   If the story's state includes an exit command, return (with :generator [] set appropreately)
;;   Otherwise, recur

(defn generate-story-fn [storyon-lib]
  (fn [story-gen]
    (generate-story story-gen storyon-lib)))


(defn exit-state [story-generator]
  (let [exit (:exit (:state story-generator))]
    (case exit
      :outward nil
      :inplace [story-generator]
      :inward [(assoc-in story-generator [:state :subgenerator] nil)
               (assoc-in (get-in story-generator [:state :subgenerator]) [:state :seed] (get-in story-generator [:state :seed])) ] ; the order is important!
      :forward [(:inward (:state story-generator))]
      [story-generator])))

(defn story [story-generator storyon-lib]
  (let [output
    (let [seed (:seed (:state story-generator))
          story-gen ((generate-story-fn storyon-lib) story-generator)]
      ;(clojure.pprint/pprint story-gen)
      {:output (:output (:state story-gen))
       :generator (exit-state story-gen)
       :feedback nil})]
    output))

;;;
;;; Stories
;;;

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
   {:name "Scheherazade" :tags {:stories [] :description "a queen of Persia" :gender :female :can-tell-stories? true}} ])

(defn make-event [event-map]
  event-map)
  ;(merge event-map {:seed 7}));(hash (str event-map))}))

(defn make-storytelling-scene [story]
  (let [seed (:seed (:state story))
        char-list (get-in story [:state :characters])
        teller (nonogen.stories.characters/pick-storyteller char-list seed)]
    {:tags {:storyteller teller}}))


(defn make-story [{:keys [characters scenes events output depth nesting narrator]} storyon-lib]
  (gens/make-generator
     {:state {
              :seed (random/get-random-seed)
              :characters characters
              :scenes scenes
              :events events
              :output output
              :nesting nesting
              :narrator narrator
              :exit nil}
      :generator (fn [g] (story g storyon-lib))}))


(defn make-basic-story [storyon-lib]
  (add-event (add-scene (make-story {:characters (make-characters) :scenes [] :events [] :output []} storyon-lib)
                        {:tags {:storyteller "Scheherazade"}})
             {:tags {:event :story-introduction}}))

(defn make-storytelling-story [storyon-lib story]
  (let [seed (:seed (:state story))
        char-list (nonogen.stories.characters/make-character-list 3 seed)
        teller (nonogen.stories.characters/pick-storyteller char-list seed)
        nesting (inc (if (nil? (:nesting (:state story))) 0 (:nesting (:state story))))
        narrator (get-in story [:state :tags :storyteller])
        ]
  (make-story {:characters char-list
               :scenes [{:tags {:storyteller teller}}]
               :events [{:tags {:event :story-introduction}}]
               :output []
               :nesting nesting
               :narrator narrator
               }
              storyon-lib)))


(defn make-thousand-nights-story [storyon-lib]
   (make-story
    {:characters (nonogen.stories.characters/make-thousand-nights-character-list)
     :scenes [{:tags {:storyteller "Scheherazade" :reality-prime true}}]
     :events [{:tags {:event :story-introduction}}]
     :nesting 0
     :output []} storyon-lib))


(defn make-labyrinth-story [storyon-lib story]
  (let [seed (:seed (:state story))
        char-list (nonogen.stories.characters/make-character-list 1 seed)
        explorer (nonogen.stories.characters/pick-storyteller char-list seed)
        zip (nonogen.stories.labyrinth/enter-labyrinth story)
        nesting (inc (if (nil? (:nesting (:state story))) 0 (:nesting (:state story))))
        narrator (get-in story [:state :tags :storyteller])
        ]
  (assoc (add-tag (make-story {:characters char-list
                                    :scenes []
                                    :events [{:tags {:event :labyrinth-introduction}}]
                                    :output []
                               :nesting nesting
                               :narrator narrator
                               }
                                   storyon-lib)
                 {:explorer explorer :labyrinth true})
    :zip (:zip zip))))



;(defn make-make-story [storyon-lib]
;  {:make-story
;   (defn make-story
;   ;  ([]
;  ; (make-story {:characters [] :scenes [] :events [] :output []} (fn [g] (story g storyon-lib))))
;  ;([characters]
;  ; (make-story {:characters characters :scenes [] :events [] :output []} (fn [g] (story g storyon-lib))))
;  ([{:keys [characters scenes events output depth]} generator]
;   (gens/make-generator
;    {:state {
;             :seed (random/get-random-seed)
;             :characters characters
;             :scenes scenes
;             :events events
;             :output output
;             :exit nil             }
;     :generator generator})))
;  :make-basic-story
;  (defn make-basic-story []
;  (add-event (add-scene (make-story {:characters (make-characters) :scenes [] :events [] :output []} (fn [g] (story g storyon-lib)))
;                        {:tags {:storyteller "Scheherazade"}})
;             {:tags {:event :story-introduction}}))   }
;
;  )



;;;
;;; Sketching
;;;

;(:make-story (make-make-story []))

;(nth
;  (iterate gens/process
;  (gens/insert (gens/make-generator-stack)
;               (add-event
;                (add-scene ((:make-story (make-make-story [])) (make-characters)) {:tags {:storyteller "Scheherazade"}})
;                {:tags {:event :story-introduction}}
;               )))
;   6)
