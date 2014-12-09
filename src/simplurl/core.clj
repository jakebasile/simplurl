;; This contains the meat of the server, and includes all data access.
(ns simplurl.core
  (:import (java.net URI))
  (:require [datomic.api :as d]
            [simplurl.schema :refer [schema]]))

(def ^:dynamic *db-uri*
  "Datomic uri to connect to. Defaults to in-memory."
  "datomic:mem://simplurl")

;; ## Connection Management
;; We'll obviously need to connect to the database, and we'll
;; want some functions to make it a little more succinct.
;; If the value  of the dynamic *db-uri* is changed, these
;; functions will start hitting the new target database.

(defn connect
  "Connects to the database, returning the connection."
  []
  (d/connect *db-uri*))

(defn db
  "Gets a db instance from the connection."
  []
  (d/db (connect)))

;; ## DB Management
;; First, provide a way to (re)create the database. This will create the actual
;; database in Datomic, and then apply the schema from simplurl.schema.

(defn create
  "Creates the database."
  []
  (d/create-database *db-uri*)
  (d/transact (connect) schema))

(defn delete
  "Purge the unclean!"
  []
  (d/delete-database *db-uri*))

(defn recreate
  "Deletes the database, then recreates it."
  []
  (delete)
  (create))

;; ## Reading
;; To get the data we need to redirect, we'll have to query.
;; The basic query we need is to find a long URL from a short key.
;; We find the :url/prime scalar value (only one) in the default
;; database where :url/key is the string we are given.

(defn expand
  "Find the original URL from a key, if there is one."
  [k]
  (d/q '[:find ?prime .
         :in $ ?key
         :where [?url :url/key ?key]
                [?url :url/prime ?prime]]
       (db)
       k))

;; ## Writing
;; We also need to create shortened URLs. To do that, we need to add
;; both the short key and long URL to the database. We're going to allow
;; changing of the long URL, so we just upsert on the key.

(defn add
  "Adds a key->URL mapping, updating if it already exists."
  [k url]
  (d/transact (connect) [{:db/id (d/tempid :db.part/user)
                          :url/key k
                          :url/prime (URI. url)}]))

