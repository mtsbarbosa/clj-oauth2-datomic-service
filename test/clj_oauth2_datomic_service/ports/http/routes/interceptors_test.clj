(ns clj-oauth2-datomic-service.ports.http.routes.interceptors-test
  (:require [clojure.test :refer :all]
            [clj-oauth2-datomic-service.ports.http.routes.interceptors :refer :all]
            [matcher-combinators.test]
            [matcher-combinators.matchers :as m])
  (:import (clojure.lang ExceptionInfo)))

(def authz (:enter check-root-authz))

(deftest check-root-authz-test
  (is (authz {:request {:headers {"Authorization" "Basic QWxhZGRpbjpvcGVuIHNlc2FtZQ=="}}}))
  (is (thrown-match? ExceptionInfo
                     {:type :unauthorized,
                      :message "Unauthorized"}
                     (authz {:request {:headers {"Authorization" "Basic dGVzdDp0ZXN0=="}}}))))