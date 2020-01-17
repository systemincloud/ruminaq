/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.model.diagram.impl.factories;

import java.util.Optional;
import java.util.WeakHashMap;

import org.eclipse.emf.common.util.ECollections;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.graphiti.mm.algorithms.AbstractText;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.algorithms.impl.MultiTextImpl;
import org.eclipse.graphiti.mm.algorithms.impl.RectangleImpl;
import org.eclipse.graphiti.mm.algorithms.styles.Color;
import org.eclipse.graphiti.mm.algorithms.styles.Font;
import org.eclipse.graphiti.mm.algorithms.styles.LineStyle;
import org.eclipse.graphiti.mm.algorithms.styles.Orientation;
import org.eclipse.graphiti.mm.algorithms.styles.StylesFactory;
import org.eclipse.graphiti.mm.algorithms.styles.StylesPackage;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.IGaService;
import org.eclipse.graphiti.ui.services.GraphitiUi;
import org.ruminaq.gui.model.diagram.LabelShape;
import org.ruminaq.gui.model.diagram.LabeledRuminaqShape;
import org.ruminaq.gui.model.diagram.impl.Colors;
import org.ruminaq.gui.model.diagram.impl.NoResource;
import org.ruminaq.model.ruminaq.BaseElement;

/**
 * Factory for creating label.
 *
 * @author Marek Jagielski
 */
public class LabelShapeFactory implements Factory {

  public static final LabelShapeFactory INSTANCE = new LabelShapeFactory();

  public static final Font FONT = StylesFactory.eINSTANCE.createFont();

  static {
    FONT.eSet(StylesPackage.eINSTANCE.getFont_Name(), IGaService.DEFAULT_FONT);
    FONT.eSet(StylesPackage.eINSTANCE.getFont_Size(),
        IGaService.DEFAULT_FONT_SIZE);
    FONT.eSet(StylesPackage.eINSTANCE.getFont_Italic(), Boolean.FALSE);
    FONT.eSet(StylesPackage.eINSTANCE.getFont_Bold(), Boolean.FALSE);
  }

  private static final int SHAPE_LABEL_SPACE = 2;

  private static final int TEXT_PADDING = 5;

  public static void placeLabelInDefaultPosition(LabelShape labelShape) {
    LabeledRuminaqShape labeledShape = labelShape.getLabeledShape();
    int labelShapeX = labeledShape.getX() - (labelShape.getWidth() >> 1)
        + (labeledShape.getWidth() >> 1);
    int labelShapeY = labeledShape.getY() + labeledShape.getHeight()
        + SHAPE_LABEL_SPACE;

    labelShape.setX(labelShapeX);
    labelShape.setY(labelShapeY);
  }

  public static class LabelShapeGA extends RectangleImpl {

    private EList<GraphicsAlgorithm> children;

    private LabelShape shape;

    private AbstractText text = new MultiTextImpl() {

      @Override
      public String getValue() {
        return Optional.ofNullable(shape.getLabeledShape())
            .map(LabeledRuminaqShape::getModelObject).map(BaseElement::getId)
            .orElse("");
      }

      @Override
      public Orientation getHorizontalAlignment() {
        return Orientation.ALIGNMENT_CENTER;
      }

      @Override
      public Orientation getVerticalAlignment() {
        return Orientation.ALIGNMENT_MIDDLE;
      }

      @Override
      public Boolean getFilled() {
        return false;
      }

      @Override
      public Font getFont() {
        return FONT;
      }

      @Override
      public Integer getLineWidth() {
        return 1;
      }

      @Override
      public LineStyle getLineStyle() {
        return LineStyle.SOLID;
      }

      @Override
      public Double getTransparency() {
        return 0D;
      }

      @Override
      public int getHeight() {
        return GraphitiUi.getUiLayoutService()
            .calculateTextSize(getValue(), FONT).getHeight() + TEXT_PADDING;
      }

      @Override
      public int getWidth() {
        return GraphitiUi.getUiLayoutService()
            .calculateTextSize(getValue(), FONT).getWidth() + TEXT_PADDING;
      }

      @Override
      public Color getBackground() {
        return Colors.WHITE;
      }

      @Override
      public Resource eResource() {
        return new NoResource();
      }
    };

    public LabelShapeGA(LabelShape shape) {
      this.shape = shape;
      this.children = ECollections.asEList(text);
    }

    @Override
    public int getX() {
      return shape.getX();
    }

    @Override
    public int getY() {
      return shape.getY();
    }

    @Override
    public void setX(int newX) {
      shape.setX(newX);
    }

    @Override
    public void setY(int newY) {
      shape.setY(newY);
    }

    @Override
    public int getWidth() {
      return text.getWidth();
    }

    @Override
    public int getHeight() {
      return text.getHeight();
    }

    @Override
    public Boolean getFilled() {
      return Boolean.FALSE;
    }

    @Override
    public Integer getLineWidth() {
      return 1;
    }

    @Override
    public LineStyle getLineStyle() {
      return LineStyle.SOLID;
    }

    @Override
    public Boolean getLineVisible() {
      return Boolean.FALSE;
    }

    @Override
    public Double getTransparency() {
      return 0D;
    }

    @Override
    public EList<GraphicsAlgorithm> getGraphicsAlgorithmChildren() {
      return children;
    }

    @Override
    public Resource eResource() {
      return new NoResource();
    }
  }

  private WeakHashMap<EObject, LabelShapeGA> cacheGraphicsAlgorithms = new WeakHashMap<>();

  @Override
  public boolean isForThisShape(Shape shape) {
    return LabelShape.class.isInstance(shape);
  }

  @Override
  public GraphicsAlgorithm getGA(Shape shape) {
    if (shape instanceof LabelShape) {
      if (!cacheGraphicsAlgorithms.containsKey(shape)) {
        cacheGraphicsAlgorithms.put(shape,
            new LabelShapeGA((LabelShape) shape));
      }
      return cacheGraphicsAlgorithms.get(shape);
    } else {
      return null;
    }
  }
}
