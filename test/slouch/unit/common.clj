(ns slouch.unit.common
  (:require [slouch.common]
            [midje.sweet :refer :all]))

(fact
  slouch.common/response-codes => {:success 200
                                   :not-found 404
                                   :exception 500})
