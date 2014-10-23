(ns genmo2014.fight
   (:require (bigml.sampling [simple :as sample-simple]
                             [reservoir :as sample-reservoir]
                             [stream :as sample-stream])
                  (bigml.sampling.test [stream :as stream-test])))

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
