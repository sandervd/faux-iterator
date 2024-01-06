package eu.essentialcomplexity.sparql.pfunction;

import java.util.stream.IntStream;
import java.util.Iterator ;

import org.apache.jena.atlas.iterator.Iter;
import org.apache.jena.atlas.lib.Lib ;
import org.apache.jena.atlas.logging.Log ;
import org.apache.jena.datatypes.RDFDatatype;
import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.graph.Node ;
import org.apache.jena.graph.NodeFactory ;
import org.apache.jena.query.QueryBuildException ;
import org.apache.jena.query.QueryException ;
import org.apache.jena.sparql.core.Var ;
import org.apache.jena.sparql.engine.ExecutionContext ;
import org.apache.jena.sparql.engine.QueryIterator ;
import org.apache.jena.sparql.engine.binding.Binding ;
import org.apache.jena.sparql.engine.binding.BindingFactory;
import org.apache.jena.sparql.engine.iterator.QueryIterPlainWrapper;
import org.apache.jena.sparql.pfunction.PropFuncArg ;
import org.apache.jena.sparql.pfunction.PropFuncArgType ;
import org.apache.jena.sparql.pfunction.PropertyFunctionEval ;
import org.apache.jena.sparql.util.IterLib ;



public class fauxIterator extends PropertyFunctionEval
{
    public fauxIterator()
    {
        super(PropFuncArgType.PF_ARG_SINGLE, PropFuncArgType.PF_ARG_LIST) ;
    }

    @Override
    public void build(PropFuncArg argSubject, Node predicate, PropFuncArg argObject, ExecutionContext execCxt)
    {
        // Do some checking.
        // These checks are assumed to be passed in .exec()
        if ( argSubject.isList() )
            throw new QueryBuildException(Lib.className(this)+ "Subject must be a single node or variable, not a list") ;
    }

    @Override
    public QueryIterator execEvaluated(Binding binding, PropFuncArg argSubject, Node predicate, PropFuncArg argObject, ExecutionContext execCxt)
    {
        try 
        {
            Node subject = argSubject.getArg();
            // Object must be unbound variable
            if ( !argObject.getArg().isVariable() )
            {
                Log.warn(this, "Object to property function faux-iterator must be an unbound variable.") ;
                return IterLib.noResults(execCxt) ;
            }
            // Subject bound to literal.
            if ( !subject.isLiteral())
            {
                Log.warn(this, "Subject to property function faux-iterator is not a literal.") ;
                return IterLib.noResults(execCxt) ;
            }
            // Subject bound to integer.
            RDFDatatype type = subject.getLiteralDatatype();
            if ( !type.getURI().equals("http://www.w3.org/2001/XMLSchema#integer") )
            {
                Log.warn(this, "Subject to property function faux-iterator is not a number : type= " + type.getURI()) ;
                return IterLib.noResults(execCxt) ;
            }
            
            // Subject is an integer
            return getNumericRangeList(subject, argObject, binding, execCxt) ;
        } catch (QueryException ex)
        {
            Log.warn(this, "Unexpected problems in faux-iterator: " + ex.getMessage()) ;
            return null ;
        }
    }

    /**
     * Turns an integer into a range of integers (e.g. 4 -> {0, 1, 2, 3}).
     *
     * @param subject
     * @param argObject
     * @param binding
     * @param execCxt
     * @return
     */
    private QueryIterator getNumericRangeList(Node subject, PropFuncArg argObject, Binding binding, ExecutionContext execCxt)
    {
        Integer range = (Integer) subject.getLiteralValue();
        IntStream stRange = IntStream.range(0, range);
        final Var objectVar = Var.alloc(argObject.getArg());

        Iterator<Binding> it = Iter.map(
                    stRange.iterator(),
                    item -> BindingFactory.binding(binding, objectVar, NodeFactory.createLiteralByValue(item, XSDDatatype.XSDint)));
        return QueryIterPlainWrapper.create(it, execCxt);
    }
}
