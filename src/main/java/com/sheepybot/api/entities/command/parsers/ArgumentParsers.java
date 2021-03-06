package com.sheepybot.api.entities.command.parsers;

import com.google.common.collect.Lists;
import com.sheepybot.api.entities.command.CommandContext;
import com.sheepybot.api.entities.command.argument.Argument;
import com.sheepybot.api.entities.command.argument.ArgumentParser;
import com.sheepybot.api.entities.command.argument.RawArguments;
import com.sheepybot.api.entities.utils.FinderUtil;
import com.sheepybot.api.exception.parser.ParserException;
import net.dv8tion.jda.api.entities.Member;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ArgumentParsers {

    /**
     * Parse text input.
     */
    public static final ArgumentParser<String> STRING = new ArgumentParser<String>() {

        @Override
        public String parse(final CommandContext context,
                            final RawArguments args) {
            if (args.peek() == null) {
                return null;
            }

            String first = args.next();
            final char qc = first.charAt(0);

            if (qc == '"' || qc == '\'') {

                final StringBuilder builder = new StringBuilder(first);

                boolean in = false;

                while (args.peek() != null) {

                    final String next = args.next();
                    final int len = next.length();

                    builder.append(' ');

                    for (int i = 0; i < len; i++) {
                        final char ch = next.charAt(i);
                        if (ch == '\\' && i + 1 < len && next.charAt(i + 1) == qc) {
                            builder.append(qc);
                            i++;
                        } else if (ch == qc) {
                            builder.append(qc);
                            if (!in) {
                                in = true;
                            } else {
                                if (i != len - 1) throw new ParserException("Missing end quote for input");
                                break;
                            }
                        } else {
                            builder.append(ch);
                        }
                    }
                }

                first = builder.toString();
                if (first.endsWith("\\" + qc) || !first.endsWith(qc + "") || first.equals("\"")) {
                    throw new ParserException("Missing end quote for input");
                }

                return first.substring(1, first.length() - 1);
            }

            return first;
        }

    };

    /**
     * Parses the remaining text input from passed {@link RawArguments} instance
     */
    public static final ArgumentParser<String> REMAINING_STRING = new ArgumentParser<String>() {

        @Override
        public String parse(final CommandContext context,
                            final RawArguments args) {
            final StringBuilder builder = new StringBuilder();
            while (args.peek() != null) {
                builder.append(STRING.parse(context, args)).append(" ");
            }
            return builder.toString().trim();
        }

    };

    /**
     * Parses the remaining text input from passed {@link RawArguments} instance
     */
    public static final ArgumentParser<String> REMAINING_STRING_NO_QUOTE = new ArgumentParser<String>() {

        @Override
        public String parse(final CommandContext context,
                            final RawArguments args) {
            final StringBuilder builder = new StringBuilder();
            while (args.peek() != null) {
                builder.append(args.next()).append(" ");
            }
            return builder.toString().trim();
        }

    };

    /**
     * Parse string input to an {@link Integer}
     */
    public static final ArgumentParser<Integer> INTEGER = new ArgumentParser<Integer>() {

        @Override
        public Integer parse(final CommandContext context,
                             final RawArguments args) {
            final String parsed = args.next();
            try {
                return Integer.parseInt(parsed);
            } catch (final NumberFormatException ex) {
                throw new ParserException(context.i18n("parserNumberInvalidNumber"));
            }
        }

    };

    /**
     * Parse string input to a {@link Long}
     */
    public static final ArgumentParser<Long> LONG = new ArgumentParser<Long>() {

        @Override
        public Long parse(final CommandContext context,
                          final RawArguments args) {
            final String parsed = args.next();
            try {
                return Long.parseLong(parsed);
            } catch (final NumberFormatException ex) {
                throw new ParserException(context.i18n("parserNumberInvalidNumber"));
            }
        }

    };

    /**
     * Parse string input to a {@link Double}
     */
    public static final ArgumentParser<Double> DOUBLE = new ArgumentParser<Double>() {

        @Override
        public Double parse(final CommandContext context,
                            final RawArguments args) {
            final String parsed = args.next();
            try {
                return Double.parseDouble(parsed);
            } catch (final NumberFormatException ex) {
                throw new ParserException(context.i18n("parserNumberInvalidNumber"));
            }
        }

    };

    /**
     * Attempts to retrieve a user by an input string from the local {@link net.dv8tion.jda.api.entities.Guild}, this can be any of
     * <ul>
     *     <li>Partial name</li>
     *     <li>Full name</li>
     *     <li>User ID</li>
     *     <li>@mention</li>
     * </ul>
     */
    public static final ArgumentParser<Member> MEMBER = new ArgumentParser<Member>() {

        @Override
        public Member parse(final CommandContext context,
                            final RawArguments args) {
            final StringBuilder builder = new StringBuilder();

            Member member = null;
            while (args.peek() != null) {

                builder.append(args.next()).append(" ");

                final List<Member> members = FinderUtil.findMembers(builder.toString().trim(), context.getGuild());
                if (members.size() == 0) {
                    args.rollback();
                    break;
                }

                member = members.get(0);

            }

            if (member == null) {
                throw new ParserException(context.i18n("parserUnknownMember", builder.toString()));
            }

            return member;
        }

    };

    /**
     * Apply a fallback parameter for a {@link ArgumentParser} in the event of none being specified
     *
     * <p>This fallback parameter may be {@code null}</p>
     *
     * @param parser   The parser
     * @param fallback The fallback parameter
     * @return The {@link ArgumentParser}
     */
    public static <T> ArgumentParser<T> alt(final ArgumentParser<T> parser,
                                            final T fallback) {
        return new ArgumentParser<T>() {

            @Override
            public Argument<T> getDefaultParameter() {
                return Argument.create(fallback);
            }

            @Override
            public T parse(final CommandContext context,
                           final RawArguments args) {
                return parser.parse(context, args);
            }

        };
    }

    /**
     * Creates an {@link ArgumentParser} that can resolve any parameter type with a corresponding parser
     *
     * @param parser1     a parser
     * @param parser2     another parser
     * @param moreParsers more parsers...
     * @return The {@link ArgumentParser}
     */
    public static ArgumentParser<Object> anyOf(@NotNull("parser1 cannot be null") final ArgumentParser<?> parser1,
                                               @NotNull("parser2 cannot be null") final ArgumentParser<?> parser2,
                                               final ArgumentParser<?>... moreParsers) {
        return new ArgumentParser<Object>() {

            private final ArgumentParser<?>[] parsers;

            {
                this.parsers = new ArgumentParser<?>[2 + moreParsers.length];

                this.parsers[0] = parser1;
                this.parsers[1] = parser2;

                System.arraycopy(moreParsers, 0, this.parsers, 2, moreParsers.length);
            }

            @Override
            public Object parse(final CommandContext context,
                                final RawArguments args) {
                final StringBuilder errors = new StringBuilder();
                for (final ArgumentParser<?> parser : this.parsers) {
                    try {
                        return parser.tryParse(context, args);
                    } catch (final Exception ex) {
                        errors.append(ex.getMessage()).append("\n");
                    }
                }
                throw new ParserException(errors.toString());
            }
        };
    }

    /**
     * Creates an {@link ArgumentParser} that will only accept any of the provided choices
     *
     * @param parser  The {@link ArgumentParser} used
     * @param choice  a choice
     * @param choices more choices
     * @return The {@link ArgumentParser}
     */
    public static <T> ArgumentParser<T> options(final ArgumentParser<T> parser,
                                                final String choice,
                                                final String... choices) {
        return new ArgumentParser<T>() {

            private final Set<String> options;
            private final String typename;

            {
                this.options = new HashSet<>();
                this.options.add(choice);
                this.options.addAll(Arrays.asList(choices));

                if (choices.length == 0) {
                    this.typename = choice;
                } else {
                    final StringBuilder builder = new StringBuilder(choice);
                    for (final String option : choices) {
                        builder.append('|').append(option);
                    }
                    this.typename = builder.toString().trim();
                }
            }

            @Override
            public T parse(final CommandContext context,
                           final RawArguments args) {
                final String parse = STRING.parse(context, args);

                for (final String option : this.options) {
                    if (option.equalsIgnoreCase(parse)) {
                        return parser.parse(context, new RawArguments(Lists.newArrayList(option)));
                    }
                }

                throw new ParserException(String.format("Invalid choice %s", parse));
            }

        };
    }

    private ArgumentParsers() {
    }

}
