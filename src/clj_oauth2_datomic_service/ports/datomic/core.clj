(ns clj-oauth2-datomic-service.ports.datomic.core
  (:require [datomic.api :as d]
            [outpace.config :refer [defconfig]]
            [clj-oauth2-datomic-service.ports.datomic.schema :refer [specs]]))


(defconfig db-host "datomic:mem:/")
(defconfig db-name "clj-oauth2-datomic-service")

(defonce db-uri (str db-host "/" db-name))

(defn create-db! []
  (d/create-database db-uri))

(defn connect! []
  (d/connect db-uri))

(defn create-schema!
  [conn]
  (d/transact conn specs))

(defn erase-db!
  "erase DB -> test use only!!!"
  []
  (println "ERASING DB!!!!!!!")
  (d/delete-database db-uri))

(defn start
  []
  (create-db!)
  (let [conn (connect!)]
    (create-schema! conn)))
