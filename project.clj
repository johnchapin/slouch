(defproject com.roomkey/slouch :lein-v

  :description "Simple Clojure RPC, now with clusters!"

  :url "http://github.com/g1nn13/slouch"

  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :plugins      [[s3-wagon-private "1.1.2"]
                 [com.roomkey/lein-v "3.3.4"]]

  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/tools.logging "0.2.6"]

                 [com.taoensso/nippy "2.1.0"]
                 [http.async.client "0.5.2"]
                 [ring/ring-core "1.2.0"] ;; TODO: Move to test
                 [slingshot "0.10.3"]

                 [log4j/log4j "1.2.16"]
                 [org.slf4j/jcl-over-slf4j "1.6.4"]
                 [org.slf4j/slf4j-api "1.6.4"]
                 [org.slf4j/slf4j-log4j12 "1.6.4"]

                 ]

  :ring {:handler slouch.example.ring/app}

  :repositories {"releases"  {:url "s3p://rk-maven/releases/"}
                 "snapshots" {:url "s3p://rk-maven/snapshots/"}})
