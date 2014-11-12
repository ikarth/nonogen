(ns genmo2014.nights-test
  (:require [clojure.test :refer :all]
            [genmo2014.nights :refer :all]
            [genmo2014.generators :as gens]))

(deftest a-test
  (testing "1 = 1"
    (is (= 1 1))))

(def example-story (assoc (make-story)
  :state {:characters [{:name "Scheherazade" :tags {:stories [] :gender :female}} {:name "Shahryar" :tags {:gender :male}}]
          :scenes [{:current-character "Scheherazade" :scene :storytelling :storyteller "Scheherazade"}]
          :output []
          }))

example-story

(deftest test-example-story
  (testing "Example story construction"
    (is (= (:state example-story)
        {:characters
         [{:name "Scheherazade",
           :tags {:stories [], :gender :female}}
          {:name "Shahryar",
           :tags {:gender :male}}],
         :scenes [{:current-character "Scheherazade", :scene :storytelling, :storyteller "Scheherazade"}],
         :output []}))
    (is (= ((get example-story :generator) example-story)
           {:output ["So she said, \"It is related, O august king, that...\" " "Then she ended, saying, \"But there is another tale which is more marvelous still.\"\n" "And she told them a story. " "And then debug text was printed. "], :generator {}, :feedback nil}
         ))
    (is (= (:state (genmo2014.storyon/generate-story example-story))
         {:characters [{:name "Scheherazade", :tags {:stories [], :gender :female}} {:name "Shahryar", :tags {:gender :male}}], :scenes [{:current-character "Scheherazade", :scene :storytelling, :storyteller "Scheherazade"}], :output ["So she said, \"It is related, O august king, that...\" " "Then she ended, saying, \"But there is another tale which is more marvelous still.\"\n" "And she told them a story. " "And then debug text was printed. "]}
                 ))
    (is (= (nth (iterate gens/process (gens/insert (genmo2014.generators/make-generator-stack) example-story)) 15)
         {:generator-stack [], :output ["So she said, \"It is related, O august king, that...\" " "Then she ended, saying, \"But there is another tale which is more marvelous still.\"\n" "And she told them a story. " "And then debug text was printed. "]}
         ))
    ))


((get example-story :generator) example-story)
(genmo2014.storyon/generate-story example-story)
(def gen-stack (genmo2014.generators/make-generator-stack))
(gens/insert (genmo2014.generators/make-generator-stack) example-story)
(gens/process (gens/insert (genmo2014.generators/make-generator-stack) example-story))
(gens/process (gens/insert (gens/insert (genmo2014.generators/make-generator-stack) example-story) example-story))
(gens/process (gens/process (gens/insert (gens/insert (genmo2014.generators/make-generator-stack) example-story) example-story)))

(nth (iterate gens/process (gens/insert (genmo2014.generators/make-generator-stack) example-story)) 15)




(run-tests)




;(def example-story (assoc (make-story)
;  :state {:characters [{:name "Scheherazade" :tags {:stories [] :gender :female}} {:name "Shahryar" :tags {:gender :male}}]
;;          :scenes [{:current-character "Scheherazade" :scene :storytelling :storyteller "Scheherazade"}]
;          :output []
;          }))
;


