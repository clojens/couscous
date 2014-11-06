(ns couscous.system
  (:require (couscous [instance :refer :all :as db]
                      [schema :refer [list-schemas]])
            [korma.db :refer [defdb mysql]]))

(defn system
  "System multable state centralized and isolated, now hooked up with
  dev/user.clj in the same project may reload projects without flushing
  state necessarily."
  []
  {:pools {:isdb isdb :wbdb wbdb}
   :schemas (list-schemas)})

(defn start
  "Performs side effects to initialize the system, acquire resources,
  and start it running. Returns an updated instance of the system."
  [system]
  (db/initialize!)
  system
  )

(defn stop
  "Performs side effects to shut down the system and release its
  resources. Returns an updated instance of the system."
  [system]
  (db/unmap!)
  system
  )
