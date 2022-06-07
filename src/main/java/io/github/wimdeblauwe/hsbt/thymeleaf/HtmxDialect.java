package io.github.wimdeblauwe.hsbt.thymeleaf;

import io.github.wimdeblauwe.hsbt.mvc.HtmxSpringStandardExressionObjectFactory;
import org.thymeleaf.dialect.AbstractProcessorDialect;
import org.thymeleaf.dialect.IExpressionObjectDialect;
import org.thymeleaf.expression.IExpressionObjectFactory;
import org.thymeleaf.processor.IProcessor;

import java.util.HashSet;
import java.util.Set;

public class HtmxDialect extends AbstractProcessorDialect implements IExpressionObjectDialect {

    private HtmxSpringStandardExressionObjectFactory expressionObjectFactory;

    public HtmxDialect() {
        super("Htmx", "hx", 1000);
    }

    @Override
    public Set<IProcessor> getProcessors(String dialectPrefix) {
        Set<IProcessor> htmxProcessors = new HashSet<>();

        htmxProcessors.add(new HtmxAttributeProcessor(dialectPrefix, "boost"));
        htmxProcessors.add(new HtmxAttributeProcessor(dialectPrefix, "confirm"));
        htmxProcessors.add(new HtmxAttributeProcessor(dialectPrefix, "delete"));
        htmxProcessors.add(new HtmxAttributeProcessor(dialectPrefix, "disable"));
        htmxProcessors.add(new HtmxAttributeProcessor(dialectPrefix, "disinherit"));
        htmxProcessors.add(new HtmxAttributeProcessor(dialectPrefix, "encoding"));
        htmxProcessors.add(new HtmxAttributeProcessor(dialectPrefix, "ext"));
        htmxProcessors.add(new HtmxAttributeProcessor(dialectPrefix, "get"));
        htmxProcessors.add(new HtmxAttributeProcessor(dialectPrefix, "headers"));
        htmxProcessors.add(new HtmxAttributeProcessor(dialectPrefix, "history-elt"));
        htmxProcessors.add(new HtmxAttributeProcessor(dialectPrefix, "include"));
        htmxProcessors.add(new HtmxAttributeProcessor(dialectPrefix, "indicator"));
        htmxProcessors.add(new HtmxAttributeProcessor(dialectPrefix, "params"));
        htmxProcessors.add(new HtmxAttributeProcessor(dialectPrefix, "patch"));
        htmxProcessors.add(new HtmxAttributeProcessor(dialectPrefix, "post"));
        htmxProcessors.add(new HtmxAttributeProcessor(dialectPrefix, "preserve"));
        htmxProcessors.add(new HtmxAttributeProcessor(dialectPrefix, "prompt"));
        htmxProcessors.add(new HtmxAttributeProcessor(dialectPrefix, "put"));
        htmxProcessors.add(new HtmxAttributeProcessor(dialectPrefix, "push-url"));
        htmxProcessors.add(new HtmxAttributeProcessor(dialectPrefix, "request"));
        htmxProcessors.add(new HtmxAttributeProcessor(dialectPrefix, "select"));
        htmxProcessors.add(new HtmxAttributeProcessor(dialectPrefix, "swap"));
        htmxProcessors.add(new HtmxAttributeProcessor(dialectPrefix, "swap-oob"));
        htmxProcessors.add(new HtmxAttributeProcessor(dialectPrefix, "sync"));
        htmxProcessors.add(new HtmxAttributeProcessor(dialectPrefix, "target"));
        htmxProcessors.add(new HtmxAttributeProcessor(dialectPrefix, "trigger"));
        htmxProcessors.add(new HtmxAttributeProcessor(dialectPrefix, "vals"));
        htmxProcessors.add(new HtmxAttributeProcessor(dialectPrefix, "vars"));

        return htmxProcessors;
    }

    @Override
    public IExpressionObjectFactory getExpressionObjectFactory() {
        if (this.expressionObjectFactory == null) {
            this.expressionObjectFactory = new HtmxSpringStandardExressionObjectFactory();
        }
        return this.expressionObjectFactory;
    }
}
