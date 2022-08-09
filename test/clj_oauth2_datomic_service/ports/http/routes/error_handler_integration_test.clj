(ns clj-oauth2-datomic-service.ports.http.routes.error-handler-integration-test
  (:require [clojure.test :refer :all]
            [io.pedestal.test :refer :all]
            [clj-oauth2-datomic-service.ports.http.routes.error-handler :refer :all]
            [matcher-combinators.test]
            [io.pedestal.http :as service]
            [io.pedestal.http.route.definition :refer [defroutes]]
            [ring.util.response :as ring-resp]))


(defn bad-format
  [_]
  (ring-resp/response (throw (ex-info "Bad Format" {:type :bad-format
                                                    :validation-messages [:field "name" :message ":name is mandatory"]}))))

(defn drop-through
  [_]
  (throw (Exception. "Just testing the error-handler, this is not a real exception")))

(defroutes request-handling-routes
           [[:request-handling "error-dispatch.pedestal"
             ["/" ^:interceptors [service-error-handler]
              ["/div" {:any bad-format}]
              ["/drop" {:any drop-through}]]]])

(defn make-app [options]
  (-> options
      service/default-interceptors
      service/service-fn
      ::service/service-fn))

(def app (make-app {::service/routes request-handling-routes}))

(def url "http://error-dispatch.pedestal/div")
(def drop-url "http://error-dispatch.pedestal/drop")

(deftest captures-bad-format-exception
  (let [resp (response-for app :get url)]
    (is (= (:body resp)
           "{\"type\":\"bad-format\",\"validation-messages\":[\"field\",\"name\",\"message\",\":name is mandatory\"]}"))
    (is (= (:status resp)
           400))))

(deftest allows-fallthrough-behavior
  (let [boom-resp (response-for app :get drop-url)]
    (is (= (:status boom-resp)
           500))
    (is (= (:body boom-resp)
           "Internal server error: exception"))))