# Zeta Programming language
Zeta `0.1` is an imperative programming language made for educational purposes that supports Object Orientation and closures among other modern features.
* Zeta is dynamically typed
* Zeta has tight lexical scoping
###  How code looks like
~~~~
// A counter
fn makeCounter() {
	let count = 0;
	fn increment() {
	count = count + 1;
	print count;
	}
	return increment;
}
let counter = makeCounter();
counter(); // prints 1
counter(); // prints 2
counter(); // prints 3
~~~~  

## Installation
The `out` directory contains class files with `Zeta` as the main class.
`buildlinux.sh` is a loose script written for Linux systems : 
* `$ git clone https://github.com/mirimmad/zeta-lang.git`
* `$ cd zeta-lang`
* `$ [sudo] sh buildlinux.sh`

## TO-DO
* Library Support - Zeta currently does not support external libraries.
* Replacement of Tree Walking interpreter by a VM.
* Optimizations .
### Contact
[Twitter](http://twitter.com/1madmir)

