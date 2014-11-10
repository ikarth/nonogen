(ns genmo2014.generators
   (:require [clojure.pprint]
             ))


(def example-generator
  {:state [0]
   :output "test"
   :feedback []
   :generator (fn [gen]
                {:output (:state gen)
                 :generator (merge example-generator {:state (into [] (map inc (:state gen)))})
                 :feedback []})})

(defn make-generator
  [{:keys [state output feedback generator]}]
  {:state (if (nil? state) [] state)
   :output (:generator generator)
   :feedback (if (nil? feedback) [] feedback)
   :generator generator
   })



example-generator

(def example-generator-2
  (make-generator
 {:state [0]
  :generator (fn [gen]
                {:output (:state gen);(:generator gen)
                 :generator (merge gen {:state (into [] (map inc (:state gen)))})
                 :feedback []})}))

example-generator-2
((:generator example-generator-2) example-generator-2)



(def example-generator-stack
  {:generator-stack [example-generator-2 example-generator-2]
   :output []})


(defn generator-manager
  [{:keys [generator-stack output]}]
  {:generator-stack generator-stack
   :output output
   :process-gen
   (fn []
     (let [basic-gen (generator-manager {:generator-stack generator-stack :output output})]
     (if (empty? generator-stack)
       basic-gen
       (let [outcome ((:generator (peek generator-stack)) (peek generator-stack))
             feedback (into (:feedback (peek (pop generator-stack))) (:feedback outcome))
             oldstack (let [stack (pop generator-stack)]
                        (if (empty? stack)
                          stack
                          (conj (pop stack) (assoc (peek stack)) :feedback feedback)))
             newstack (generator-manager {:generator-stack (if (:generator outcome)
                         (conj oldstack (:generator outcome))
                         oldstack) :outcome (conj output (:output outcome))})

             ]
         newstack
         ))))})


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


(generator-manager {:generator-stack [example-generator] :output []})
((:process-gen
  (generator-manager
   {:generator-stack [example-generator]
    :output []})))



;(repeatedly)

;(reduce process example-generator-stack)
;(peek example-generator-stack)
