# Couscous

## Status

Early Alpha, this project is not initially intended for release but
more as means of personal organization yet anyone finding it useful
may take of course.

## Purpose

Couscous is a collection of functionality which enhances several aspects
of Korma and MySQL which I require for data analysis, fun and profit.
Also this should become a MySQL->Datomic(via ADI) mapper.

## Etymology

The name is a pun on Korma, which it largely utilizes in order to achieve
certain tastiness. Couscous is a traditional Berber dish of semolina which
is cooked by steaming. It is traditionally served with a meat or vegetable
stew spooned over it

## Functionality

Currently a great deal of convenience has been made available through the
use of protocols which greatly enhances Korma's ability to handle different
naming conventions between systems. Some examples:

```
(hyphenate "foo-bar_baz-bal") ; :foo-bar-baz-bal
(upperscore :my-little_POnY)  ; "MY_LITTLE_PONY"
(underscore ["HELLO-WORLD" :goodbye-world "HELLO_REINCARTNATION"])
; ["hello_world" "goodbye_world" "hello_reincarnation"]
(columnify :some-table-name "AND_FIELD_ID") ; "`some_table_name`.`and_field_id`"
```

As you can see, a broad range of variation is allowed to be transformed
the extension of native Clojure and Java types by means of protocol.



## Usage

FIXME

## License

Copyright © 2014 FIXME

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
