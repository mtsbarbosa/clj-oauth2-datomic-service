(ns clj-oauth2-datomic-service.adapters.tokens
  (:require
    [clj-jwt.core  :refer :all]
    [clj-time.core :refer [now plus days]]))

(defn user-claim
  [user-id]
  {:iss "clj-oauth2-datomic-service"
   :exp (plus (now) (days 1))
   :user-id (if (uuid? user-id)
              (.toString user-id)
              user-id)
   :iat (now)})


