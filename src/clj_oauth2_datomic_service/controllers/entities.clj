(ns clj-oauth2-datomic-service.controllers.entities
  (:require [datomic-helper.entity :as d.ent]
            [clj-oauth2-datomic-service.ports.datomic.core :as d.c]
            [pedestal-api-helper.params-helper :as p.h]
            [clj-oauth2-datomic-service.adapters.entities :as a.e])
  (:import (java.util UUID)))

(defn get-all
  [id-ks converter-fn]
  (->> (d.ent/find-all (d.c/connect!) id-ks)
       (converter-fn)))

(defn get-by-id
  [id id-ks converter-fn]
  (->> {:id id}
       (p.h/extract-field-value :id)
       (d.ent/find-by-id (d.c/connect!) id-ks)
       (converter-fn)))

(defn post
  [id-ks m converter-fn]
  (let [id (UUID/randomUUID)]
    (->> (assoc m id-ks id)
         (converter-fn)
         (d.ent/upsert! (d.c/connect!) [id-ks id]))
    (a.e/convert-id-outbound id)))

(defn post-raw
  [m]
  (d.ent/insert! (d.c/connect!) m))

(defn patch
  [m id id-ks converter-fn]
  (->>  (converter-fn m)
        (d.ent/upsert! (d.c/connect!) [id-ks (p.h/extract-field-value :id {:id id})]))
  (assoc m :id (a.e/convert-id-outbound (:id m))))

(defn delete-by-id
  [id id-ks]
  (->> (a.e/convert-id-inbound id)
       (d.ent/delete! (d.c/connect!) id-ks)))

