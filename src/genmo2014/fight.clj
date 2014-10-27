(ns genmo2014.fight
   (:require (bigml.sampling [simple :as sample-simple]
                             [reservoir :as sample-reservoir]
                             [stream :as sample-stream])
             [clojure.pprint]
             ))

;prereq
;outcome
;description

;;; A fight consists of two characters, a set of tags, and an advantage score
;;; A fight action is a description, a list of prereqs, and an outcome

;Rule
;Criterion
;Response
;Writeback
;Followup


;; Given a weighted set of things, pick one at random

(defn weighted-selection [m]

  )

(def test-fight-action
  {:criterion #{} ; set of predicates
   :text "Nothing happened."
   :outcome nil})


(def test-actions
  (list
    {:criterion #{:range #(< 1 %)}
     :text "I stab you with my knife!"
     :outcome nil}
    {:criterion #{:range #(< 4 % 1)}
     :text "I jab at you with my spear!"
     :outcome nil}
    {:criterion #{:range #(> % 1)}
     :text "I run towards you!"
     :outcome {:distance :close}}
    {:criterion #{:range #(< % 9)}
     :text "I run away from you!"
     :outcome {:distance :far}}
   ))



(def fighter-one
  {:name "Alice"
   :tags {}
   })

(def fighter-two
  {:name "Bob"
   :tags {}
   })

(def present-scene
  {:tags {:range 0}
   :actors [fighter-one fighter-two]
   }
  )


(defn process-range [distance]
  (cond
   (< distance 1) :hand
   (< distance 2) :close
   (< distance 4) :reach
   (< distance 7) :near
   :else :far))

(def process-functions {:range process-range})

(clojure.pprint/pprint (test-fight-action :text))

(defn filter-by-criterion [predicates data]
  (filter
   #(((key %) predicates) (val %))
   data))

(filter-by-criterion (:criterion (first test-actions)) (:tags present-scene))

;; Go through the map of criterion, testing if they match the known info
;; Get a set of predicates, evaluate each to see if true...
(defn filter-criterion [criterion world]
  (clojure.set/superset? world criterion)
  )

(defn filter-actions "Returns the subset of actions that are currently possible."
  [action-list]
  (filter #(filter-criterion (:criterion %1) #{:far}) action-list)
  )



;; Select an action with weighted random sampling
(defn select-actions [action-list]
  action-list)

(defn run-actions [action-list]
  (let [alist (filter-actions action-list)
        text (map #(:text %1) alist)
        outcome (map #(:outcome %1) alist)]
    {:text text :outcome outcome}))


(run-actions test-actions)




;(filter
;; #(filter-criterion
;;   (:criterion %1) #{:far})
; test-actions)


(clojure.set/difference #{1 2 3} #{3 4 5})

(defn testfn [{:keys [x y z]}]
  (map eval [x y z]))

(reduce + (testfn {:x '(+ 1 5) :b 2 :y 3 :z 4}))

((fn [x] (#(:range %) x)) {:range 3 :color :blue})

(def t-fns {:range #(> % 2) :color #(= :blue %)})
(def t-data {:range 3 :color :blue})
(((key (first t-data)) t-fns) ((key (first t-data)) t-data))
((key (first t-fns)) t-fns)
(filter
 #(((key %) t-fns) (val %))
 t-data)
(map
  #(((key %) t-fns) (val %))
 t-data)

 ( #(:range %2) t-fns {:range 1})





;; Event
;;

