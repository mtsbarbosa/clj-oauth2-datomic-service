(ns clj-oauth2-datomic-service.ports.datomic.tokens
  (:require [datomic-helper.entity :refer [transform-out
                                           database-context]]))

(defn find-token-by-entity
  ([dcontext conn entity-id token-type]
   (let [db ((:db dcontext) conn)
         q '[:find (pull ?e [*]) .
             :in $ ?entity-id ?token-type
             :where [?e :token/entity-id ?entity-id]
                    [?e :token/token-type ?token-type]]]
     (->> ((:q dcontext) q db entity-id token-type)
          (transform-out))))
  ([conn entity-id token-type]
   (find-token-by-entity database-context conn entity-id token-type)))

(defn find-token
  ([dcontext conn token-id entity-id token-type]
   (let [db ((:db dcontext) conn)
         q '[:find (pull ?e [*]) .
             :in $ ?token-id ?entity-id ?token-type
             :where [?e :token/id ?token-id]
                    [?e :token/entity-id ?entity-id]
                    [?e :token/token-type ?token-type]]]
     (->> ((:q dcontext) q db token-id entity-id token-type)
          (transform-out))))
  ([conn token-id entity-id token-type]
   (find-token database-context conn token-id entity-id token-type)))
