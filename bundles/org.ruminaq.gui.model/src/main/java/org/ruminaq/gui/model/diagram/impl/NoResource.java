package org.ruminaq.gui.model.diagram.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.ECollections;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;

public class NoResource implements Resource {

  @Override
  public EList<Adapter> eAdapters() {
    return null;
  }

  @Override
  public boolean eDeliver() {
    return false;
  }

  @Override
  public void eNotify(Notification arg0) {
  }

  @Override
  public void eSetDeliver(boolean arg0) {
  }

  @Override
  public void delete(Map<?, ?> arg0) throws IOException {
  }

  @Override
  public TreeIterator<EObject> getAllContents() {
    return null;
  }

  @Override
  public EList<EObject> getContents() {
    return ECollections.emptyEList();
  }

  @Override
  public EObject getEObject(String arg0) {
    return null;
  }

  @Override
  public EList<Diagnostic> getErrors() {
    return null;
  }

  @Override
  public ResourceSet getResourceSet() {
    return null;
  }

  @Override
  public long getTimeStamp() {
    return 0;
  }

  @Override
  public URI getURI() {
    return null;
  }

  @Override
  public String getURIFragment(EObject arg0) {
    return null;
  }

  @Override
  public EList<Diagnostic> getWarnings() {
    return null;
  }

  @Override
  public boolean isLoaded() {
    return false;
  }

  @Override
  public boolean isModified() {
    return false;
  }

  @Override
  public boolean isTrackingModification() {
    return false;
  }

  @Override
  public void load(Map<?, ?> arg0) throws IOException {
  }

  @Override
  public void load(InputStream arg0, Map<?, ?> arg1) throws IOException {
  }

  @Override
  public void save(Map<?, ?> arg0) throws IOException {
  }

  @Override
  public void save(OutputStream arg0, Map<?, ?> arg1) throws IOException {
  }

  @Override
  public void setModified(boolean arg0) {
  }

  @Override
  public void setTimeStamp(long arg0) {
  }

  @Override
  public void setTrackingModification(boolean arg0) {
  }

  @Override
  public void setURI(URI arg0) {
  }

  @Override
  public void unload() {
  }

}
