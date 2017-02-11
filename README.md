# Specs through Destructuring (Spede) 

The aim is to provide a defn-replacing macro that additionally creates a matching
spec that automatically includes any destructured forms in the parameter list.


## Usage
```clojure
(s/def ::a integer?)

(spede/sdefn my-func [{a ::a}]
  (+ a a))
          
  -> defn + (s/fdef my-func (s/cat :firstarg (s/keys :req [::a])))
  ```

## License

Copyright © 2017 Vincit

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
