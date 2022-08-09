(ns clj-oauth2-datomic-service.configs
  (:require [outpace.config :refer [defconfig]]))

(defconfig env "test")

(defn env-test?
  []
  (= "test" env))
