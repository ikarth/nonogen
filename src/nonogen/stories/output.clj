(ns nonogen.stories.output
   (:require [clojure.pprint]
             [nonogen.random :as random]
             [nonogen.stories.nights]
             ))


;; --- Parsing ---
;; Parsing needs to combine expressiveness with consision. It needs to take the vector
;; of mostly text data and process it. If it's text, send it back as just text. If it's
;; a function, call the function with the data from the current story state.

(defn She [story])
(defn she [story])
(defn her [story])

(defn protagonist [story])
(defn protagonist-name [story])
(defn protagonist-description [story])
(defn storyteller-name [])



(defn parse [story & output-data]
  (str output-data)
  )


(parse "Test data ")
