(ns nonogen.stories.output
   (:require [clojure.pprint]
             [nonogen.random :as random]
             [nonogen.stories.predicates]
             [clojure.string]
             ))


;; --- Parsing ---
;; Parsing needs to combine expressiveness with consision. It needs to take the vector
;; of mostly text data and process it. If it's text, send it back as just text. If it's
;; a function, call the function with the data from the current story state.

(defn get-tags [story]
  (nonogen.stories.predicates/get-story-tags story))

(defmacro wrap-get-tags [expr]
  `(fn [story#]
     (let [tags# (nonogen.stories.predicates/get-story-tags story#)]
       (get tags# ~expr))))

(defn storyteller-name []
  (fn [story]
    (:storyteller (get-tags story))
    ))

;(defn storyteller-name [] (wrap-get-tags :storyteller))

(defn gender-pronoun [gender]
  (gender {:male "he" :female "she"}))

(defn She []
  (fn [story]
    (let [protagonist (:current-character (get-tags story))]
      (:name protagonist))))

(defn she []
  (fn [story]
    (let [protagonist (:current-character (get-tags story))]
      ;protagonist
      ((:gender (:tags protagonist)) {:male "he" :female "she"})
      )))

(defn her []
  (fn [story]
    (let [protagonist (:current-character (get-tags story))]
      ;(str (:name protagonist) "'s")
      ((:gender (:tags protagonist)) {:male "his" :female "her"})
      )))

(defn current-character-name []
  (fn [story]
    (let [protagonist (:current-character (get-tags story))]
      (:name protagonist))))

(defn a-current-character-description []
  (fn [story]
    (let [protagonist (:current-character (get-tags story))]
      (:description (:tags protagonist)))))

(defn vary [& text]
  (fn [story]
    (apply str (first (random/shuffle-randomly
                       text (:seed (nonogen.stories.predicates/get-story-tags
                                    story)))))))



(defn describe-all-characters []
  (fn [story]
    (let [char-desc (map
     (fn [c] (apply str (:description (:tags c)) " named " (:name c)))
       (:characters (:state story)))]
      (if (< (count char-desc) 2)
        (apply str "there was " (first char-desc))
        (apply str "there was " (clojure.string/join ", " (butlast char-desc)) " and " (last char-desc))
      ))))


;((vary "test" "two" "three")
; {:state {:seed -1, :characters [{:name "Shahryar", :tags {:gender :male}} {:name "Scheherazade", :tags {:stories [], :gender :female, :can-tell-stories? true}}], :scenes [{:tags {:storyteller "Scheherazade"}}], :events [{:tags {:event :story-introduction}}], :output [], :exit nil}, :generator nil}
; )

;((vary "6" "9")
; {:state {:seed nil, :characters [{:name "Shahryar", :tags {:gender :male}} {:name "Scheherazade", :tags {:stories [], :gender :female, :can-tell-stories? true}}], :scenes [{:tags {:storyteller "Scheherazade"}}], :events [{:tags {:event :story-introduction}}], :output [], :exit nil}, :generator nil}
; )

;(defmacro output [& data]
;  `[~@data])

(defn parse-one [story data]
  (if (string? data)
    data
    (if (ifn? data)
        ((data) story)
      data)
    ))

(defn parse [story output-data]
  (apply str
         (map
          (partial parse-one story)
          output-data)))



;;;
;;; Sketching
;;;

(def test-story {:state {:scenes [{:tags {:storyteller "Test"}}]}})


(parse test-story ["test" "one" "two" storyteller-name])

(parse test-story ["one" "two"])












;; (output "text" (function-to-be-evaluted-later parameters))
;;        to
;; (output '["text" (function-to-be-evaluted-later parameters)])
;;        to
;; ["text" (function-to-be-evaluated-later story parameters)]
;;

;(defn output [& data]
;  `[~@data])



(defn foo [arg1]
  (fn [state] (+ state arg1)))

((foo (+ 9 6)) 9)


;(output "test" "test" she)
;(parse example-story (output "test" "test" storyteller-name '(vary "one" "two")))
