(ns clj-oauth2-datomic-service.controllers.tokens
  (:require [clj-oauth2-datomic-service.controllers.entities :as c.e]
            [clj-oauth2-datomic-service.ports.datomic.core :refer [connect!]]
            [clj-oauth2-datomic-service.ports.datomic.tokens :as d.token])
  (:import (java.util UUID)))

(defn create
  ([claim-fn entity-id entity-type token-type]
   )
  ([entity-id entity-type token-type]
    (let [token-id (.toString (UUID/randomUUID))
          entity-id (UUID/fromString entity-id)]
      (c.e/post-raw {:token/id token-id
                     :token/entity-id entity-id
                     :token/entity-type entity-type
                     :token/token-type token-type})
      token-id)))

(defn find-token-by-entity
  [entity-id token-type]
  (d.token/find-token-by-entity (connect!) entity-id token-type))

(defn has-token?
  [id entity-id token-type]
  (-> (d.token/find-token (connect!) id entity-id token-type)
      (nil?)
      (not)))