(ns slouch.example.api)

(defn sum [& nums]
  (apply + nums))

(defn product [& nums]
  (apply * nums))
