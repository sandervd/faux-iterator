test: apache-jena/lib/faux-iterator-1.0-SNAPSHOT.jar test/test-query.rq
	./apache-jena/bin/sparql --query=test/test-query.rq

example: apache-jena/lib/faux-iterator-1.0-SNAPSHOT.jar test/example.rq
	./apache-jena/bin/sparql --query=test/example.rq

apache-jena/lib/faux-iterator-1.0-SNAPSHOT.jar: src/main/java/eu/essentialcomplexity/sparql/pfunction/fauxIterator.java pom.xml apache-jena
	mvn package
	cp target/faux-iterator-1.0-SNAPSHOT.jar apache-jena/lib


apache-jena:
	./bin/download-jena.sh
clean:
	rm -rf target/
.PHONY: test
