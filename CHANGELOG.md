# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [0.3.5] - 2022-02-09
### Cleaned up
- Various reformatting, removing obsolete methods and misc cleanups

## [0.3.4] - 2022-02-06
### Added
- Numbers.isIntegral on number type also checks for primitives number types.
- Numbers.isReal on number type also checks for primitives number types.

## [0.3.3] - 2021-04-30
### Added
- Convert.toType can convert numbers to dates.

## [0.3.2] - 2021-03-31
### Fixed
- fixed IllegalStateException due to a recursive update when updating component
  cache with the superclasses of an class in Dissector.

## [0.3.1] - 2021-03-22
### Added
- Added hex characters to generate random hexadecimal strings.

## [0.3.0] - 2021-03-13
### Added
- Made into Java 9 module (added module-info).

## [0.2.8] - 2021-02-17
### Added
- min and max functions over variable number of numeric values added to Numbers class.


