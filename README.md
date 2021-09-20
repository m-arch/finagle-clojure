# finagle-clojure [![Build Status](https://travis-ci.org/finagle/finagle-clojure.svg?branch=master)](https://travis-ci.org/finagle/finagle-clojure)

A thin wrapper around Finagle & Twitter Future.
This library assumes you are familiar with Finagle.
If not, check out its [docs](https://twitter.github.io/finagle/guide/).

# Note 

This fork of the main repo is not stable, it's an updated replacement of the original repo that might break under some circumstences.
To install you need to clone the repo and run lein install locally for sub directories first starting by core and then for other directories.

Latest version: 0.8.0-SNAPSHOT

## Building

    lein sub -s "lein-finagle-clojure:finagle-clojure-template:core:thrift:http:mysql:thriftmux" install


## Running Tests

    lein sub midje


## Libraries

The readmes in each sub-library have more information.

* `core`: convenience fns for interacting with Futures.
* `thrift`: create Thrift clients & servers.
* `thriftmux`: create ThriftMux clients & servers.
* `http`: create HTTP servers, requests, and responses.
* `mysql`: a fully featured asynchronous MySQL client.
* `lein-finagle-clojure`: a lein plugin for automatically compiling Thrift definitions using [Scrooge](https://twitter.github.io/scrooge/index.html).
* `finagle-clojure-template`: a lein template for creating new projects using finagle-clojure & Thrift.


## Create a new project with Thrift

    lein new finagle-clojure $PROJECT-NAME

Then check out the readmes in the generated project.


## Documentation

* [Quick Start with finagle-clojure & Thrift](doc/quick-start.md)
* [API Docs](https://finagle.github.io/finagle-clojure/)
  * run `lein doc` from this directory to generate
* Finagle Docs
  * [User's Guide](https://twitter.github.io/finagle/guide/)
  * [API Docs (scala)](https://twitter.github.io/finagle/docs/#com.twitter.finagle.package)
  * [GitHub](https://github.com/twitter/finagle)
