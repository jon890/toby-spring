package chapter3.template_and_callback;

public interface LineCallback<T> {
    T doSomethingWithLine(String line, T value);
}
