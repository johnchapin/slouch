(ns example.api)

(defn product [& nums]
  {:pre [every? number? nums]}
  (apply * nums))

(defn- mean [nums]
  {:pre [every? number? nums]}
  (/ (apply + nums) (count nums)))

(defn- median [nums]
  {:pre [every? number? nums]}
  (let [c (count nums)
        sorted (vec (sort nums))]
    (if (odd? c)
      (get sorted (/ (dec c) 2))
      (mean (let [mid (/ c 2)]
              (subvec sorted (dec mid) (inc mid)))))))

(defn- mode [nums]
  {:pre [every? number? nums]}
  (->> nums
       frequencies
       (sort-by val)
       last
       key))

(defn stats-meta [& nums]
  {:pre [every? number? nums]}
  (with-meta
    nums
    {:mean (mean nums)
     :median (median nums)
     :mode (mode nums)}))

(defn blow-up []
  (throw (RuntimeException. "Kaboom")))
