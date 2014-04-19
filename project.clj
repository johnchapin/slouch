(defproject com.roomkey/slouch "0.2.0-SNAPSHOT"
  :description "Simple Clojure RPC over HTTP"

  :url "http://github.com/johnchapin/slouch"

  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [[org.clojure/clojure "1.5.1"]
                 [com.taoensso/nippy "2.1.0"]
                 [http-kit "2.1.16"]]

  :profiles {:dev {:source-paths ["example/src"]
                   :plugins [[lein-ring "0.8.7"]]
                   :dependencies [[midje "1.5.1"]]
                   :ring {:handler example.server/app}}})
