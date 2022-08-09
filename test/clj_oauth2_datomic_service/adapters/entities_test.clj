(ns clj-oauth2-datomic-service.adapters.entities-test
  (:require [clojure.test :refer :all]
            [clj-oauth2-datomic-service.adapters.entities :refer :all]))


(deftest convert-inbound-test
  (is (= {:name "Rosa" :id #uuid "f04c78f0-0673-11ed-b939-0242ac120002"}
        (convert-inbound {:name "Rosa" :id "f04c78f0-0673-11ed-b939-0242ac120002"})))
  (is (= [{:name "Rosa" :id #uuid "f04c78f0-0673-11ed-b939-0242ac120002"}
          {:name "Fidel" :id #uuid "f04a78f0-0673-11ed-b939-0242ac120004"}]
         (convert-inbound [{:name "Rosa" :id "f04c78f0-0673-11ed-b939-0242ac120002"}
                           {:name "Fidel" :id "f04a78f0-0673-11ed-b939-0242ac120004"}])))
  (is (= {:person/name "Rosa" :person/id #uuid "f04c78f0-0673-11ed-b939-0242ac120002"}
         (convert-inbound {:name "Rosa" :id "f04c78f0-0673-11ed-b939-0242ac120002"} "person")))
  (is (= [{:person/name "Rosa" :person/id #uuid "f04c78f0-0673-11ed-b939-0242ac120002"}
          {:person/name "Fidel" :person/id #uuid "f04a78f0-0673-11ed-b939-0242ac120004"}]
         (convert-inbound [{:name "Rosa" :id "f04c78f0-0673-11ed-b939-0242ac120002"}
                           {:name "Fidel" :id "f04a78f0-0673-11ed-b939-0242ac120004"}] "person")))
  (is (= nil
         (convert-inbound nil)))
  (is (= [nil]
         (convert-inbound [nil]))))

(deftest convert-outbound-test
  (is (= {:name "Rosa" :id "f04c78f0-0673-11ed-b939-0242ac120002"}
         (convert-outbound {:name "Rosa" :id #uuid "f04c78f0-0673-11ed-b939-0242ac120002"})))
  (is (= [{:name "Rosa" :id "f04c78f0-0673-11ed-b939-0242ac120002"}
          {:name "Fidel" :id "f04a78f0-0673-11ed-b939-0242ac120004"}]
         (convert-outbound [{:name "Rosa" :id #uuid "f04c78f0-0673-11ed-b939-0242ac120002"}
                            {:name "Fidel" :id #uuid "f04a78f0-0673-11ed-b939-0242ac120004"}])))
  (is (= nil
         (convert-outbound nil)))
  (is (= [nil]
         (convert-outbound [nil]))))