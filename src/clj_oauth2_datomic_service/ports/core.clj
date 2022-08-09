(ns clj-oauth2-datomic-service.ports.core
  (:require [clj-oauth2-datomic-service.ports.http.core :as http.c]
            [clj-oauth2-datomic-service.ports.datomic.core :as d.c]))

(defn start-ports-dev
  []
  (d.c/start)
  (http.c/start-dev))

(defn start-ports
  []
  (d.c/start)
  (http.c/start))
