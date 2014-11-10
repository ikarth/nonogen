(ns genmo2014.generators
   (:require [clojure.pprint]
             ))

(def example-generator
  {:state []
   :output "test"
   :feedback [0]
   :generator (fn [gen]
                {:output "output"
                 :generator example-generator
                 :feedback (:feedback gen)})

   })

(def example-generator-stack
  {:generator-stack [example-generator example-generator]
   :output []})

(defn process [{:keys [generator-stack output]}]
  (if (empty? generator-stack)
    {:generator-stack generator-stack :output output}
    (let [gen (peek generator-stack)
          outcome ((:generator gen) gen)
          feedback (into (:feedback (peek (pop generator-stack))) (:feedback outcome))
          oldstack (let [stack (pop generator-stack)]
                     (if (empty? stack)
                       stack
                       (conj (pop stack) (assoc (peek stack) :feedback feedback))))
          newstack {:generator-stack
                        (if (:generator outcome)
                         (conj oldstack (:generator outcome))
                         oldstack)
                  :output (conj output (:output outcome))}]
    newstack)))



(process example-generator-stack)

(process (process (process example-generator-stack)))


;(take-while
; #(not (nil? (:generator-stack %)))
; (iterate process example-generator-stack))

(nth (iterate process example-generator-stack) 15)


;(repeatedly)

;(reduce process example-generator-stack)
;(peek example-generator-stack)
