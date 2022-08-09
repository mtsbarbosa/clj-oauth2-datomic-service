(ns clj-oauth2-datomic-service.mail.client-test
  (:require [clojure.test :refer :all]
            [clj-oauth2-datomic-service.ports.mail.client :refer :all]))

(defonce test-client
         {:send-uri "http://my-email-provider/send",
          :api-key "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",
          :client-post (fn [uri m _ _]
                         [uri m])})

(deftest send-mail-test
  (let [resp (send-mail test-client "lenin@bsvk.urss" "Lenin" "Test" "privet, konrad")]
    (is (= "http://my-email-provider/send"
           (first resp)))
    (is (= "application/json"
           (get-in (last resp) [:headers "Content-Type"])))
    (is (= "Bearer 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"
           (get-in (last resp) [:headers "Authorization"])))
    (let [body { "personalizations" [{"to" [{"email" "lenin@bsvk.urss", "name" "Lenin"}],
                                      "subject" "Test"}],
                 "content" [{"type" "text/html",
                             "value" "privet, konrad"}],
                 "from" {"email" nil, "name" nil}}]
      (is (= (clojure.data.json/write-str body)
             (get (last resp) :body))))))