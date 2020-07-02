/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/
package org.ruminaq.gui.model.diagram.impl.label;

import java.util.Optional;

import org.eclipse.emf.common.util.ECollections;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.algorithms.impl.RectangleImpl;
import org.eclipse.graphiti.mm.algorithms.styles.LineStyle;
import org.ruminaq.gui.model.diagram.LabelShape;
import org.ruminaq.gui.model.diagram.LabeledRuminaqShape;
import org.ruminaq.gui.model.diagram.impl.NoResource;

/**
 * GraphicsAlgorithm for Label.
 *
 * @author Marek Jagielski
 */
public class LabelShapeGA extends RectangleImpl {

  private EList<GraphicsAlgorithm> children;

  private LabelShape shape;

  private Text text;

  /**
   * GraphicsAlgorithm for Label.
   *
   * @param shape LabelShape
   */
  public LabelShapeGA(LabelShape shape) {
    this.shape = shape;
    this.text = Optional.of(this.shape.getLabeledShape())
        .map(LabeledRuminaqShape::getModelObject).map(Text::new).orElseThrow();
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
    return ECollections.unmodifiableEList(children);
  }

  @Override
  public Resource eResource() {
    return new NoResource();
  }
}
