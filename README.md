# Specs through Destructuring (Spede) 

The aim is to provide a defn-replacing macro that automatically creates a matching
spec that includes any destructured forms in the parameter list.

## Usage
```clojure
(s/def ::a valid-data?) ; you still spec your keywords normally

(spede/sdefn my-func [{a ::a}]
  (+ a a))
          
  -> (defn my-func ...)
  -> (s/fdef my-func :args (s/cat :firstarg (s/keys :req [::a])))
```
  
You can also pass fdef arguments before the argument list. The :args will be
combined with the generated spec using clojure.spec/and, others are passed
unaltered.

TLDR:

```clojure
(spede/sdefn my-func
  "my cool function"
  :args my-additional-arg-predicate?
  :fn my-fn-predicate?
  :ret my-ret-predicate?
  [a b c]
  (+ a b c))
  
  -> (defn my-func ...)
  -> (s/fdef my-func :args (s/and my-additional-arg-predicate? (s/cat ...)) :fn ... :ret ...)
```

You can also specify the spec of a symbol in the binding form. A single predicate
has to be put in a (s/def) call to give it a namespace keyword.

```clojure
(s/def ::a valid-data?)

(spede/sdefn my-func [a ::a]
  (+ a a))

  -> (defn my-func ...)
  -> (s/fdef my-func :args (s/cat :a ::a))
```

## License

Copyright © 2017 Vincit

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
