(ns nonogen.stories.storyon
   (:require [clojure.pprint]
             [clojure.inspector]
             [nonogen.generators :as gens]
             [nonogen.stories.predicates]
             [clojure.repl]
             (bigml.sampling [simple :as simple]
                             [reservoir :as reservoir]
                             [stream :as stream])
             ))

;;;
;;; Storyons
;;;

(defn make-storyon
  ([]
   (make-storyon {:predicates nil
                  :result [[:output ""]]}))
  ([predicates result]
   (make-storyon {:predicates predicates
                  :result result}))
  ([{:keys [predicates result]}]
   {;:uuid nil;((print-str predicates) (print-str result))
    :name nil
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
    (let [valid-storyons (filter #(not (nil? %)) storyon-deck)]
      ;(clojure.inspector/inspect-tree (take 1 (simple/sample valid-storyons)))
      (print (get tags :seed))
      (into [] (take 1 (simple/sample valid-storyons)))
      )
    storyon-deck
    ))

(take 1 (simple/sample [1 2 3 4 5] :seed 9999))
