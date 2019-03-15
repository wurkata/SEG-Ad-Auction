package common;

import java.util.ArrayList;
import java.util.List;

public interface Observable {
    List<Observer> observers = new ArrayList<>();

    void addObserver(Observer o);
    void removeObserver(Observer o);
    void notifyObservers(Object arg);
    void notifyObservers();
}
