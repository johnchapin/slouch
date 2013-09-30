(defproject com.roomkey/slouch :lein-v

  :description "Simple Clojure RPC, now with clusters!"

  :url "http://github.com/g1nn13/slouch"

  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :plugins      [[s3-wagon-private "1.1.2"]
                 [com.roomkey/lein-v "3.3.4"]]

  :dependencies [
                 [com.taoensso/nippy "2.1.0"]
                 [compojure "1.1.5"] ;; TODO: Remove?
                 [http.async.client "0.5.2"]
                 [log4j/log4j "1.2.16"] ;;   TODO: Move to test
                 [org.clojure/clojure "1.5.1"]
                 [org.clojure/tools.logging "0.2.6"]
                 [ring/ring-core "1.2.0"] ;; TODO: Move to test
                 [slingshot "0.10.3"]
                 ]

  :ring {:handler slouch.example.ring/app}

  :repositories {"releases"  {:url "s3p://rk-maven/releases/"}
                 "snapshots" {:url "s3p://rk-maven/snapshots/"}})
