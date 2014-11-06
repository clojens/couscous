(ns couscous.mocro)


(defmacro erm
  "Allow for a conventional description of entity-relationships in real-world
  (often erratic) database scenario's like used in e.g. Magento. Uses the first
  column of the table as primary key as only side-effect since the pattern on
  naming conventions is too often broken."
  [entity & relations]
  (template
   (concat (list 'defentity '~entity
                 (list 'pk (~pk! '~entity))
                 (list 'table ~(keyword entity) ~(abbreviate entity)))

           (form-relationship '~relations)

           )
   ))
