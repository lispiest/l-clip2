### [Configure Local Storage](https://docs.datomic.com/datomic-local.html#storage-dir)
```bash
~ $ cd
~ $ mkdir -p ~/.datomic
~ $ echo "{:storage-dir "$PWD"}" > ~/.datomic/local.edn
~ $ mkdir -p ~/db
```
### PLAY (REPL)
```bash
$ clj -M:dev
```
```clj
user=> (reset)
:reloading (l-clip2.core user)
:started
```
### -main
```bash
$ clojure -M:run
Starting up on port 8090
```
### URL
```
http://localhost:8090
```
