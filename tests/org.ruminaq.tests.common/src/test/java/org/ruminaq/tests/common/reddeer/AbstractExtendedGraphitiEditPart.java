package org.ruminaq.tests.common.reddeer;

import java.lang.reflect.Method;
import java.util.AbstractMap.SimpleEntry;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.gef.EditPart;
import org.eclipse.graphiti.features.context.ISingleClickContext;
import org.eclipse.graphiti.features.custom.ICustomFeature;
import org.eclipse.graphiti.ui.editor.DiagramEditor;
import org.eclipse.reddeer.common.util.Display;
import org.eclipse.reddeer.common.util.ResultRunnable;
import org.eclipse.reddeer.graphiti.GraphitiLayerException;
import org.eclipse.reddeer.graphiti.handler.GraphitiEditPartHandler;
import org.eclipse.reddeer.graphiti.impl.graphitieditpart.AbstractGraphitiEditPart;
import org.eclipse.reddeer.graphiti.lookup.DiagramEditorLookup;
import org.hamcrest.Matcher;
import org.ruminaq.util.Result;

public abstract class AbstractExtendedGraphitiEditPart
    extends AbstractGraphitiEditPart {

  public AbstractExtendedGraphitiEditPart(EditPart editPart) {
    super(editPart);
  }

  public AbstractExtendedGraphitiEditPart(Matcher<EditPart> matcher,
      int index) {
    super(matcher, index);
  }

  public void singleClick() {
    DiagramEditor diagramEditor = DiagramEditorLookup.getInstance()
        .findDiagramEditor();

    Optional<Method> createPictogramContext = Optional
        .of(GraphitiEditPartHandler.class)
        .map(c -> Result.attempt(() -> c
            .getDeclaredMethod("createPictogramContext", EditPart.class)))
        .flatMap(r -> Optional.ofNullable(r.orElse(null)));
    createPictogramContext.ifPresent(m -> m.setAccessible(true));
    Optional<ISingleClickContext> singleClickCtx = createPictogramContext
        .map(m -> Result.attempt(
            () -> m.invoke(GraphitiEditPartHandler.getInstance(), editPart)))
        .flatMap(r -> Optional.ofNullable(r.orElse(null)))
        .filter(ISingleClickContext.class::isInstance)
        .map(ISingleClickContext.class::cast);

    if (singleClickCtx.isPresent()) {
      List<ICustomFeature> features = Display
          .syncExec((ResultRunnable<List<ICustomFeature>>) () -> Stream
              .of(diagramEditor.getDiagramTypeProvider()
                  .getAvailableToolBehaviorProviders())
              .map(t -> new SimpleEntry<>(t, singleClickCtx.get()))
              .map(se -> se.getKey().getSingleClickFeature(se.getValue()))
              .collect(Collectors.toList()));

      if (features.isEmpty()) {
        throw new GraphitiLayerException("Cannot call single click");
      }
      features.stream()
          .forEach(feature -> Display.getDisplay().asyncExec(() -> {
            singleClickCtx.ifPresent(feature::execute);
          }));
      Display.getDisplay().syncExec(() -> {
      });
    }
  }

}
