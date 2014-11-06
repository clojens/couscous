(ns couscous.innodb
  (:require [korma.core :refer :all]
            (couscous [instance :refer :all]
                      [protocol :refer :all])
            [plumbing.core :refer [map-vals]]))

(defn fk-bitkeys
  "Returns the meaning of bitwise flags in innodb foreign relationship tables."
  [i] (condp = i
        1 "ON DELETE CASCADE" 2 "ON UPDATE SET NULL"
        4 "ON UPDATE CASCADE" 8 "ON UPDATE SET NULL"
        16 "ON DELETE NO ACTION" 32 "ON UPDATE NO ACTION"))

(defentity
  ^{:doc "Information schema holds innodb-sys-foreign table with data
    on foreign relations but only those from InnoDb tables."}
  foreign-relations
  (database isdb)
  (table :innodb-sys-foreign))

(defn ^{:doc"S-expression pur-sang. It takes a schema name as keyword and returns
        innodb relations for all tables that schema, grouped by parent as hash key."
        :example '(filter-relations :wb001)}
  filter-relations
  [dbk]
  (map-vals (group-by first
                      (map dbk (filter dbk (mapv #(two-triple %)
                                                 (mapv #((juxt :ref-name :for-name) %)
                                                       (select foreign-relations
                                                               (fields :ref-name :for-name)
                                                               ))))))
            #(apply vector (sort (map last %)))))

