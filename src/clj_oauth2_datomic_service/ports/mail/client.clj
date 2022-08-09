(ns clj-oauth2-datomic-service.ports.mail.client
  (:require [outpace.config :refer [defconfig]]
            [clj-http.client :as client]
            [clojure.data.json :as json]))

;(defconfig mail-send-uri "http://my-mail-provider")
;(defconfig mail-api-key "x4lfkfiijjd233")
;(defconfig mail-token-sender "my-mail@mail.com")
(defconfig mail-send-uri "https://api.sendgrid.com/v3/mail/send")
(defconfig mail-api-key "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")
(defconfig mail-token-sender "majorcluster.langrt.db@gmail.com")
(defconfig mail-token-sender-name "clj-oauth2-datomic-service Team")

(defonce default-client
         {:send-uri mail-send-uri,
          :api-key mail-api-key,
          :token-sender mail-token-sender,
          :token-sender-name mail-token-sender,
          :client-post client/post})

(defn send-mail
  ([client to-mail to-name subj body]
   (let [to {:to [{:email to-mail
                   :name to-name}]
             :subject subj}
         content {:type "text/html"
                  :value body}
         from {:email (:token-sender client)
               :name  (:token-sender-name client)}
         body (-> {:personalizations [to]
                   :content [content]
                   :from from}
                  (json/write-str))]
     ((:client-post client) (:send-uri client)
                  {:async true,
                   :body body
                   :headers {"Content-Type" "application/json"
                             "Authorization" (format "Bearer %s" (:api-key client))}}
      ;; respond callback
      (fn [response] (println "response is:" response))
      ;; raise callback
      (fn [exception] (println "exception message is: " (.getMessage exception) (.getCause exception))))))
  ([to-mail to-name subj body]
   (send-mail default-client to-mail to-name subj body)))