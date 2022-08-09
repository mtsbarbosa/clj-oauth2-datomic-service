(ns clj-oauth2-datomic-service.ports.http.routes.users-integration-test
  (:require [clojure.test :refer :all]
            [core-test :refer :all]
            [clojure.data.json :as cjson]
            [io.pedestal.test :refer :all]
            [matcher-combinators.test]
            [clojure.string :as cstr]
            [clj-oauth2-datomic-service.controllers.users :as c.users]))

(use-fixtures :each test-fixture)

(defn get-id-from-resp
  [resp]
  (get (-> (:body resp)
           (cjson/read-str))
       "id"))

(deftest get-users
  (let [user {:username "lenin@bsvk.urss"
              :password "my-pwd"}
        user-2 {:username "jcarlos@mymail.net"
                :password "my-pwd"}]
    (testing "when root credentials are not provided response is 401"
      (is (= 401
             (:status (response-for service
                                    :get "/users")))))
    (testing "empty users are returned once no entry is on db"
      (is (= "{\"users\":[]}"
             (:body (response-for service
                                  :get "/users"
                                  :headers root-auth-headers)))))
    (testing "once users are on db they are returned"
      (let [id-1 (c.users/post user)
            resp (response-for service
                               :get "/users"
                               :headers root-auth-headers)]
        (is (= (format "{\"users\":[{\"username\":\"lenin@bsvk.urss\",\"id\":\"%s\"}]}" id-1)
               (:body resp)))
        (is (= 200
               (:status resp)))
        (let [id-2 (c.users/post user-2)
              resp (response-for service
                                 :get "/users"
                                 :headers root-auth-headers)]
          (is (= (format "{\"users\":[{\"username\":\"lenin@bsvk.urss\",\"id\":\"%s\"},{\"username\":\"jcarlos@mymail.net\",\"id\":\"%s\"}]}" id-1 id-2)
                 (:body resp)))
          (is (= 200
                 (:status resp))))))))

(deftest get-user
  (let [user {:username "lenin@bsvk.urss"
              :password "my-pwd"}
        user-2 {:username "jcarlos@mymail.net"
                :password "my-pwd"}
        id-1 (c.users/post user)
        id-2 (c.users/post user-2)]
    (testing "when root credentials are not provided response is 401"
      (is (= 401
             (:status (response-for service
                                    :get (str "/users/" id-1))))))
    (testing "once valid id is sent, user is returned"
      (let [resp (response-for service
                               :get (str "/users/" id-1)
                               :headers root-auth-headers)]
        (is (= (format "{\"username\":\"lenin@bsvk.urss\",\"id\":\"%s\"}" id-1)
               (:body resp)))
        (is (= 200
               (:status resp))))
      (let [resp (response-for service
                               :get (str "/users/" id-2)
                               :headers root-auth-headers)]
        (is (= (format "{\"username\":\"jcarlos@mymail.net\",\"id\":\"%s\"}" id-2)
               (:body resp)))
        (is (= 200
               (:status resp)))))
    (testing "once invalid id is sent, 404 is returned"
      (let [resp (response-for service
                               :get "/users/3"
                               :headers root-auth-headers)]
        (is (= 404
               (:status resp))))
      (let [resp (response-for service
                               :get "/users/473a4e4a-b4ec-44ea-a2be-ac67b9d9e205"
                               :headers root-auth-headers)]
        (is (= 404
               (:status resp)))))))

(deftest post-user
  (testing "when root credentials are not provided response is 401"
    (is (= 401
           (:status (response-for service :post "/users"
                                          :headers from-encoded-header
                                          :body "{\"username\":\"lenin@bsvk.urss\"}")))))
  (testing "missing fields are validated with 400"
    (let [resp (response-for service :post "/users"
                                     :headers root-auth-encoded-headers
                                     :body "")]
      (is (= "{\"type\":\"bad-format\",\"validation-messages\":[{\"field\":\"username\",\"message\":\"Field :username is not present\"},{\"field\":\"password\",\"message\":\"Field :password is not present\"}]}"
             (:body resp)))
      (is (= 400
             (:status resp))))
    (let [resp (response-for service :post "/users"
                                     :headers root-auth-encoded-headers
                                     :body "username=test@test.com")]
      (is (= "{\"type\":\"bad-format\",\"validation-messages\":[{\"field\":\"password\",\"message\":\"Field :password is not present\"}]}"
             (:body resp)))
      (is (= 400
             (:status resp)))))
  (testing "when mandatory fields are present, 201 with location is returned and user is placed on db"
    (let [new-user {:username "test@test.com"}
          resp (response-for service :post "/users"
                                     :headers root-auth-encoded-headers
                                     :body "username=test@test.com&password=my-pwd")
          id (get-id-from-resp resp)
          db-found (c.users/get-by-id id)]
      (is (= (format "{\"id\":\"%s\"}" id)
             (:body resp)))
      (is (= 201
             (:status resp)))
      (is (cstr/ends-with?
            (get-in resp [:headers "Location"])
            (str "/users/" id)))
      (is (= (assoc new-user :id id)
             db-found))))
  (testing "token was created for the user"
    (is (= 1 1)))                                                ;TODO: add after token stuff is created
  (testing "inserting user with same username results in 400"
    (let [resp (response-for service :post "/users"
                                     :headers root-auth-encoded-headers
                                     :body "username=test@test.com&password=my-pwd")]
      (is (= "{\"type\":\"duplicated\",\"message\":\"Username already used\",\"reason\":{}}"
             (:body resp)))
      (is (= 400
             (:status resp))))))

(deftest delete-user
  (testing "when root credentials are not provided response is 401"
    (is (= 401
           (:status (response-for service :delete "/users/1"
                                          :headers from-encoded-header)))))
  (testing "not present user delete returns 200"
    (let [resp (response-for service :delete "/users/1"
                                     :headers root-auth-encoded-headers)]
      (is (= 200
             (:status resp)))))
  (testing "when id is present, user is deleted"
    (let [user {:username "test@test.com"
                :password "my-pwd"}
          id (c.users/post user)
          user (dissoc user :password)
          db-found-before (c.users/get-by-id id)
          resp (response-for service :delete (str "/users/" id)
                                     :headers root-auth-encoded-headers)
          db-found-after (c.users/get-by-id id)]
      (is (= 200
             (:status resp)))
      (is (match? (merge {:id id} user) db-found-before))
      (is (= (nil? db-found-after))))))
