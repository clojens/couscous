(ns couscous.instance
  (:require (couscous [protocol :refer :all])
            (korma [db :refer [defdb mysql]]
                   [core :refer [exec-raw]])))

(defn config-map
  "Single unified database options map function with some (or not so) sane
  defaults. Swap in the plain text password for something else if needed.
  Optional use obviously, normal Korma means of literal map input still possible."
  [&{db-name :db-name db-user :db-user db-port :db-port db-pass :db-pass
     :or {db-name :information-schema db-user "root" db-pass "" db-port 3306}}]
  {:db (underscore db-name)
   :user db-user
   :password db-pass
   :port db-port
   :naming  {:keys #(hyphenate %)
             :fields #(upperscore %)}})

(defn initialize!
  "Side-effectful database instances wrapper sets variable. Hence initialized
  immediately below since we must ensure the variables to be properly initialized."
  []
  (do
    (defdb isdb (mysql (config-map)))
    (defdb wbdb (mysql (config-map :db-name 'wb001)))))

;; ensure databases are initialized before returning system state variable so we
;; can return the instances in the hash-map
(initialize!)

(defn unmap!
  []
  (do
    (ns-unmap *ns* 'isdb)
    (ns-unmap *ns* 'wbdb)))

