(ns clj-oauth2-datomic-service.ports.http.routes.tokens
  (:require [clj-oauth2-datomic-service.ports.http.routes.interceptors :refer [json-stack]]
            [clj-oauth2-datomic-service.controllers.entities :refer [delete-by-id] ]
            [pedestal-api-helper.params-helper :refer [validate-and-mop!! is-uuid]]))

(defn confirm-user-mail
  [request]
  (let [{user-id :user-id
         token-id :token-id} (-> (get request :path-params {})
                                   (validate-and-mop!!
                                     ["user-id","token-id"]
                                     ["user-id","token-id"]))]
    (cond (is-uuid token-id)
          (do (delete-by-id token-id :token/id)
              {:status 200 :body "E-Mail Confirmed!"})
          :else {:status 404 :body "Invalid token"})))

(def specs #{["/tokens/:token-id/user/:user-id/confirm-mail"
              :get (conj json-stack `confirm-user-mail)
              :route-name :confirm-user-mail]})