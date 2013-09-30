(ns slouch.example.api)

(defn sum [& nums]
  (apply + nums))

(defn product [& nums]
  (apply * nums))

(defn exception [& _]
  (throw (Exception. "foo")))

(defn complex [& _]
  [{:foo :bar
    :baz [:bing {:monkeys 123} true]}
   "blargh"])
