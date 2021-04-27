# Single Page App demo in Clojure, Ring, Compojure and Reagent/Inertia.js

Small example to demonstrate the use of [Inertia-Clojure](https://github.com/prestancedesign/inertia-clojure) + [Inertia.js](https://inertiajs.com/).
I forked the [usermanager-example](https://github.com/seancorfield/usermanager-example) repository, a back-end web application and a few basic modifications are enough to
make it a Single Page Application.

The diff of what it took to change the app to SPA (for the back-end): [commit/9e4bfb86d610d7467ef506981da68ec597bd31f3](https://github.com/prestancedesign/usermanager-reagent-inertia-example/commit/9e4bfb86d610d7467ef506981da68ec597bd31f3)

## Usage

Clone the repo, `cd` into it, then follow below to _Run the Application_ or _Run the application in REPL_

### Run the Application
```
clj -M -m usermanager.main
```

It should create a SQLite database (`usermanager_db`) and populate two tables (`department` and `addressbook`) and start a Jetty instance on port 8080.

If that port is in use, start it on a different port. For example, port 8100:

```
clj -M -m usermanager.main 8100
```

### Run the Application in REPL

Start REPL

```
$ clj
```

Once REPL starts, start the server as an example on port 8888:

```clj
user=> (require 'usermanager.main)                             ; load the code
user=> (in-ns 'usermanager.main)                               ; move to the namesapce
usermanager.main=> (def system (new-system 8888))              ; specify port
usermanager.main=> (alter-var-root #'system component/start)   ; start the server
```
