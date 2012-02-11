package net.stickycode.resource.codec;

import java.beans.Introspector;
import java.io.InputStream;
import java.io.OutputStream;

import javax.inject.Inject;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;
import javax.xml.transform.stream.StreamSource;

import net.stickycode.coercion.CoercionTarget;
import net.stickycode.resource.ResourceCodec;
import net.stickycode.stereotype.component.StickyExtension;
import net.stickycode.xml.jaxb.JaxbFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@StickyExtension
public class JaxbElementResourceCodec<T>
    implements ResourceCodec<T> {

  private Logger log = LoggerFactory.getLogger(getClass());

  @Inject
    private JaxbFactory jaxbFactory;

  @Override
  public void store(CoercionTarget sourceType, T resource, OutputStream outputStream) {
    Class<?> type = sourceType.getType();
    XmlType annotation = type.getAnnotation(XmlType.class);
    try {
      assert annotation != null;

      QName name = new QName(namespace(annotation), typeName(annotation, type));

      @SuppressWarnings({ "unchecked", "rawtypes" })
      JAXBElement element = new JAXBElement(name, type, resource);
      Marshaller m = jaxbFactory.createMarshaller(sourceType);
      m.marshal(element, outputStream);
    }
    catch (JAXBException e) {
      throw new ResourceEncodingFailureException(e, type, this);
    }
  }

  private String typeName(XmlType annotation, Class<?> type) {
    if (annotation.name().equals("##default"))
      return Introspector.decapitalize(type.getSimpleName());

    return annotation.name();
  }

  /**
   * TODO This is not sufficient it needs to check package annotations etc as well
   */
  private String namespace(XmlType annotation) {
    if (annotation.namespace().equals("##default"))
      return "";

    return annotation.namespace();
  }

  @Override
  public T load(InputStream source, CoercionTarget resourceTarget) {
    Class<T> type = (Class<T>) resourceTarget.getType();
    try {
      Unmarshaller u = jaxbFactory.createUnmarshaller(resourceTarget);
      return u.unmarshal(new StreamSource(source), type).getValue();
    }
    catch (JAXBException e) {
      throw new ResourceDecodingFailureException(e, type, this);
    }
  }

  @Override
  public boolean isApplicableTo(CoercionTarget type) {
    if (type.getType().isAnnotationPresent(XmlType.class))
      return true;
    
    if (type.getType().isAssignableFrom(JAXBElement.class))
      return true;

    return false;
  }

  @Override
  public String toString() {
    return getClass().getSimpleName();
  }

}
