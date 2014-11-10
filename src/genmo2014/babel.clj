(ns genmo2014.babel
  (:require [clojure.pprint]
            )
  (:use [clojure.pprint]
        )
  (:gen-class))

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

(babel-page)
