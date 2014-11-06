(ns couscous.core-test
  (:require [clojure.test :refer :all]
            [couscous.core :refer :all]))

(deftest abbreviate-words
  (testing "If we can abbreviate words of string or keyword type and return
    lower-case keyword abbreviations made on hyphen or upperscore separators."
    (is (= (abbreviate "some-global-corp") :sgc))
    (is (= (abbreviate :some_global_corp) :sgc))
    (is (= (abbreviate "another_name_acronym") :ana))
    (is (= (abbreviate :one-final-run) :ofr))
    (is (= (abbreviate [:some-funny-abbr "and_another_one"]) [:sfa :aao]))
    ))

(deftest hyphenate-words
  (testing "If we can transform strings or keywords with upperscores or hyphens
    to hyphenate keywords used in Clojure programs and code."
    (is (= (hyphenate "some_table_name") :some-table-name))
    (is (= (hyphenate :some_table_name) :some-table-name))
    (is (= (hyphenate "some-table-name") :some-table-name))
    (is (= (hyphenate :some-table-name) :some-table-name))
    (is (= (hyphenate ["SOME_TABLE_NAME" "another-table_name"]) [:some-table-name :another-table-name]))
    ))

(deftest upperscore-words
  (testing "If we can transform strings or keywords with hyphens or upperscores
    to return a string with upperscores used in many MySQL setups."
    (is (= (upperscore :some-table-name) "some_table_name"))
    (is (= (upperscore "some-table-name") "some_table_name"))
    (is (= (upperscore :some_table_name) "some_table_name"))
    (is (= (upperscore "some_table_name") "some_table_name"))
    (is (= (upperscore [:some-table-name "another-table-name"]) ["SOME_TABLE_NAME" "ANOTHER_TABLE_NAME"]))
    ))

(deftest database-option-maps
  (testing "If we get a option map returned to use in combination with a
    database adapter to create a connection pool."
    (is (= (:db db-opts) "INFORMATION_SCHEMA"))
    (is (= (:user db-opts) "root"))
    (is (= (:port db-opts) 3306))
    ))

(deftest fk-bitkeys-results
  (testing "Whether we retrieve the meaning of the InnoDB foreign relations
    column bitwise key flags as action to take on update/delete of records."
    (is (= (fk-bitkeys 1) "ON DELETE CASCADE"))
    (is (= (fk-bitkeys 2) "ON UPDATE SET NULL"))
    (is (= (fk-bitkeys 4) "ON UPDATE CASCADE"))
    (is (= (fk-bitkeys 8) "ON UPDATE SET NULL"))
    (is (= (fk-bitkeys 16) "ON DELETE NO ACTION"))
    (is (= (fk-bitkeys 32) "ON UPDATE NO ACTION"))))



