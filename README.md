# Base data structures, algorithms and utility function
This project contains a set of base data structures, algorithms and utility functions 
which are used in several other projects. These components have been developed to be
generic and of wide applicability. When an implementation of a component in this project 
has subsequently become available in the JDK, it should be deprecated in favour of the JDK 
implementation.

## Install
To use through Maven, include the following dependency in your pom.xml file:
 
    <dependency>
      <groupId>com.vikmad</groupId>
      <artifactId>base</artifactId>
      <version>0.2.7</version>
    </dependency>
    
or in Gradle, add the following line to your dependencies list in your build.gradle file:

    compile "com.vikmad:base:0.2.7"
    
## Components and example usage
Following are overview of the some of the components in this project. More information
is available in the javadocs.

### Fast and simple streaming-read of XML files

The `ma.vi.base.xml.XmlReader` class provides a simplified iterator interface to a streaming XML
parser with support for backtracking an arbitrary number of steps, specified in the constructor 
or, better, through `ma.vi.base.xml.XmlReaderBuilder`. `XmlReader` is an iterator of `Fragment` with
each `Fragment` carrying information on its type (e.g. element start, element end, comment, text, etc.)
and content. Example use:

    XmlReader xml = XmlReaderBuilder.newBuilder(new StringReader(xmlText))
                         .discardInterElementSpaces(false)   // do not discard spaces between elements
                         .discardComments(false)             // do not discard comments
                         .rewindCapacity(10)                 // allow rewinding up to 10 positions back
                         .build();
    for (Fragment fragment: xml) {
        // do something with the fragment
    }
    
### Tuples
`ma.vi.base.tuple.T1` to `ma.vi.base.tuple.T5` are one to five elements tuples inheriting from a common
interface and intended as a convenience when a function need to return several values. Tuples with more
components can be created by subclassing the `AbstractTuple` class or implementing the `Tuple` interface.

### Tries
`ma.vi.base.trie.Trie` is a generic Trie structure which can be used to implement tries with any sequence
types. The `StringTrie` subclass implements the classic tries on strings and the `PathTrie` subclass extends
this concept to paths which are sequence of path elements (files and folders).
 
### Union-Find
`ma.vi.base.unionfind.UnionFind` is an extended implementation of the union-find structure backed by as hashmap,
implementing path-compression, and allowing for arbitrary values to be associated to each disjoint set. Operations 
to find and merge sets are accomplished in amortized constant time in this structure.  

### String escaping and utilities
`ma.vi.base.string.Escape` can be used to find, escape and unescape certain characters in strings which is 
useful when parsing certain arbitrary character sequences. It works by temporarily mapping a set of special characters 
to the unicode private area and remapping them back to unescape. 

`ma.vi.base.string.Strings` contains static functions to generate random strings and to manipulate strings.

`ma.vi.base.crypt.Obfuscator` can obfuscate phrases which need to be stored or transmitted in some form other than
plain text. This is not an encryption function as it does not use a key; it merely manipulates the characters in the
text and inject certain random characters to obscure the original phrase in a manner that it can be revealed with
a reverse function.

### Reflection
`ma.vi.base.reflect.Dissector` uses Java reflection to provide access to the different elements of a class. 
`ma.vi.base.reflect.Property` is an abstraction of fields and properties (get and set accessors) allowing
their values to be read and written irrespective of how they are implemented (e.g., as private field with or without
accessors).

### Collections
`ma.vi.base.collections.DiskBasedCollection` is an ordered collection of objects that is transparently stored
on disk allowing it to grow to huge size with minimal impact on RAM. The objects are stored in two files on disk,
with an index file containing pointers to a data file. The index file is structured as a self-balancing AA tree
and both files are accessed through `ma.vi.base.cache.CachedRandomAccessFile` which optimizes read and write
access to random-access files.

The `ma.vi.base.collections` package contains other classes to facilitate working with maps, sets, arrays, etc. 


