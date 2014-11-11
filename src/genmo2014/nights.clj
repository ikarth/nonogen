(ns genmo2014.nights
   (:require [clojure.pprint]
             [genmo2014.generators :as gens]
             [genmo2014.storyon]))

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
  (let [story-gen (genmo2014.storyon/generate-story story-generator)]
    (let [exit (:exit (:state story-gen))]
    (if exit
      {:output (:output-buffer (:state story-gen))
       :generator (exit-state story-gen)
       :feedback nil}
      ;story-gen;(recur story-gen)
      ))))


;; --- Process Event Queue ---
;; Called with the story-generator
;; Take the event at the top of the event queue (if there isn't one, send the exit effect or a special event that triggers the exit storyon)
;; Filter the storyon deck: from the active deck of storyons take the filtered set of valid storyons for this event+state
;; Select a storyon or storyons: build a vector of the chosen storyons
;; Map across the storyons, building a vector of their effects
;; Return the vector of effects

;; --- Process the Effects ---
;; Called with the argument of the story-generator
;; Take the vector of effects
;; Go through the vector of effects, applying their effects in order.
;; Return the result of the changes

;; --- Effects ---
;; - Pop Event Queue / Don't Pop Event Queue (determines if the event immidiately repeats)
;; - Replace Event (put the popped event back in the queue, to eventually run again)
;; - Insert Event (Stick an event at the bottom of the queue)
;; - Priority Event (Stick an event at the top of the queue, for an immidiate reaction)
;; - Exit: Inwards (At the generator level, return with :generator set to [this-current-state new-subgenerator]
;; - Exit: Outwards (At the generator level, return with an empty :generator result)
;; - Exit: In-Place (At the generator level, return with :generator set to the current state of this generator)
;; - Exit: Forwards (At the generator level, return with generator set to a completely different generator...which has the side effect of dropping this one )
;; - Output (Add information to the output queue)
;; - Feedback (Add information to the feedback vector)
;; - Alter state (Most common effect, with many different varities.)
;; Effects can also call other effects.

;; --- Get Tags ---
;; Go through all the structures in the story-state
;;   Get their :tags
;;   Merge them into one unified map
;;   Return that

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

(def example-story (assoc (make-story)
  :state {:characters [{:name "Scheherazade" :tags {:stories [] :gender :female}} {:name "Shahryar" :tags {:gender :male}}]
          :scenes [{:current-character "Scheherazade" :scene :storytelling :storyteller "Scheherazade"}]
          }))
example-story
(def gen-stack (genmo2014.generators/make-generator-stack))
(gens/insert gen-stack example-story)
(gens/process (gens/insert gen-stack example-story))
(nth (iterate gens/process (gens/insert gen-stack example-story)) 15)
;(clojure.pprint/pprint (nth (iterate gens/process (gens/insert gen-stack example-story)) 15))

