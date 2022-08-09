(ns clj-oauth2-datomic-service.ports.http.routes.interceptors
  (:require [io.pedestal.http :as http]
            [clojure.data.json :as cjson]
            [outpace.config :refer [defconfig]]
            [clojure.string :as s]
            [io.pedestal.http.body-params :as body-params]
            [clj-oauth2-datomic-service.ports.http.routes.error-handler :refer [service-error-handler]]
            [clj-data-adapter.core :refer [transform-keys]])
  (:import (clojure.lang ExceptionInfo)
           (java.util Base64)))

(defconfig root-username "Aladdin")
(defconfig root-pwd "open sesame")

(defn convert-to-json
  [m]
  (cond (map? m) (cjson/write-str m)
        :else m))

(defn json-out
  []
  {:name ::json-out
   :leave (fn [context]
            (->> (:response context)
                 :body
                 (convert-to-json)
                 (assoc-in context [:response :body])))})

(defn authorization-error
  "Throws an authorization error"
  ([]
   (authorization-error "Unauthorized" {}))
  ([message]
   (authorization-error message {}))
  ([message data]
   (throw (ex-info "Unauthorized"
                   {:type :unauthorized
                    :message message
                    :reason data}))))

(defn- byte-transform
  "Used to encode and decode strings.  Returns nil when an exception
  was raised."
  [direction-fn string]
  (try
    (s/join (map char (direction-fn (.getBytes ^String string))))
    (catch Exception _)))

(defn- decode-base64
  "Will do a base64 decoding of a string and return a string."
  [^String string]
  (byte-transform #(.decode (Base64/getDecoder) ^bytes %) string))

(defn- extract-basic-authentication
  [request]
  (let [auth (or (get-in request [:headers "Authorization"])
                 (get-in request [:headers "authorization"]))
        cred (and auth (decode-base64 (last (re-find #"^Basic (.*)$" auth))))
        [user pass] (and cred (s/split (str cred) #":" 2))]
    [user pass]))


(defn- authorize-root
  [request]
  (let [[username pwd] (extract-basic-authentication request)]
    (and (= username root-username)
         (= pwd root-pwd))))

(def check-root-authz
  {:name ::check-root-authz
   :enter (fn [context]
            (try
              (let [request (get context :request)]
                (cond (authorize-root request) context
                      :else (authorization-error)))
              (catch ExceptionInfo e
                (println "error authorizing" (ex-cause e) (ex-message e))
                (authorization-error))))})

(def json-root-stack
  [service-error-handler
   check-root-authz
   (body-params/body-params)
   (json-out)])

(def json-stack
  [service-error-handler
   (body-params/body-params)
   (json-out)])
