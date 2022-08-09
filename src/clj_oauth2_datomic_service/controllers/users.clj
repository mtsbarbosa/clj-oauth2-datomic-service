(ns clj-oauth2-datomic-service.controllers.users
  (:require [clj-oauth2-datomic-service.adapters.users :as a.u]
            [clj-oauth2-datomic-service.controllers.entities :as c.e]
            [clj-oauth2-datomic-service.controllers.tokens :as c.t]
            [clj-oauth2-datomic-service.ports.mail.client :as mail]
            [pedestal-api-helper.params-helper :as p.h]))

(defn get-all
  []
  (c.e/get-all :user/id a.u/convert-outbound))

(defn get-by-id
  [id]
  (c.e/get-by-id id :user/id a.u/convert-outbound))

(defn post
  [m]
  (let [found (-> (:username m)
                  (c.e/get-by-id :user/username (fn [m] m)))]
    (cond found (throw (ex-info "Username already used" {:type :duplicated
                                                         :message "Username already used"
                                                         :reason {}}))
          :else (let [id (c.e/post :user/id m a.u/convert-inbound)
                      token-id (c.t/create id
                                           :entity-type/user
                                           :token-type/:user-mail-confirm)]
                    (mail/send-mail (:username m)
                                    ""
                                    "clj-oauth2-datomic-service E-mail Confirmation"
                                    (format "<a href=\"%s/users/confirm-mail/token/%s\">Confirm your E-mail.</a><br/><br/>Regards,<br/>clj-oauth2-datomic-service Team"
                                            "http://localhost:8080"
                                            (.toString token-id)))
                    id))))

(defn delete-by-id
  [id]
  (when (p.h/is-uuid id)
    (c.e/delete-by-id id :user/id)))

