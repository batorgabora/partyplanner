package shared.model.service;

import java.beans.PropertyChangeListener;

/**
 * Defines observer registration methods for services that publish named
 * property change events.
 *  * @author Loke Hansen
 *  * @author Victor Tonu
 *  * @author Marko Stokic
 *  * @author Mike Lorenzen
 *  * @author Bator Gabora
 */
public interface ObservableService {
  /**
   * Registers a listener for a specific property or event name.
   * @param propertyName the property or event to observe
   * @param listener the listener to notify when the property changes
   */
  void addListener(String propertyName, PropertyChangeListener listener);

  /**
   * Removes a previously registered listener for a specific property or event
   * name.
   * @param propertyName the property or event the listener was registered for
   * @param listener the listener to remove
   */
  void removeListener(String propertyName, PropertyChangeListener listener);
}
