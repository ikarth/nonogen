(ns genmo2014.clean)


;;; Useful functions for cleaning an input text

(def ^:dynamic *windows-linebreaks* true)

(defn strip-italics
  "Removes _underscored italics_ from the text. It'd be nice to find a way to include these, later."
  [text]
  (clojure.string/replace text "_" ""))

(defn truncate-whitespace
  "Removes excess spaces from the text, regularizing lines. Not what you want when you're trying to preserve the format of poetry, but definitely what you want when you're trying to parse the words."
   [text]
  (clojure.string/replace text #"\s+" " "))

(defn strip-linebreaks-windows
  "Returns string with linebreaks stripped out."
   [text]
   (clojure.string/replace
     (clojure.string/replace text #"[\r\n]+" "_@_")
     "_@_" " "))

(defn strip-linebreaks-unix
  "Returns string with linebreaks stripped out."
   [text]
   (clojure.string/replace
     (clojure.string/replace text #"[\n]+" "_@_")
     "_@_" " "))

(defn strip-linebreaks
  "Returns string with linebreaks stripped out."
   [text]
  (if *windows-linebreaks*
    (strip-linebreaks-windows text)
    (strip-linebreaks-unix text)))

(defn mark-paragraphs-windows
  "Find the linebreaks and mark their position for later splitting. May need updating for non-Windows files."
   [source-text]
  (clojure.string/replace source-text #"\r\n\r\n" "¶"))

(defn mark-paragraphs-unix
  "Find the linebreaks and mark their position for later splitting. May need updating for non-Windows files."
   [source-text]
  (clojure.string/replace source-text #"\n\n" "¶"))

(defn mark-paragraphs
  "Find the linebreaks and mark their position for later splitting."
   [text]
  (if *windows-linebreaks*
    (mark-paragraphs-windows text)
    (mark-paragraphs-unix text)))

(defn break-on-pilcrow-windows
  "Find paragraph markers and reinsert the linebreaks."
   [source-text]
  (clojure.string/replace
      source-text
      #"¶" "\r\n\r\n"))

(defn break-on-pilcrow-unix
  "Find paragraph markers and reinsert the linebreaks."
   [source-text]
  (clojure.string/replace
      source-text
      #"¶" "\n\n"))

(defn break-on-pilcrow
  "Find paragraph markers and reinsert the linebreaks."
  [text]
  (if *windows-linebreaks*
    (break-on-pilcrow-windows text)
    (break-on-pilcrow-unix text)))

(defn append-space
  "Add a space to the end of the string"
  [source-text]
  (if-not (nil? (re-find #"¶" source-text))
    (str source-text " ")
    source-text))

(defn get-paragraphs
  "Returns source-texts broken into a list of paragraphs."
  [source-text]
  (clojure.string/split source-text #"¶"))
