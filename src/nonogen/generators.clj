(ns nonogen.generators
   (:require [clojure.pprint]
             ))

(defn make-generator
  [{:keys [state generator]}]
  {:state (if (nil? state) [] state)
   :generator generator
   })


;; A generator function returns an output map:
;; {:output - text and so on to go in the output queue
;;  :feedback - optional information to be passed up the stack
;;  :generators - a generator or vector of generators to get put on top of the stack (often the current generator, after it has been processed)
;; }


;; Process the generator, translating its return value to the changes to the
;; generator stack.
;; - If the stack is empty, we're done and can stop processing.
;; - If the stack isn't empty, grab the top item off the stack. This is what
;;     we're going to process.
;; - Run its generator to get the output.
;;     - Possible results:
;;       - OUTWARDS: This generator is finished, remove it from the stack. (Generator returns nil)
;;       - IN-PLACE: This generator isn't finished, run it again. (Generator returns itself)
;;       - INWARDS: This generator has created a subgenerator, put that
;;                  on top of the stack. (Generator returns new generator + itself)
;;       - FORWARDS: Replace this generator with another generator. (Generator returns just a new generator)
;; - If it had feedback, send that feedback upstream.
;; - Create the new stack:
;;   - Outcome is the generator returned by running the generator.

(defn into-conj [collection addition]
  (if (empty? addition)
    collection
    (if (vector? addition)
      (into collection addition)
      (conj collection addition))))

(defn process
  "Takes a generator stack and processes the top generator."
  [{:keys [generator-stack output]}]
  (if (empty? generator-stack)
    {:generator-stack generator-stack :output output} ; If the generator is empty, we're done here.
    (let [gen (peek generator-stack) ; grab the top of the stack
          oldstack (pop generator-stack)
          gen-result ((:generator gen) gen)  ; run the generator function, which returns an outcome {:output "Text for the output queue" :feedback "messages to go up the stack" :generator}
          new-output (into-conj output (:output gen-result)) ; put output in queue
          after-feedback (if (or (empty? (:feedback gen-result)) (empty? oldstack))
                           oldstack
                           (conj (pop oldstack) (assoc (peek oldstack) :feedback (:feedback gen-result))))
          gen-stack (if (empty? (:generator gen-result))
                      after-feedback
                      (into-conj after-feedback (:generator gen-result)))]
      ;(clojure.pprint/pprint "------------Process-----------")
      ;(print ".")
      {:generator-stack gen-stack :output new-output})))

(defn insert [generator-stack addition]
  (assoc generator-stack :generator-stack
    (into-conj (get generator-stack :generator-stack) addition)))

(defn make-generator-stack []
  {:seed nil
   :generator-stack []
   :output []})




;;;
;;; Sketching & Testing
;;;

;(make-generator-manager example-generator-stack)
;((:process-gen (make-generator-manager example-generator-stack)))





;(process-generator (make-generator-manager example-generator-stack))
;(process (make-generator-manager example-generator-stack))

;;;
;;; Generator Manager
;;;

;; I'm still not sure if doing it this way helps at all...
(defn make-generator-manager
  ([]
   (make-generator-manager
       {:generator-stack []
        :output []}))
  ([{:keys [generator-stack output]}]
   {:generator-stack generator-stack
    :output output
    :process-gen (fn [] (make-generator-manager (process {:generator-stack generator-stack, :output output})))
    :insert (fn [a] (make-generator-manager (insert {:generator-stack generator-stack, :output output} a)))
   }))

;(defn process-generator [gen]
;  ((:process-gen (make-generator-manager example-generator-stack))))
