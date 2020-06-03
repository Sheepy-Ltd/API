package com.sheepybot.api.entities.command.argument;

import com.google.common.collect.Lists;
import com.sheepybot.util.Objects;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class RawArguments implements Iterable<String> {

    private List<String> args;
    private int current;

    /**
     * @param args The raw arguments
     */
    public RawArguments(@NotNull(value = "args cannot be null") final List<String> args) {
        this.args = args;
        this.current = 0;
    }

    /**
     * @return The raw array
     */
    public List<String> getRaw() {
        return this.args;
    }

    /**
     * Retrieve the argument at the provided {@code pos}
     *
     * @param pos The argument position
     *
     * @return The argument, or {@code null} if {@code pos} is either less than 0 or greater than the array length
     */
    public String getRaw(final int pos) {
        if (pos > this.args.size() || pos < 0) {
            return null;
        }
        return this.args.get(pos);
    }

    /**
     * Set the value of the underlying array
     * <p>
     * <p>This resets the current position to 0</p>
     *
     * @param args The new args
     */
    public void setRaw(@NotNull(value = "args cannot be null") final List<String> args) {
        this.args = args;
        this.current = 0;
    }

    /**
     * Set the value of the underlying array
     * <p>
     * <p>This resets the current position to 0</p>
     *
     * @param args The new args
     */
    public void setRaw(@NotNull(value = "args cannot be null") final String[] args) {
        this.args = Lists.newArrayList(args);
        this.current = 0;
    }

    /**
     * @param amount The amount to drop
     *
     * @return This {@link RawArguments} instance
     *
     * @throws IllegalArgumentException If {@code amount} is greater than the total length of the internal array
     */
    public RawArguments drop(final int amount) throws IllegalArgumentException {
        if (amount > this.args.size()) {
            throw new IllegalArgumentException("Amount cannot be greater than total length");
        }

        final List<String> newArgs = this.args.stream().skip(amount).collect(Collectors.toList());

        if (this.current >= amount) {
            this.current -= amount;
        }

        this.args = newArgs;

        return this;
    }

    /**
     * Peek at the next argument in the internal array without incrementing the cursor position
     *
     * @return The next argument or {@code null} if there are no more arguments
     */
    public String peek() {
        return this.current == this.args.size() ? null : this.args.get(this.current);
    }

    /**
     * A convenience method to check for the presence of a next argument
     * <p>
     * <p>This is the equivalent of {@code #peek() != null}</p>
     *
     * @return {@code true} if there is another element, {@code false} otherwise
     */
    public boolean hasNext() {
        return this.peek() != null;
    }

    /**
     * Query the next argument incrementing the cursor position by 1 for each call
     * <p>
     * <p>A call to {@link #peek()} should be made before calling this method to prevent
     * an {@link ArrayIndexOutOfBoundsException} being thrown.</p>
     *
     * @return The next argument
     */
    public String next() {
        return this.args.get(this.current++);
    }

    /**
     * Rollback the current cursor position to its previous element
     */
    public void rollback() {
        Objects.checkArgument(this.current > 0, "cursor position cannot be less than 0");
        this.current--;
    }

    /**
     * Return a {@link String[]} of arguments
     *
     * @param start The zero-based position of which to start at
     * @return A {@link String[]} containing every point from {@code start} until the end
     * @throws IllegalArgumentException If the {@code start} is greater than the internal array length
     * @throws IllegalArgumentException If the {@code start} minus the internal array length is less than or equal to 0
     */
    public List<String> getParsed(final int start) throws IllegalArgumentException {
        Objects.checkArgument(start < this.args.size(), "start cannot be greater than total length");
        Objects.checkNotNegative(start, "start cannot be negative");
        Objects.checkNotNegative((this.args.size() - start), "cannot have a negative sized array");

        return this.args.stream().skip(start).collect(Collectors.toList());
    }

    /**
     * @return The total amount of arguments
     */
    public int length() {
        return this.args.size();
    }

    /**
     * @return A copy of this {@link RawArguments}
     */
    public RawArguments copy() {

        final RawArguments args = new RawArguments(this.args);
        args.current = this.current;

        return args;
    }

    @NotNull
    @Override
    public Iterator<String> iterator() {
        return new ArgumentIterator(this);
    }

    private static final class ArgumentIterator implements Iterator<String> {

        private final RawArguments args;

        ArgumentIterator(final RawArguments args) {
            this.args = args;
        }

        @Override
        public boolean hasNext() {
            return this.args.peek() != null;
        }

        @Override
        public String next() {
            return this.hasNext() ? this.args.next() : null;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

    }

}
