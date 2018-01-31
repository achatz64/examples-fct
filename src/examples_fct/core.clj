(ns examples-fct.core)

(require '[fct.core :as f])



;; forming expressions with variables

(def a (f/and (f/var* :x) (f/var* :y)))

(f/ev* a {:x true :y false})

;; combine

(def b (f/or a (f/and (f/var* :x) (f/var* :z))))

(f/ev* b {:x true :y false :z true})

;; any clojure.core function has a "lift" in fct.core so that it can be used on variables, to lift some other function

(def lift-my-not (f/lift* (fn my-not [x] (if x false true))))

(f/ev* (lift-my-not b) {:x true :y false :z true})

;; the most essential macros or special forms in clojure.core have a lift in fct.core, 

;; local bindings

(def d (f/let [{:keys [my]} (f/var* :x)] my))

(f/ev* d {:x {:my "project"}})

;; if is replaced by if-else 

(def e (f/if-else (f/var* :x)
                  "ok?"
                  (f/throw (Exception. "GL!"))))

(f/ev* e {:x true})

;; function constructions

(def f (f/fn [a] ((f/var* :x) a (f/var* :y))))

((f/ev* f {:x + :y -4}) 4)

;; for testing it's better to supply ranges for the variables, gen* generates a witness 

(def g (f/+ 5 (f/var* :x (f/rand-int 10))))

(f/gen* g)

;; ;; any fct expression can be used 

;; (f/gen* (f/var* :x (lift-my-not (f/rand-nth '(true false)))))

;; function construction allows for including generators (= an fct function without argument returning a vector) for the arguments

(def h (f/fn [a y] {:gen (f/fn [] (f/vector (f/rand-int 5) (f/rand-int 5)))}
         ((f/var* :x +) a y)))

(f/gcheck* h)



