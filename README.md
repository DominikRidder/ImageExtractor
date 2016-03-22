ImageExtractor
==============


What is it?
-----------

The ImageExtractor provides a gui for sorting, viewing and analyse
NIFTI's and DICOMS files.


The Latest Version
------------------

You can find the newest version under
https://github.com/DominikRidder/ImageExtractor


Requirements
------------
- JAVA JRE (1.7 or higher) or JDK (for development)
- ant (for development / building from source)
- anything else?

Installation
------------

The installation is done, when the ImageExtrator.jar file is created.
In case, that the ImageExtractor.jar under dist/ does not exist or
if this jar seems not up to date, you can follow these steps:

```bash
$ cd <project top level folder> 
$ ant jar
```

The jar is than created in the dist/ folder. From there you can put 
it to any location you like. Any needed libary is packed with in the
jar file, to be undependend from the location of the jar.

MacOS installation note
---------

In order to build the executable (jar), in MacOS (10.9 or higher) you need to install
'ant' first. Easiest way is by 'brew' (see brew.sh):

/usr/bin/ruby -e "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/master/install)"

And then:

brew update
brew install ant

Licensing
---------

Please see the file called LICENSE.txt
