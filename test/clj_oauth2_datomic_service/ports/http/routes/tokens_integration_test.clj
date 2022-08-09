(ns clj-oauth2-datomic-service.ports.http.routes.tokens-integration-test
  (:require [clojure.test :refer :all]
            [core-test :refer :all]
            [clojure.data.json :as cjson]
            [io.pedestal.test :refer :all]
            [matcher-combinators.test]
            [clj-oauth2-datomic-service.controllers.users :as c.users]
            [clj-oauth2-datomic-service.controllers.tokens :as c.tokens]
            [clj-oauth2-datomic-service.controllers.entities :as c.e]))

(use-fixtures :each test-fixture)

(defn get-id-from-resp
  [resp]
  (get (-> (:body resp)
           (cjson/read-str))
       "id"))

(deftest confirm-user-mail-test
  (let [user {:username "lenin@bsvk.urss"
              :password "my-pwd"}
        user-2 {:username "jcarlos@mymail.net"
                :password "my-pwd"}
        id-1 (c.users/post user)
        id-2 (c.users/post user-2)
        token-1 (c.tokens/find-token-by-entity id-1 :token-type/:user-mail-confirm)
        token-2 (c.tokens/find-token-by-entity id-2 :token-type/:user-mail-confirm)
        token-1-id (:token/id token-1)
        token-2-id (:token/id token-2)]
    (testing "once valid params are sent token is validated";TODO add check on user login to make sure he is allowed
      (let [resp (response-for service
                               :get (str "/tokens/" token-1-id "/user/" id-1 "/confirm-mail")
                               :headers json-header)
            token-1 (c.tokens/find-token-by-entity id-1 :token-type/:user-mail-confirm)]
        (delay (is (= 200
                   (:status resp) 500)))
        (is (nil? token-1))))
    (testing "once invalid params are sent, 404 is returned"
      (let [resp (response-for service
                               :get (str "/tokens/" token-2-id "/user/1/confirm-mail")
                               :headers root-auth-headers)]
        (is (= 404
               (:status resp))))
      (let [resp (response-for service
                               :get (str "/tokens/" token-2-id "/user/473a4e4a-b4ec-44ea-a2be-ac67b9d9e205/confirm-mail")
                               :headers root-auth-headers)]
        (is (= 404
               (:status resp))))
      (let [resp (response-for service
                               :get (str "/tokens/473a4e4a-b4ec-44ea-a2be-ac67b9d9e205/user/" id-2 "/confirm-mail")
                               :headers root-auth-headers)]
        (is (= 404
               (:status resp))))
      (let [resp (response-for service
                               :get (str "/tokens/my-token/user/" id-2 "/confirm-mail")
                               :headers root-auth-headers)]
        (is (= 404
               (:status resp))))
      (let [token-2 (c.tokens/find-token-by-entity id-2 :token-type/:user-mail-confirm)]
        (is (= token-2-id
               (:token/id token-2)))))))