# slouch

Simple Clojure RPC over HTTP

Heavily inspired by [slacker](http://github.com/sunng87/slacker), but using:

- nippy (https://github.com/ptaoussanis/nippy)
- http.async.client (https://github.com/neotyk/http.async.client)
- ring (https://github.com/ring-clojure/ring)

## Demo

Start the server:
```shell
$ lein ring server-headless
...
Started server on port 3000
```

In a new terminal, exercise the client:
```shell
$ lein repl
...
user=> (use 'example.client)
user=> (product 1 2 3 4)
24
user=> (meta (stats-meta 1 2 3 4))
{:mean 5/2, :median 5/2, :mode 4}
```

## Interceptors

Interceptors are just Ring middleware functions that are injected into the slouch server pipeline.

For example, to record processing time for the entire pipeline:

```clojure
(defn wrap-processing-time [handler]
  (fn [request]
      (let [start (System/currentTimeMillis)
            response (handler request)
            finish (System/currentTimeMillis)
            duration (- finish start)]
        (log/info :ProcessingTime duration)
        response)))

(def ring-app
  (slouch.server/handler
    'slouch.example.api :interceptors {:alpha wrap-processing-time}))
```

There are three injection points for interceptors in the slouch server pipeline.

*:alpha* - wraps the entire pipeline

*:beta*  - after deserialization of the incoming request body, before serialization of the outgoing response body

*:gamma* - after looking up the slouch function name in the incoming request URI, before function execution.

See the [Ring documentation](https://github.com/ring-clojure/ring/wiki/Concepts) to learn more about middleware.

Use [clojure.core/comp](http://clojure.github.io/clojure/clojure.core-api.html#clojure.core/comp) to compose multiple interceptors for injection at a single point.

## License

Copyright © 2013 Roomkey

Distributed under the Eclipse Public License, the same as Clojure.
