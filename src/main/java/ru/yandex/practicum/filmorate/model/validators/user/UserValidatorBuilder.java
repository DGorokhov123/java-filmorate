package ru.yandex.practicum.filmorate.model.validators.user;

public class UserValidatorBuilder {

    private UserValidator head;
    private UserValidator tail;

    public static UserValidatorBuilder builder() {
        return new UserValidatorBuilder();
    }

    public UserValidatorBuilder register(UserValidator validator) {
        if (tail == null) {
            head = validator;
        } else {
            tail.setNext(validator);
        }
        tail = validator;
        return this;
    }

    public UserValidator build() {
        if (head == null) throw new IllegalStateException("At least one validator must be added");
        return head;
    }

}
