package eu.koboo.getlogs.api.result;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public final class Result<RET, ERR> {

    RET returnValue;
    ERR errorValue;

    private Result(RET returnValue, ERR errorValue) {
        this.returnValue = returnValue;
        this.errorValue = errorValue;
    }

    public RET getValue() {
        return returnValue;
    }

    public ERR getError() {
        return errorValue;
    }

    public boolean hasValue() {
        return returnValue != null;
    }

    public boolean hasError() {
        return errorValue != null;
    }

    public boolean isSuccess() {
        return returnValue != null && errorValue == null;
    }

    public static <RET, ERR> Result<RET, ERR> success(RET returnValue) {
        return new Result<>(returnValue, null);
    }

    public static <RET, ERR> Result<RET, ERR> error(ERR errorValue) {
        return new Result<>(null, errorValue);
    }
}
