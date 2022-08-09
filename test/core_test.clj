(ns core-test
  (:require [clojure.test :refer :all]
            [io.pedestal.test :refer :all]
            [io.pedestal.http :as bootstrap]
            [clj-oauth2-datomic-service.ports.http.core :as service]
            [clj-oauth2-datomic-service.ports.datomic.core :as d.c]))

(def json-header
  {"Content-Type" "application/json"})

(def from-encoded-header
  {"Content-Type" "application/x-www-form-urlencoded"})

(def root-auth-headers
  (merge json-header {"Authorization" "Basic QWxhZGRpbjpvcGVuIHNlc2FtZQ=="}))

(def root-auth-encoded-headers
  (merge from-encoded-header {"Authorization" "Basic QWxhZGRpbjpvcGVuIHNlc2FtZQ=="}))

(def service
  (::bootstrap/service-fn (bootstrap/create-servlet service/service)))

(defn setup
  []
  (d.c/start))

(defn teardown
  []
  (d.c/erase-db!))

(defn test-fixture [f]
  (setup)
  (f)
  (teardown))
