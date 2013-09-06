package checkers.subtyping;

import checkers.basetype.BaseTypeChecker;
import checkers.types.BasicAnnotatedTypeFactory;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.processing.SupportedOptions;

import com.sun.source.tree.CompilationUnitTree;

/**
 * A checker for type qualifier systems that only checks subtyping
 * relationships.
 *
 * <p>
 *
 * The annotation(s) are specified on the command line, using an annotation
 * processor argument:
 *
 * <ul>
 * <li>{@code -Aquals}: specifies the annotations in the qualifier hierarchy
 * (as a comma-separated list of fully-qualified annotation names with no
 * spaces in between).  Only the annotation for one qualified subtype
 * hierarchy can be passed.</li>
 * </ul>
 *
 * @checker.framework.manual #basic-checker Basic Checker
 */
@SupportedOptions( { "quals" })
public final class SubtypingChecker extends BaseTypeChecker<BasicAnnotatedTypeFactory<SubtypingChecker>> {

    @Override
    public BasicAnnotatedTypeFactory<SubtypingChecker> createFactory(CompilationUnitTree root) {
        return new BasicAnnotatedTypeFactory<SubtypingChecker>(this, root);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected Set<Class<? extends Annotation>> createSupportedTypeQualifiers() {

        String qualNames = getOption("quals");
        if (qualNames == null) {
            errorAbort("SubtypingChecker: missing required option: -Aquals");
        }

        Set<Class<? extends Annotation>> qualSet =
            new HashSet<Class<? extends Annotation>>();
        for (String qualName : qualNames.split(",")) {
            try {
                final Class<? extends Annotation> q =
                    (Class<? extends Annotation>)Class.forName(qualName);
                qualSet.add(q);
            } catch (ClassNotFoundException e) {
                errorAbort("SubtypingChecker: could not load class for qualifier: " + qualName + "; ensure that your classpath is correct.");
            }
        }

        return Collections.unmodifiableSet(qualSet);
    }

    @Override
    public Collection<String> getSuppressWarningsKeys() {
        Set<String> swKeys = new HashSet<String>();
        Set<Class<? extends Annotation>> annos = getSupportedTypeQualifiers();
        if (annos.isEmpty())
            return super.getSuppressWarningsKeys();

        for (Class<? extends Annotation> anno : annos)
            swKeys.add(anno.getSimpleName().toLowerCase());

        return swKeys;
    }
}