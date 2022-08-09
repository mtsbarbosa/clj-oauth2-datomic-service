(ns clj-oauth2-datomic-service.adapters.entities
  (:require [clojure.walk :refer [postwalk]]
            [clj-data-adapter.core :refer [transform-keys
                                           transform-values
                                           namespaced-key->kebab-key
                                           kebab-key->namespaced-key]])
  (:import (java.util UUID)))

(defn convert-inbound
  ([m]
   (cond (nil? m) m
         (map? m) (transform-values #(cond (= :id %) (UUID/fromString %2)
                                           :else %2)
                                    m)
         :else (map #(convert-inbound %) m)))
  ([m entity]
   (->> (convert-inbound m)
        (transform-keys (partial kebab-key->namespaced-key entity)))))

(defn convert-outbound
  [m]
  (cond (nil? m) m
        (map? m) (->> (postwalk #(cond (uuid? %)(.toString %)
                                       :else %) m)
                      (transform-keys namespaced-key->kebab-key))
        :else (map #(convert-outbound %) m)))

(defn str->uuid
  [s]
  (UUID/fromString s))

(defn uuid->str
  [uuid]
  (.toString uuid))

(defn convert-id-inbound
  [id]
  (cond (uuid? id) id
        (string? id) (str->uuid id)
        :else id))

(defn convert-id-outbound
  [id]
  (cond (uuid? id) (uuid->str id)
        (string? id) id
        :else id))
