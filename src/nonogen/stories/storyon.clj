(ns nonogen.stories.storyon
   (:require [clojure.pprint]
             [nonogen.generators :as gens]
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
  storyon-deck) ;todo

(defn select-storyons [storyon-deck tags]
  storyon-deck) ;todo

;(defn select-action
;  "Given a map of tags (distilled from the current state) and a vector of actions,
;  finds the actions which are valid for the current event and state and picks one."
;  [state-tags actions]
;  (first ; TODO: add weighted selection
;   (filter
;      (fn [an-action]
;      (not (some false?
;                 (map (fn [predicate]
;                        (let [state state-tags
;                              tag ((key predicate) state)]
;                          (if tag
;                            ((val predicate) tag)
;                            false)
;                          ))
;                      (:predicates an-action)))))
;    actions)))






;;;
;;; Sketching
;;;

;(defn make-story []
;  (gens/make-generator
;   {:state {:characters []
;            :scenes []
;            :events []
;            :output []
;            }
;    :generator nil}))

;(def example-story
;  (merge-with
;   #(merge %1 %2)
;   (make-story)
;   {:state {:characters [{:name "Scheherazade" :tags {:stories [] :gender :female}} {:name "Shahryar" :tags {:gender :male}}]
;          :scenes [{:current-character "Scheherazade" :scene :storytelling :storyteller "Scheherazade"}]
;          }}))

;example-story
;(println "-----------------------------")
;(println "-----------------------------")
;(println "-----------------------------")
;(clojure.pprint/pprint "Running test...")
;(generate-story example-story)


;(def example-predicate-list [:current-character-is-storyteller :test [:vector "test"]])
;(expand-predicates example-predicate-list predicate-conversions)


;((:output (story-effects example-story)) ["Test"])

;(def example-story  {:state {:characters []
;            :scenes []
;            :events []
;            :output []
;            }
;    })

;((:output (story-effects example-story)) "test")
;(map #(((key %1) (story-effects example-story)) (val %1)) {:output "test"})

;(call-s example-story [[:output "test"] [:output "test2"]])

(defn test-effect [state]
  {:a (fn [x] (println "A") (+ x state))
   :b (fn [x] (println "B" )(- x state))})

(defn call-test
  []
  (reduce
   (fn [story effect-vec]
     (let [effect-fn (first effect-vec)]
       (apply (if (keyword? effect-fn)
                (get (test-effect story) effect-fn)
                effect-fn)
              (rest effect-vec))))
   9
   [[:b 7][:a 5]]))

;(call-test)
