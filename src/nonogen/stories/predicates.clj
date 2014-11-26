(ns nonogen.stories.predicates
   (:require [clojure.pprint]
                          [clojure.inspector]

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
    ;(print (str (:seed state) (:seed event)))
    (merge
     {:seed (get state :seed)} ; todo: calculate this from the current story-state
     {:current-character current-character}
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

(defn expand-one-predicate [pred conversions]
     (loop [p pred]
       (cond
        (vector? p)
        (let [f (get conversions (first p))
              func (expand-one-predicate f conversions)]
          (if (ifn? func)
            (fn [r] (func (first (rest p)) r))
            (fn [_] (str "vector malformed: " f ))))

        (keyword? p) (recur (p conversions))
        (ifn? p) p
        :else (fn [_] false)
        )))

(defn expand-predicates [predicates conversions]
  (map
   #(expand-one-predicate % conversions)
   predicates))

;;;
;;; The actual predicates we're using
;;;

(def predicate-conversions
  {:current-character-is-storyteller #(= (:storyteller %) (:name (:current-character %))) ;todo: add scenes + :storyteller tag
   :test :current-character-is-storyteller
   ;:storytelling-beginning #(contains? % :storytelling-beginning)
   ;:storytelling-ending #(contains? % :storytelling-ending)
   ;:storytelling-ready-to-tell #(contains? % :storytelling-ready-to-tell)
   :event (fn [tag-value-to-look-for tags]
            (= (get tags :event) tag-value-to-look-for))
   :not-current-character-is-storyteller (fn [_] false)
   :at-least-one-character (fn [_] true)
   :not-tag (defn not-tag [tag-to-look-for tags]
              (not (get tags tag-to-look-for)))
   :is-tag (defn is-tag [tag-to-look-for tags]
              (get tags tag-to-look-for))
   })

(defn expand-predicates-default [predicates]
  (let [expand (expand-predicates predicates predicate-conversions)]
    expand))


;;;
;;; Sketching
;;;

