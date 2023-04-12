# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [0.5.4] - 2023-04-12
### Added
- Functions `date`, `time` and `now` returning the current date, the current time and 
  the current date-time, respectively, added to `Dates` class.

## [0.5.3] - 2023-02-09
### Added
- `round` method added to `Numbers` to round doubles up to some number of fractional
  digits.

## [0.5.2] - 2023-02-08
### Added
- Duration methods `daysBetween`, `monthsBetween` and `yearsBetween` added to `Dates`.
- `format` methods with and without default formatting pattern added to `Dates`.

## [0.5.1] - 2023-02-07
### Added
- Polymorphic `LocalDate` comparison functions added to utility class `Dates`. 
  Any of the parameter to these functions can be a `LocalDate` or a `String`; the
  latter will be parsed to a `LocalDate` prior to the comparison.
- `Values` utility class containing general functions for working with values.
- `coalesce` function in `Values` returns first non-null value in the parameter
  list.

## [0.5.0] - 2022-06-16
### Added
- `Dates` and `Convert` moved from old `Date` classes to Java 8 time library 
  including use of `LocalDate`, `LocalTime`, `LocalDateTime` and supporting classes.

## [0.4.5] - 2022-06-08
### Added
- Conversion of string to UUID type.

## [0.4.4] - 2022-05-25
### Added
- `clear` method to `Trie` to clear all entries.
- `deletePrefixed` method taking an `Iterator` in `Trie`.

## [0.4.3] - 2022-05-23
### Added
- Exact conversion of numeric value to esql numeric types.

### Fixed
- Fixed `toHex` method used by `sha256` in `Hashing` which had a bug where single
  digit hex values were not being left-padded with a zero.
- Corrected the 2-parameters method `makeUniqueSeq` in `Strings` which should 
  delegate to the 3-parameters version of method `makeUniqueSeq` but was instead
  delegating to the method `makeUnique`. The latter uses a random string suffix
  to make names unique instead of sequence number. 

## [0.4.2] - 2022-03-22
### Added
- `makeUniqueSeq` creates unique names by adding a sequential prefix (instead of 
  a random suffix).
- Method `remove` to remove a key from `Configuration`.

## [0.4.1] - 2022-02-26
### Added
- `isReadable` and `isWritable` methods added to dissector `Property`.
- Dissection of Java records into properties.
- Final fields are now allowed as properties.

## [0.4.0] - 2022-02-17
### Added
- Configuration now encapsulates its map of values instead of inheriting from 
  HashMap. As such it has better control over what clients can do with the 
  underlying map values. Further, specific map implementations can be provided
  on construction (e.g. readonly maps, concurrent, etc.) and the configuration
  will inherit their properties.
- `has` method tests existence of parameter in `Configuration`. 
- Move to Java 17.

## [0.3.6] - 2022-02-11
### Added
- Configuration can now be built from a map of string to object.
- Configuration.param method returns the parameter value casting to the expected 
  type, returning a default value if the parameter does not exist in the configuration.

## [0.3.5] - 2022-02-09
### Cleaned up
- Various reformatting, removing obsolete methods and misc cleanups.

## [0.3.4] - 2022-02-06
### Added
- Numbers.isIntegral on number type also checks for primitives number types.
- Numbers.isReal on number type also checks for primitives number types.

## [0.3.3] - 2021-04-30
### Added
- Convert.toType can convert numbers to dates.

## [0.3.2] - 2021-03-31
### Fixed
- Fixed IllegalStateException due to a recursive update when updating component
  cache with the superclasses of a class in Dissector.

## [0.3.1] - 2021-03-22
### Added
- Added hex characters to generate random hexadecimal strings.

## [0.3.0] - 2021-03-13
### Added
- Made into Java 9 module (added module-info).

## [0.2.8] - 2021-02-17
### Added
- `min` and `max` functions over variable number of numeric values added to Numbers 
  class.


