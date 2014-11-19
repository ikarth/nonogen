(ns nonogen.stories.predicates
   (:require [clojure.pprint]
             ))

;;;
;;; Story Query
;;;

(defn get-current-event [story]
  (peek (get-in story [:state :events])))

(defn get-current-character [story]
  (peek (get-in story [:state :characters]))) ; todo: pick current character by event


(defn get-story-tags [story]
  (let [state (get story :state)
        scene (peek (get state :scenes))
        event (get-current-event story) ;todo: add support for event tags
        current-character (get-current-character story)
        ;event-tags ((:get-tags (peek (get state :events))))
        ]
    (merge
     {:current-character (:name current-character)}
     (:tags state)
     (:tags scene)
     (:tags event)
     (:tags current-character))))


;; --- Get Tags ---
;; Go through all the structures in the story-state
;;   Get their :tags
;;   Merge them into one unified map
;;   Return that

;;;
;;; Predicates
;;;


(defn expand-predicates
  "Takes a vector of predicates and uses the predicate conversion map to convert them to functions."
  [predicates keyword-conversions]
  (map
   (fn [pred]
     (loop [p pred]
       (if (fn? p)
         p
         (if (or (vector? p) (not (keyword? p)))
           p
           (recur (p keyword-conversions))))))
   predicates))

;;;
;;; The actual predicates we're using
;;;

(def predicate-conversions
  {:current-character-is-storyteller #(= (:storyteller %) (:current-character %)) ;todo: add scenes + :storyteller tag
   :test :current-character-is-storyteller
   :storytelling-beginning #(contains? % :storytelling-beginning)
   :storytelling-ending #(contains? % :storytelling-ending)
   :storytelling-ready-to-tell #(contains? % :storytelling-ready-to-tell)
   })

(defn expand-predicates-default [predicates]
  (expand-predicates predicates predicate-conversions))

