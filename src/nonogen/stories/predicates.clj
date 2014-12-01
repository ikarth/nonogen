(ns nonogen.stories.predicates
   (:require [clojure.pprint]
                          [clojure.inspector]

             ))

;;;
;;; Story Query
;;;

(defn get-current-event [story]
  (peek (get-in story [:state :events])))

;(defn get-current-character [story character]
;  ;(peek (get-in story [:state :characters]))) ; todo: pick current character by event


(defn get-story-tags [story]
  (let [state (get story :state)
        scene (peek (get state :scenes))
        event (get-current-event story) ;todo: add support for event tags
        current-character (:current-character (:state story))
        ;event-tags ((:get-tags (peek (get state :events))))
        ]
    (merge
     {:seed (get state :seed)} ; todo: calculate this from the current story-state
     {:current-character current-character}
     (:tags state)
     (:tags scene)
     (:tags event)
     (:tags current-character)
     (merge {:qualities (get-in story [:state :qualities])} {:qualities (get-in story [:state :qualities])})
     )))


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
          (do ;(println func)
           (if (ifn? func)
            (fn [r] (func (first (rest p)) r))
            (fn [_] (str "vector malformed: " f )))))

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

(defn nil-to-zero [n]
  (if (nil? n) 0 n))


(def predicate-conversions
  {:current-character-is-storyteller #(= (:storyteller %) (:name (:current-character %)))
   :current-character-is-explorer #(= (:explorer %) (:name (:current-character %)))
   :test :current-character-is-storyteller
   :nights-quality-at-least-1001 (fn [tags]
                                   ;(println tags)
                                   (> (nil-to-zero (:nights (:qualities tags))) 8))
   ;:storytelling-beginning #(contains? % :storytelling-beginning)
   ;:storytelling-ending #(contains? % :storytelling-ending)
   ;:storytelling-ready-to-tell #(contains? % :storytelling-ready-to-tell)
   :event (fn [tag-value-to-look-for tags]
            (= (get tags :event) tag-value-to-look-for))
   :not-current-character-is-storyteller (fn [_] false)
   :at-least-one-character (fn [_] true)
   :not-tag (defn not-tag [tag-to-look-for tags]
              ;(println ":is-tag " tag-to-look-for "+" tags "=" (nil? (get tags tag-to-look-for)))
              (nil? (get tags tag-to-look-for)))
   :is-tag (defn is-tag [tag-to-look-for tags]
             ;(println ":is-tag " tag-to-look-for "+" tags "=" (not (nil? (get tags tag-to-look-for))))
              (not (nil? (get tags tag-to-look-for))))
   })

(defn expand-predicates-default [predicates]
  (let [expand (expand-predicates predicates predicate-conversions)]
    expand))



;;;
;;; Sketching
;;;

