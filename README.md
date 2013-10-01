# slouch

Simple Clojure RPC over HTTP

Heavily inspired by slacker, but using:

- nippy (https://github.com/ptaoussanis/nippy)
- http.async.client (https://github.com/neotyk/http.async.client)
- ring (https://github.com/ring-clojure/ring)

## usage

;; server
(require '[slouch.server]
         '[slouch.example.api]
(def ring-app
 (slouch.server/handler 'slouch.example.api))

;; client
(require '[slouch.client])
(def c (slouch.client/new-client "http://localhost:3000"))
(defn-remote c sum :remote-ns 'slouch.example.api)
(sum 1 2 3)
;; 6

## License

Copyright © 2013 Roomkey

Distributed under the Eclipse Public License, the same as Clojure.
