(defproject clj-oauth2-datomic-service "0.0.1-SNAPSHOT"
            :description "FIXME: write description"
            :url "http://example.com/FIXME"
            :license {:name "Eclipse Public License"
                      :url "http://www.eclipse.org/legal/epl-v10.html"}
            :dependencies [[org.clojure/clojure "1.11.1"]
                           [io.pedestal/pedestal.service "0.5.10"]
                           [clj-http "3.12.3"]
                           [clj-jwt "0.1.1"]
                           [io.pedestal/pedestal.jetty "0.5.10"]
                           [org.clojure/data.json "0.2.6"]
                           [com.outpace/config "0.13.5"]

                           [org.clojars.majorcluster/datomic-helper "1.1.0"]
                           [org.clojars.majorcluster/pedestal-api-helper "0.4.1"]
                           [org.clojars.majorcluster/clj-data-adapter "0.2.1"]
                           ;[com.datomic/datomic-pro "1.0.6362"]
                           [com.datomic/datomic-free "0.9.5697"]]
            :min-lein-version "2.0.0"
            :aliases {"config" ["run" "-m" "outpace.config.generate"]}
            :resource-paths ["config", "resources"]
            :jvm-opts ["-Dresource.config.edn=app-config.edn"]
            :profiles {:dev {:aliases {"run-dev" ["trampoline" "run" "-m" "clj-oauth2-datomic-service.server/run-dev"]}
                             :dependencies [[io.pedestal/pedestal.service-tools "0.5.10"]]
                             :jvm-opts ["-Dresource.config.edn=dev-config.edn"]}
                       :test {:dependencies [[io.pedestal/pedestal.service-tools "0.5.10"]
                                             [nubank/matcher-combinators "1.2.1"]]
                              :jvm-opts ["-Dresource.config.edn=test-config.edn"]}
                       :uberjar {:aot [clj-oauth2-datomic-service.server]}}
            :main ^{:skip-aot true} clj-oauth2-datomic-service.server)
