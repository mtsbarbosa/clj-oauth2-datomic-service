(ns clj-oauth2-datomic-service.ports.http.routes.core
  (:require [clj-oauth2-datomic-service.ports.http.routes.users :as r.users]
            [clj-oauth2-datomic-service.ports.http.routes.tokens :as r.tokens])
  (:use clojure.pprint))


(def specs (into #{} (concat r.users/specs r.tokens/specs)))
;(def specs r.users/specs)
