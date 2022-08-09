(ns clj-oauth2-datomic-service.adapters.users-test
  (:require [clojure.test :refer :all]
            [matcher-combinators.test]
            [clj-oauth2-datomic-service.adapters.users :refer :all]))

(deftest convert-inbound-test
  (is (match? {:user/name "Marx" :user/password "dN6qAqQ5JxTDAfcoF4n-FQ"}
              (convert-inbound {:name "Marx" :password "my-pwd"})))
  (is (match? [{:user/name "Marx" :user/password "dN6qAqQ5JxTDAfcoF4n-FQ"}
               {:user/id #uuid "f04c78f0-0673-11ed-b939-0242ac120002" :user/name "Engels" :user/password "YhLON9wwTljN3We9C3zicg"}]
              (convert-inbound [{:name "Marx" :password "my-pwd"}
                                {:id "f04c78f0-0673-11ed-b939-0242ac120002" :name "Engels" :password "open sesame"}])))
  (is (match? {}
              (convert-inbound {})))
  (is (= nil
         (convert-inbound nil))))

(deftest convert-outbound-test
  (is (= {:id "f04c78f0-0673-11ed-b939-0242ac120002"
          :name "Marx"}
         (convert-outbound {:user/id #uuid "f04c78f0-0673-11ed-b939-0242ac120002"
                            :user/name "Marx"
                            :user/password "dN6qAqQ5JxTDAfcoF4n-FQ"})))
  (is (= [{:id "f04c78f0-0673-11ed-b939-0242ac120002"
          :name "Marx"}
          {:id "a04c78f0-0673-11ed-b939-0242ac120002"
           :name "Engels"}]
         (convert-outbound [{:user/id #uuid "f04c78f0-0673-11ed-b939-0242ac120002"
                            :user/name "Marx"
                            :user/password "dN6qAqQ5JxTDAfcoF4n-FQ"}
                            {:user/id #uuid "a04c78f0-0673-11ed-b939-0242ac120002"
                             :user/name "Engels"
                             :user/password "YhLON9wwTljN3We9C3zicg"}])))
  )
