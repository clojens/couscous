(ns

  ^{:doc "The entities and wrappers around the Korma select function calls in
    this file and namespace are all performed on the `information_schema` tables
    of MySQL. The entities associated with this resource, have explicit database
    calls in their bodies due to the fact that Korma assumes the last set database
    to be the desirable one to use which is something I can't and don't want to
    assume but assure here (due to the importance of information we get from the
    components)."}

  ;==============
  couscous.schema
  ;==============

  (:require (clojure [string :as str]
                     [pprint :refer [print-table]])
            (couscous [protocol :refer :all]
                      [instance :refer :all])
            [korma.core :as korma
             :refer [select defentity has-many has-one pk table database
                     entity-fields fields where with exec-raw]]))

(defentity
  ^{:taxonomy :mysql-system-entity
    :doc "Entity of the information-schema columns table which holds info
    about data types, column flags, if it is nullable and what the primary
    key of the table is, which would otherwise involve more guesswork e.g. by
    taking the first column: this is more precise."}
  ;------
  columns
  ;------
  (database isdb)
  (table :columns)
  (entity-fields :table-name :extra :column-comment :column-key
                 :column-type :column-name :data-type :is-nullable))

(defentity
  ^{:taxonomy :mysql-system-entity
    :doc "Entity which holds information from key-column-usage table to tell
    us which tables are related to eachother in a generic (InnoDB/MyISAM)
    interface. InnoDB namespace holds more functionality to retrieve database
    table constraints on foreign keys, uniqueness and data entry."}
  ;--------
  relations
  ;--------
  (database isdb)
  (table :key-column-usage)
  (entity-fields :table-name :column-name :constraint-name
                 :referenced-table-name :referenced-column-name))

(defn
  ^{:taxonomy :database>operation>read
    :doc "Functional wrapper for selection of relevant fields to the obtainment
    of table-column relational meta-data from key-column-usage system tables.
    Flexible on input type thanks to protocols."
    :usage '[(read-relations :customer-entity)
             (read-relations "CUSTOMER_ENTITY")
             (read-relations "saLEs_Flat-order_itEM")
             (read-relations 'eav-attribute)]}

  ;-------------
  read-relations
  ;-------------

  [t]
  {:pre [(not (nil? t))]}
  (select relations
          (fields :table-name :column-name :constraint-name
                  :referenced-table-name :referenced-column-name)
          (where {:referenced-table-name (upperscore t)})))


(defn read-fields
  [&{dbs :dbs tbl :tbl :or {dbs :information-schema tbl :columns}}]
  (filter #(= (:table-name %) (underscore tbl))
          (filter #(= (:table-schema %) (underscore dbs))
                  (select columns
                          (fields :table-schema :table-name)))))


(defn pk!
  "Accurately determine the primary key of any table."
([] ;(map #((juxt :table-name :column-name) %)
         (filter #(= (:column-key %) "PRI")
                 (select columns
                         (fields :table-schema :table-name))))
  ;)
  ([tbl &{dbs :dbs :or {dbs :wb001}}]
   (-> (filter #(= (:column-key %) "PRI")
               (read-fields :dbs dbs :tbl tbl))
       first :column-name hyphenate)))


(defn ^{:doc "Returns the foreign key columns mapped to the table t provided primary key."
        :example '(get-schema-fks :wb001 :sales-flat-order)}
  get-schema-fks
  [sch tbl]
  (let [r (mapv #(hyphenate (str/join "." %))
       (map #((juxt :table-name :column-name) %)
            (first (vals (group-by :referenced-table-name
                                   (read-relations tbl))))))
        ]
    ;; safety check to ensure all foreign keys found point at the primary key
    (if-let [all-pk? (= (first (distinct (map :referenced-column-name
                                              (read-relations :sales-flat-order-item))))
                        (underscore (pk! :wb001 :sales-flat-order-item)))]
      {(pk! sch tbl) r}
      :error)))


(defn list-schemas
  "List all database schemas in the current server instance."
  []
  (->> :results (exec-raw ["show databases;"])
       (map #(hyphenate (:database %))) set))

(defn list-tables
  "Takes a database schema name of supported forms and retrieves
  collection of table names which is returned as vector of hyphen
  word separated keywords."
  [sch]
  (->> :results (exec-raw [(str "show tables from " (underscore sch) ";")])
       (map (keyword (str "tables-in-" (underscore sch))))
       (apply vector) hyphenate))

(defn list-fields
  "Outputs all fields"
  []
  (select columns
          (fields :*)
          (where {:table-schema "wb001"}))
  )

(defn all-pks
  []
  (remove nil? (map columnifies (list-fields))))


;; (map pk! (list-tables :wb001))

(defn form-relationship [s]
  "Forms a relationship according to keyword hieroglyph and as supported by korma."
  (map #(list (condp = (first %)
                :-1 'has-one :-* 'has-many :<- 'belongs-to)
              (second %) {:fk (nth % 2)})
       (partition 3 s)))


;;   ^{:taxonomy '[ storage systems
;;                  :-> database
;;                  :--> relational
;;                  :---> mysql
;;                  :====> :operation :=> select]

;;     :metrics [{:doc "Total columns in database" :fn '(count (read-fields :wb001))}]
;;     :doc ""}
