(ns clj-oauth2-datomic-service.ports.http.routes.users
  (:require [clj-oauth2-datomic-service.controllers.users :as c.users]
            [clj-oauth2-datomic-service.ports.http.routes.utils :refer :all]
            [clj-oauth2-datomic-service.ports.http.routes.interceptors :as i]
            [pedestal-api-helper.params-helper :as ph]
            [clojure.stacktrace :refer [print-stack-trace]])
  (:import (clojure.lang ExceptionInfo)))

(defn get-users
  [_]
  (let [result {:users (c.users/get-all)}]
    {:status 200 :headers json-header :body result}))

(defn get-user
  [request]
  (let [result (-> (:path-params request)
                   :id
                   (c.users/get-by-id))
        not-found? (nil? result)]
    (cond not-found? {:status 404 :headers json-header :body {}}
          :else {:status 200 :headers json-header :body result})))

(defn post-user
  [request]
  (let [id (-> (get request :form-params {})
               (ph/validate-and-mop!!
                 ["username","password"]
                 ["username","password"])
               (c.users/post))]
    {:status 201
     :headers (merge json-header {"Location" (str "http://localhost:8080/users/" id)})
     :body {:id id}}))

(defn delete-user
  [request]
  (try
    (c.users/delete-by-id (get-in request [:path-params :id]))
    {:status 200}
    (catch Exception ex
      (print-stack-trace ex)
      {:status 404})))


(def specs #{["/users" :get (conj i/json-root-stack `get-users) :route-name :get-users]
             ["/users/:id" :get (conj i/json-root-stack `get-user) :route-name :get-user]
             ["/users" :post (conj i/json-root-stack `post-user) :route-name :post-users]
             ["/users/:id" :delete (conj i/json-root-stack `delete-user) :route-name :delete-user]})