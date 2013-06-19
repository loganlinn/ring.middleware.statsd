(defproject ring.middleware.statsd "1.0.0"
  :description "Middlewares for reporting request/response metrics to statsd"
  :url "https://github.com/loganlinn/ring.middleware.statsd"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [clj-statsd "0.3.9"]]
  :profiles {:dev {:dependencies [[midje "1.5.1"]
                                  [ring/ring-core "1.1.8"]
                                  [ring-mock "0.1.5"]]
                   :plugins [[lein-midje "3.0.0"]]}})
