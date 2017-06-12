# Overview #
people-loader is a simple little program which generates fake people
entries with plausible names and addresses, and loads them into a
gemfire cluster.  It is useful for demos and smoke tests.

It uses pdx serialization.  The key is an Integer unless `--partition-by-zip`
is passed in which case the key is a custom class which implements
PartitionResolver based on the zip code.  Objects are inserted in batches
of 100 using _region.putAll_.

# Prerequisites #
- A running GemFire 9.0.4+ cluster with a region defined.
- java 1.8 runtime
- python2 or python3 (only needed for the convenience script)

# Usage Examples#

* Put 10 randomly generated Person entries into the default region (_Person_)
```
python peopleloader.py  --locator=host[port]  --count=10
```

* Put 10 randomly generated Person entries into a region called _Customer_
```
python peopleloader.py  --locator=host[port]  --count=10 --region=Customer
```

* Use 10 threads to put 10,000 randomly generated Person entries into a the
_Person_ region.
```
python peopleloader.py  --locator=host[port]  --count=10000 --threads=10000
```

* Use 10 threads to put 10,000 randomly generated Person entries into a the
_Person_ region but sleep 500ms after each _put_
```
python peopleloader.py  --locator=host[port]  --count=10000 --threads=10 --sleep=500
```
* Put 10 randomly generated Person entries into the default region (_Person_).
Use a custom key so the entries will be partitioned by zip code.
```
python peopleloader.py  --locator=host[port]  --count=10 --partition-by-zip
```
