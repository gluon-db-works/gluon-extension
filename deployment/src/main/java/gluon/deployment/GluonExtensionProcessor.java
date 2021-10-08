package gluon.deployment;

import gluon.runtime.annotations.SQL;
import gluon.runtime.utils.ISayHello;
import io.quarkus.arc.deployment.GeneratedBeanBuildItem;
import io.quarkus.arc.deployment.GeneratedBeanGizmoAdaptor;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.CombinedIndexBuildItem;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.gizmo.*;
import org.jboss.jandex.AnnotationInstance;
import org.jboss.jandex.AnnotationTarget;
import org.jboss.jandex.DotName;
import org.jboss.jandex.IndexView;
import org.objectweb.asm.Opcodes;

import javax.inject.Singleton;
import java.util.logging.Logger;

// SEE: https://quarkus.io/guides/writing-extensions#build-step-processors
// SEE: https://github.com/quarkusio/gizmo/blob/main/src/test/java/io/quarkus/gizmo/VariableAssignmentTestCase.java
// SEE: https://quarkus.io/guides/cdi-integration

public class GluonExtensionProcessor {

    private static final String FEATURE = "gluon-extension";

    private static final Logger LOG = Logger.getLogger("GluonExtensionProcessor");

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }

    @BuildStep
    void generatedBean(CombinedIndexBuildItem combinedIndexBuildItem, BuildProducer<GeneratedBeanBuildItem> generatedBeans) {
        ClassOutput beansClassOutput = new GeneratedBeanGizmoAdaptor(generatedBeans);

        try (var classCreator = ClassCreator.builder().classOutput(beansClassOutput)
                .className("gluon.impl.SayHelloImpl")
                .interfaces(ISayHello.class)
                .build()) {
            classCreator.addAnnotation(Singleton.class);
            MethodCreator method = classCreator.getMethodCreator("sayHello", String.class).setModifiers(Opcodes.ACC_PUBLIC);
            AssignableResultHandle val = method.createVariable(String.class);
            method.assign(val, method.load("HELLO!"));
            // ResultHandle ret = method.readStaticField(FieldDescriptor.of(String.class, "MAX_VALUE", int.class));
            method.returnValue(val);
        }

        DotName SQL_REPOSITORY = DotName.createSimple(SQL.Repository.class.getName());
        IndexView index = combinedIndexBuildItem.getIndex();
        for (AnnotationInstance deserializeInstance : index.getAnnotations(SQL_REPOSITORY)) {
            AnnotationTarget annotationTarget = deserializeInstance.target();
            if (AnnotationTarget.Kind.CLASS .equals(annotationTarget.kind())) {
                DotName dotName = annotationTarget.asClass().name();
                LOG.info(dotName + " is class");
            } else if (AnnotationTarget.Kind.TYPE.equals(annotationTarget.kind())) {
                var targetType = annotationTarget.asType().toString();
                LOG.info(targetType + " is type");
            } else {
                var kind = annotationTarget.kind();
                var target = annotationTarget.toString();
                LOG.info(target + " has kind " + kind);
            }
        }
    }

}
