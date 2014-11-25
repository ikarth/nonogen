(ns nonogen.stories.output
   (:require [clojure.pprint]
             [nonogen.random :as random]
             [nonogen.stories.predicates]
             ))


;; --- Parsing ---
;; Parsing needs to combine expressiveness with consision. It needs to take the vector
;; of mostly text data and process it. If it's text, send it back as just text. If it's
;; a function, call the function with the data from the current story state.

(defn She [story])
(defn she [story]
  "she")
(defn her [story])

(defn protagonist [story])
(defn protagonist-name [story])
(defn protagonist-description [story])
(defn storyteller-name [story]
  (:storyteller (nonogen.stories.predicates/get-story-tags story)))

(defn vary [story & text]
  (apply str (first (random/shuffle-randomly text (:seed (nonogen.stories.predicates/get-story-tags story))))))


(defn parse-one [story data]
  (if (string? data)
    data
    (if (ifn? data)
      (-> story data)
      data)
    ))

(defn parse [story output-data]
  (apply str
           (map
          (partial parse-one story)
          output-data)))


;(defmacro output [& output-data]
;  `(list ~@output-data))

(fn? (first '(+ 1 1)))

;;;
;;; Sketching
;;;


;; (output "text" (function-to-be-evaluted-later parameters))
;;        to
;; (output '["text" (function-to-be-evaluted-later parameters)])
;;        to
;; ["text" (function-to-be-evaluated-later story parameters)]
;;

;(defn output [& data]
;  `[~@data])

(defmacro output [& data]
  `[~@data])



(output "test" "test" she)
(parse example-story (output "test" "test" storyteller-name '(vary "one" "two")))
