(ns nonogen.stories.nights-test
  (:require [clojure.test :refer :all]
            [nonogen.stories.nights :refer :all]
            [nonogen.generators :as gens]))

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
    (is (= (:state (nonogen.stories.nights/generate-story example-story))
         {:characters [{:name "Scheherazade", :tags {:stories [], :gender :female}} {:name "Shahryar", :tags {:gender :male}}], :scenes [{:current-character "Scheherazade", :scene :storytelling, :storyteller "Scheherazade"}], :output ["So she said, \"It is related, O august king, that...\" " "Then she ended, saying, \"But there is another tale which is more marvelous still.\"\n" "And she told them a story. " "And then debug text was printed. "]}
                 ))
    (is (= (nth (iterate gens/process (gens/insert (nonogen.generators/make-generator-stack) example-story)) 15)
         {:generator-stack [], :output ["So she said, \"It is related, O august king, that...\" " "Then she ended, saying, \"But there is another tale which is more marvelous still.\"\n" "And she told them a story. " "And then debug text was printed. "]}
         ))
    ))


((get example-story :generator) example-story)
(nonogen.stories.nights/generate-story example-story)
(def gen-stack (nonogen.generators/make-generator-stack))
(gens/insert (nonogen.generators/make-generator-stack) example-story)
(gens/process (gens/insert (nonogen.generators/make-generator-stack) example-story))
(gens/process (gens/insert (gens/insert (nonogen.generators/make-generator-stack) example-story) example-story))
(gens/process (gens/process (gens/insert (gens/insert (nonogen.generators/make-generator-stack) example-story) example-story)))

(nth (iterate gens/process (gens/insert (nonogen.generators/make-generator-stack) example-story)) 15)




(run-tests)




;(def example-story (assoc (make-story)
;  :state {:characters [{:name "Scheherazade" :tags {:stories [] :gender :female}} {:name "Shahryar" :tags {:gender :male}}]
;;          :scenes [{:current-character "Scheherazade" :scene :storytelling :storyteller "Scheherazade"}]
;          :output []
;          }))
;



