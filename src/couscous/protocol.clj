(ns couscous.protocol
  (:require [clojure.string :as str]
            [swiss.arrows :refer [-<>]]))

(defprotocol NamingConvention
  (hyphenate [this] this)
  (upperscore [this] this)
  (underscore [this] this)
  (abbreviate [this] this)
  (two-triple [this] this)
  (columnify [this that] this that)
  (columnifies [this] this)
  )

(extend-protocol NamingConvention

  nil
  (hyphenate [_] nil)
  (underscore [_] nil)
  (upperscore [_] nil)
  (abbreviate [_] nil)
  (columnify [_] nil)
  (columnify [_ __] nil)

  ;; Most other types will piggie-back ride the string implementations since it's most convenient.
  java.lang.String
  (hyphenate [s] (keyword (str/lower-case (str/replace s "_" "-"))))
  (underscore [s] (str/lower-case (str/replace s "-" "_")))
  (upperscore [s] (str/upper-case (str/replace s "-" "_")))
  (abbreviate [s] (-<> s str (str/split <> #"[-|_]") (map first <>) str/join keyword))
  (columnify [st o] (str (underscore st) "." (underscore o)))

  ;; Thanks to earlier defined strings, this is a lot cleaner (some might argue it doesn't
  ;; warrant a protocol as to so generic its implementation is, ordinary functions may suffice)
  clojure.lang.Keyword
  (hyphenate [k] (hyphenate (name k)))
  (underscore [k] (underscore (name k)))
  (upperscore [k] (upperscore (name k)))
  (abbreviate [k] (abbreviate (name k)))
  (columnify [kt o] (columnify (name kt) o))

  clojure.lang.Symbol
  (hyphenate [y] (hyphenate (str y)))
  (underscore [y] (underscore (str y)))
  (upperscore [y] (upperscore (str y)))
  (abbreviate [y] (abbreviate (str y)))
  (columnify [y1 o] (columnify (str y1) o))


  ;; However with protocols its easier to add a lot of types and just always have it work,
  ;; plus it makes for a lot cleaner function implementations too.
  clojure.lang.PersistentVector
  (hyphenate [i] (mapv hyphenate i))
  (upperscore [i] (mapv upperscore i))
  (abbreviate [i] (mapv abbreviate i))
  (two-triple [[a b]] (hash-map (keyword (first (str/split a #"/")))
                                [(second (str/split a #"/"))
                                 (second (str/split b #"/"))]))

  clojure.lang.PersistentHashMap
  (columnifies [m] (when (= (:column-key m) "PRI")
                     (str/join "." ((juxt :table-name :column-name) m))))


  )
