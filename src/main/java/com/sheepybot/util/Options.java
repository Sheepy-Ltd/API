package com.sheepybot.util;

import com.google.common.collect.Lists;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Options implements Iterator<Options.Option> {

    /**
     * The {@link Pattern} used to match a given string as a startup option.
     */
    private static final Pattern FLAG_PATTERN = Pattern.compile("--([\\w-]+)(?:\\s+(\\w+))?");

    private final List<Option> options;
    private int counter;

    private Options(final List<Option> options) {
        this.options = options;
    }

    /**
     * Retrieve an option by its name
     *
     * @param option The option
     * @return The {@link Option} or {@code null} if no option was present
     */
    public Option getOption(@NotNull("option cannot be null") final String option) {
        return this.options.stream().filter(opt -> opt.getName().equalsIgnoreCase(option)).findFirst().orElse(null);
    }

    @Override
    public boolean hasNext() {
        return (this.counter != this.options.size());
    }

    @Override
    public Option next() {
        if (!this.hasNext()) {
            throw new IllegalStateException("Cannot call #next when there are no more elements to iterate over");
        }
        return (this.options.get(this.counter++));
    }

    /**
     * Parse options from an array and return them as a {@link List}
     *
     * @param options The options to parse
     *
     * @return An {@link Options} instance
     */
    public static Options parse(final String[] options) {

        final Matcher matcher = FLAG_PATTERN.matcher(String.join(" ", options));
        final List<Option> parsed = Lists.newArrayList();

        while (matcher.find()) {

            final String flag = matcher.group(1);

            String value = null;
            if (matcher.groupCount() > 1) { //allows for people putting an = sign for a flag
                value = matcher.group(2);
            }

            parsed.add(new Option(flag, value));

        }

        return new Options(parsed);
    }

    /**
     * Parses options passed to the API at startup.
     */
    public static final class Option {

        private final String name;
        private final String value;

        /**
         * Create a new {@link Option}
         *
         * @param name  The flag name
         * @param value The flags value, or {@code null} if there is no value
         */
        public Option(@NotNull("name cannot be null") final String name,
                      final String value) {
            this.name = name;
            this.value = value;
        }

        /**
         * @return The name of this {@link Option}
         */
        public String getName() {
            return this.name;
        }

        /**
         * @return This {@link Option}s value, or {@code null} if there is no value
         */
        public String getValue() {
            return this.value;
        }

        /**
         * @return This {@link Option}s value as an {@link Integer}
         * @throws IllegalArgumentException If the {@code value} is {@code null}
         * @throws NumberFormatException    If the input value is not convertible to an {@link Integer}
         */
        public int getAsInt() throws IllegalArgumentException, NumberFormatException {
            if (this.value == null) {
                throw new IllegalArgumentException("Cannot convert a null value");
            }
            return Integer.parseInt(this.value);
        }

        /**
         * Attempts to convert the input flag value into an {@link Integer}
         * <p>
         * <p>Should the input value be {@code null}, {@code def} is returned</p>
         *
         * @param def The value to return should the input value be {@code null}
         * @return The {@link Integer} value, or {@code def} if no value was passed with the flag
         * @throws NumberFormatException If the input value is not convertible to a {@link Integer}
         */
        public int getAsInt(final int def) throws NumberFormatException {
            if (this.value == null) {
                return def;
            }
            return Integer.parseInt(this.value);
        }

        /**
         * @return This {@link Option}s value as a {@link Double}
         * @throws IllegalArgumentException If the {@code value} is {@code null}
         * @throws NumberFormatException    If the input value is not convertible to a {@link Double}
         */
        public double getAsDouble() throws IllegalArgumentException {
            if (this.value == null) {
                throw new IllegalArgumentException("Cannot convert a null value");
            }
            return Double.parseDouble(this.value);
        }

        /**
         * Attempts to convert the input flag value into a {@link Double}
         * <p>
         * <p>Should the input value be {@code null}, {@code def} is returned</p>
         *
         * @param def The value to return should the input value be {@code null}
         * @return The {@link Double} value, or {@code def} if no value was passed with the flag
         * @throws NumberFormatException If the input value is not convertible to a {@link Double}
         */
        public double getAsDouble(final int def) throws NumberFormatException {
            if (this.value == null) {
                return def;
            }
            return Double.parseDouble(this.value);
        }

        /**
         * @return This {@link Option}s value as a {@link Boolean}
         */
        public boolean getAsBoolean() throws IllegalArgumentException {
            return Boolean.parseBoolean(this.value);
        }

        /**
         * @return This {@link Option}s value as a {@link Boolean}, or {@code def} if no value was given to this {@link Option}
         */
        public boolean getAsBoolean(final boolean def) throws IllegalArgumentException {
            return this.value == null ? def : Boolean.parseBoolean(this.value);
        }

        @Override
        public String toString() {
            return "Option{name=" + this.name + ", value=" + this.value + "}";
        }
    }

}
