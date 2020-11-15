package com.sheepybot.api.exception.module;

import org.jetbrains.annotations.NotNull;

public class InvalidModuleException extends Exception {

    /**
     * @param message A short explanation of what caused this {@link InvalidModuleException} to be thrown
     */
    public InvalidModuleException(@NotNull("message cannot be null") final String message) {
        super(message);
    }

    /**
     * @param cause The parent cause of this {@link InvalidModuleException}
     */
    public InvalidModuleException(@NotNull("cause cannot be null") final Throwable cause) {
        super(cause);
    }

    /**
     * @param message A short explanation of what caused this {@link InvalidModuleException} to be thrown
     * @param cause   The parent cause of this {@link InvalidModuleException}
     */
    public InvalidModuleException(@NotNull("message cannot be null") final String message,
                                  @NotNull("cause cannot be null") final Throwable cause) {
        super(message, cause);
    }
}
