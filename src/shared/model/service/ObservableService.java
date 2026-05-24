package shared.model.service;

import java.beans.PropertyChangeListener;

public interface ObservableService {
  void addListener(String propertyName, PropertyChangeListener listener);
  void removeListener(String propertyName, PropertyChangeListener listener);
}