(ns nonogen.stories.storyon
   (:require [clojure.pprint]
             [nonogen.generators :as gens]
             [nonogen.stories.predicates]
             [clojure.repl]
             ))

;;;
;;; Storyons
;;;

(defn make-storyon
  ([]
   {:name nil
    :predicates nil
    :result [[:output ""]]})
  ([predicates result]
   {:name nil
    :predicates predicates
    :result result})
  ([{:keys [predicates result]}]
   {:name nil
    :predicates predicates
    :result result}))

;;;
;;; Filtering and Selecting
;;;

;(defn filter-storyons [storyon-deck tags]
;  (filter
;   (fn [a-storyon]
;     (not (some false?
;                (map (fn [pred]
;            (pred tags))
;          (nonogen.stories.predicates/expand-predicates-default (:predicates a-storyon))))))
;   storyon-deck))


(defn filter-storyons [storyon-deck tags]
  (filter
   (fn [a-storyon]
     (not (some false?
                (map (fn [pred]
                       (if (not (nil? pred))
                         (pred tags)
                         false
                         ))
                     (nonogen.stories.predicates/expand-predicates-default
                      (:predicates a-storyon))))))
   storyon-deck))

(defn select-storyons [storyon-deck tags]
  (if (:singular-selection tags)
    [(first (filter #(not (nil? %)) storyon-deck))]; todo: implement proper weighted selection
    storyon-deck
    ))

