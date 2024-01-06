# Apache Jena Faux-Iterator
This SPARQL function offers a workaround to SPARQL lack of loops and iterators.
The setup is simple: an integer value is expected in the subject position, and a range gets bound to the variable in the object position.
For example:
```
PREFIX f: <java:eu.essentialcomplexity.sparql.pfunction.>
PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>
SELECT ?range ?offset
{
  BIND(5 AS ?range)
  # Create a set {0, 1, 2, 3, ..., ?range - 1}
  ?range f:fauxIterator ?offset
}
```

Results in the following output:
```
------------------------
| range | offset       |
========================
| 5     | "0"^^xsd:int |
| 5     | "1"^^xsd:int |
| 5     | "2"^^xsd:int |
| 5     | "3"^^xsd:int |
| 5     | "4"^^xsd:int |
------------------------
```

## Setup
Build the jar:
```
mvn package
```
Copy the resulting package to the 'lib' folder of your Jena installation.

Alternatively, use make to download Jena and build the project.

## Example usage
See [test/test-query.rq](test/test-query.rq).
