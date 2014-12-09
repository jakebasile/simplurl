;; To store our minified URLs, we need a schema for the database.
(ns simplurl.schema
  (:require [datomic.api :as d]))

;; We need a key to find the URL. This will be the shortened string
;; that a user can link to, that will expand to the full URL.
;; We'll store it as a string, and ensure that it is unique and indexed.
;; It'll be unique in such a way that you can upsert and change the URL based
;; on that key.

(def url-key
  {:db/ident :url/key
   :db/id (d/tempid :db.part/db)
   :db/valueType :db.type/string
   :db/cardinality :db.cardinality/one
   :db/unique :db.unique/identity
   :db.install/_attribute :db.part/db})

;; Now, we need to store the original URL that the key links to. Datomic
;; provides a native URI type, so we'll use that. 

(def url-prime
  {:db/ident :url/prime
   :db/id (d/tempid :db.part/db)
   :db/valueType :db.type/uri
   :db/cardinality :db.cardinality/one
   :db.install/_attribute :db.part/db})

;; Now that we have the maps for our schema, we can bring them together into
;; a list of transactions to send to Datomic.

(def schema
  [url-key
   url-prime])

