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

(defn filter-storyons [storyon-deck tags]
  (filter
   (fn [a-storyon]
     (not (some false?
                (map (fn [pred]
                       (if (ifn? pred)
                         (pred tags)
                         false))
                     (let [funct (nonogen.stories.predicates/expand-predicates-default (:predicates a-storyon))]

                       funct
                      )))))
   storyon-deck))

(defn select-storyons [storyon-deck tags]
  (if (:singular-selection tags)
    [(first (filter #(not (nil? %)) storyon-deck))]; todo: implement proper weighted selection
    storyon-deck
    ))

