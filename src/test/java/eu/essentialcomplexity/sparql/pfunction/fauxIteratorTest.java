package eu.essentialcomplexity.sparql.pfunction;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.apache.jena.query.Dataset;
import org.apache.jena.query.DatasetFactory;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Literal;

import org.junit.Test;

/**
 * Unit test for faux-itterator.
 */
public class fauxIteratorTest 
{
    /**
     * Test output of faux iterator (happy path).
     */
    @Test
    public void resultsShouldMatch()
    {
        Dataset dataset = DatasetFactory.createTxnMem();
        String query = "PREFIX f: <java:eu.essentialcomplexity.sparql.pfunction.> \n" +
        "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> \n" +
        "SELECT ?range ?offset \n" +
        "{ \n" +
        "  BIND(5 AS ?range) \n" +
        "  # Create a set {0, 1, 2, 3, ..., ?range - 1}\n" +
        "  ?range f:fauxIterator ?offset \n" +
        "} \n";

        List<Integer> result = new ArrayList<>();
        try (QueryExecution qe = QueryExecutionFactory.create(query, dataset)) {
            ResultSet rs = qe.execSelect();
            while (rs.hasNext()) {
                QuerySolution qs = rs.nextSolution();
                Literal feature = qs.getLiteral("offset");
                assertEquals("http://www.w3.org/2001/XMLSchema#int", feature.getDatatype().getURI());
                result.add(feature.getInt());
            }
        }

        List<Integer> expected = new ArrayList<>();
        expected.add(0);
        expected.add(1);
        expected.add(2);
        expected.add(3);
        expected.add(4);

        assertEquals(expected, result);
    }
}
