package com.sheepybot;

public final class ExitCode {

    /**
     * Shutdown without restarting.
     */
    public static final int EXIT_CODE_NORMAL = 0;

    /**
     * Just restart.
     * <p>
     * <p>This is expected to be used when non-programmatic changes are made.
     * For example config changes or a new module is added / one is removed</p>
     */
    public static final int EXIT_CODE_RESTART = 1;

    /**
     * Apply new updates and restart.
     */
    public static final int EXIT_CODE_UPDATE = 2;

    private ExitCode() {
    }

}
