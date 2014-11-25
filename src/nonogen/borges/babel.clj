(ns nonogen.borges.babel
  (:require [nonogen.generators :as gens]))

(defn generate-character []
  #(rand-nth "ABCDEFGHIJKLMNOPQRSTUVWXYZ,. "))

(defn babel-page []
  (str
    (clojure.string/join
      (take 40 (repeatedly (fn []
                             (str (clojure.string/join
                                    (take 80
                                          (repeatedly (generate-character))))
                                  \newline)))))))

(defn create-babel-text []
  (take 410 (repeat babel-page)))

;(create-babel-text)

;(babel-page)

(defn babel-gen [babel-generator]
  {:output (babel-page)
   :generator nil
   :feedback nil})

(defn make-babel []
  (gens/make-generator
   {:state {}
    :generator babel-gen}))


 (nth
  (iterate gens/process
(gens/insert   (gens/insert (gens/make-generator-stack)
               (make-babel)
                              ) (make-babel)))
   6)
