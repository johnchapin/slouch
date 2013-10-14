(defproject com.roomkey/slouch :lein-v
  :description "Simple Clojure RPC over HTTP"

  :url "http://github.com/g1nn13/slouch"

  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :plugins      [[s3-wagon-private "1.1.2"]
                 [com.roomkey/lein-v "3.3.4"]]

  :dependencies [[org.clojure/clojure "1.5.1"]
                 [com.taoensso/nippy "2.1.0"]
                 [http.async.client "0.5.2"]]

  :profiles {:dev {:source-paths ["example/src"]
                   :plugins [[lein-ring "0.8.7"]]
                   :dependencies [[midje "1.5.1"]]
                   :ring {:handler example.server/app}}}

  :repositories {"releases"  {:url "s3p://rk-maven/releases/"}
                 "snapshots" {:url "s3p://rk-maven/snapshots/"}})
