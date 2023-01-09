package cf.baocai.androidrabbitmq;

public enum ActionEnum {
    SEND("send"), CONSUME("consume"), START_SERVER("start_server"), EMPTY("empty");

    private final String action;

    public String getAction() {
        return action;
    }

    ActionEnum(String action) {
        this.action = action;
    }

    public static ActionEnum fromString(String action) {
        for (ActionEnum b : ActionEnum.values()) {
            if (b.action.equalsIgnoreCase(action)) {
                return b;
            }
        }
        return null;
    }
}
