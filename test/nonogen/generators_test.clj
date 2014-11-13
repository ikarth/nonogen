(ns nonogen.generators-test
  (:require [clojure.test :refer :all]
            [nonogen.generators :refer :all]))

(def example-generator-2
  (make-generator
 {:state [0]
  :generator (fn [gen]
                {:output (:state gen);(:generator gen)
                 :generator (merge gen {:state (into [] (map inc (:state gen)))})
                 :feedback nil})}))

(def example-generator-3
  (make-generator
 {:state [0]
  :generator (fn [gen]
                {:output "Generator Three"
                 :generator nil
                 :feedback []})}))

(def example-generator-stack
  {:generator-stack [example-generator-2]
   :output []})

(deftest test-example-generator
  (testing "Example generator construction"
    (is (=
         (:state (first (:generator-stack (process example-generator-stack))))
         [1]))
    (is (=
         (:output (nth (iterate process example-generator-stack) 15))
         [0 1 2 3 4 5 6 7 8 9 10 11 12 13 14]))
    (is (=
     (take 15 (take-while
               #(not (nil? %))
               (iterate #(if (< % 10) (inc %) nil) 1)))
     '(1 2 3 4 5 6 7 8 9 10)))

    ))

;(process (process (process example-generator-stack)))

(run-tests)
