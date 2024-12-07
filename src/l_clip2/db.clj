(ns l-clip2.db
  (:require [datomic.client.api :as d]
            [l-clip2.util :refer [read-edn]]
            [babashka.fs :as fs]
            [clojure.string :as string]))

(defn local-storage []
  (let [f "local.edn"
        d ".datomic"
        system "db"
        h (fs/home)
        p1 (fs/path (-> h (str "/" d)))
        p2 (fs/path (-> h (str "/" d "/" f)))
        p3 (fs/path (-> h (str "/" system)))]
    (if-not (fs/exists? p1)
      (do (fs/create-dir p1)
          (fs/create-file p2)
          (fs/write-bytes p2 (.getBytes (String. (str "{:storage-dir \"" h "\"}"))))))
    (if-not (fs/exists? p3)
      (do (fs/create-dir p3)
          system)
      system)))

(defn client [cfg]
  (d/client cfg))

(defn create [client db-name]
  (d/create-database client {:db-name db-name}))

(defn connect [client db-name]
  (d/connect client {:db-name db-name}))

(defn seed [conn]
  (when (empty? (d/q '[:find ?a
                       :where
                       [?e ?a]
                       [?a :db/ident :movie/title]]
                     (d/db conn)))
    (let [schema (read-edn "db/schema.edn")
          data (read-edn "db/data.edn")
          xact #(d/transact conn {:tx-data %})]
      (xact schema)
      (xact data))))

(defn eid-title [conn]
  (-> (d/q '[:find ?e ?title
          :where
          [?e :movie/title ?title]]
        (d/db conn))
      sort))

(defn all-data-by-eid [conn eid]
  (d/pull (d/db conn) '[*] eid))

(defn new-movie [conn [{:keys [title genre year]}]]
  (d/transact conn {:tx-data [{:movie/title title
                               :movie/genre genre
                               :movie/release-year (Long/parseLong year)}]}))
