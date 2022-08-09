(ns clj-oauth2-datomic-service.adapters.users
  (:require [clj-oauth2-datomic-service.adapters.entities :as a.entities]
            [clj-data-adapter.core :refer [transform-values]]
            [clj-oauth2-datomic-service.adapters.encrypt :refer [encrypt]]))

(defn convert-inbound
  [m]
  (-> (transform-values #(cond (= :password %) (encrypt %2)
                               :else %2) m)
      (a.entities/convert-inbound "user")))

(defn convert-outbound
  [m]
  (-> (transform-values #(cond (= :user/password %) nil
                               :else %2) m)
      (a.entities/convert-outbound)))
