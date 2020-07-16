# API [![](https://jitpack.io/v/com.sheepybot/API.svg)](https://jitpack.io/#com.sheepybot/API)

This is a Bot API made by Samuel#0420, its purpose is to make the bot building process a lot easier
by giving you the tools you want for building a bot straight out of the box like command registering,
databases and event listening but is also here to try make things much simpler for the end users too.

This is made in spare time and in the hopes people will use it. If you like the project
and want to contribute please feel free to do so, any support is welcome.  

**Getting Started**

## Adding the dependency
The API doesn't have its own maven repository, instead chose to host it on JitPack

Adding this to your build.gradle file will enable you to begin the process of using the API
```groovy
    repositories {
        maven { url 'https://jitpack.io' }
    }

    dependencies {
        implementation group: 'com.sheepybot', name: 'API', version: '1.0.0_a297523264'
    }
```

## Creating Your First Module

### First steps

```java
import com.sheepybot.api.entities.module.ModuleData;
import com.sheepybot.api.entities.module.Module;

@ModuleData(name = "HelloWorld", version = "1.0.0")
public class HelloWorld extends Module {
    
    @Override
    public void onEnable() {
        this.getLogger().info("Hello World!");
    }
    
    @Override
    public void onDisable() {
        this.getLogger().info("Goodbye World!");
    }
    
}
```

**Overview**

We start off the day by creating main class for our Module, after creating it we annotate it with `ModuleData`,
this annotation contains all the information needed of a module and is essential part before it can begin to take it's first steps into the wild.

`@ModuleData#name`
This is just the name of the Module, a name just serves as an identifier for it so if something goes wrong then we know what went wrong
and can work to get things back on track.

`@ModuleData#version`
This is the current version of the Module. The version is useful for telling us if we've successfully updated or
if we're running on older code just to make sure everything is running smoothly. 

Major = Major changes to code that will break anything and everything in its path, this will be most likely to need changing when the API itself gets updated, perform thorough testing before considering release to a dev branch
Minor = Small changes, added features, nothings broken and things stay fresh and new.
Patch = Interchangeable update, upgrade/downgrade without any issues at all (bug fixes, performance improvement, code tweaks)

After this we then extend the `Module` class which signifies this as being the main class of a Module and is just as essential as the annotation, after extending 
it gives us the ability of overriding the methods `Module#onEnable` and `Module#onDisable` and is our second step to becoming a functional module.

`Module#onEnable`
 
This method is called after a Module has been enabled and is where all your initialization
of classes, registering commands, events and the like should go.

Commands and events can be registered past this method but it isn't advised (more of a consistency thing rather than risk of breaking something)

`Module#onDisable` 

This method is ran before your Modules disabled and should be used to close your resources.

Commands, events and scheduled tasks are automatically unregistered/terminated after your Modules disabled so there's no need
to do any of these yourself.

## The Command System

### Writing your first command
```java
import com.sheepybot.api.entities.command.CommandExecutor;
import com.sheepybot.api.entities.command.CommandContext;
import com.sheepybot.api.entities.command.Arguments;
import com.sheepybot.api.entities.command.parsers.ArgumentParsers;

import net.dv8tion.jda.api.entities.Member;

public class MyCommand implements CommandExecutor {
    
    @Override              
    public void execute(final CommandContext context, final Arguments args) {
        final Member member = args.next(ArgumentParsers.MEMBER);
        
        context.reply("Boop! " + member.getAsMention());
    }
    
}
```

**Overview**:

We create our new class called "MyCommand" implementing `CommandExecutor`, this is something that ALL commands MUST implement in order to be registered.

Implementing the `CommandExecutor#execute` method we get given two parameters

`CommandContext`

This contains information such as the user that executed the command, the channel it was executed in, the server etc, basically everything
that lead up to the command being triggered and some useful things like a translation class so we can respond to users in the language
they want to but we will talk more about that at another point.

`Arguments`

This contains the arguments that were executed with this command and other things like the flags executed with it.

On command execution we then retrieve some information from the arguments the user passed to us by calling `args.next(ArgumentParser)` and in this case there's only one argument, then we respond with a message giving the user a ping.

You might ask what if the user doesn't mention anyone, you will get a null pointer or something will fail.
However, in this case it's perfectly fine to call args.next(ArgumentParser) without fear of getting exceptions or having null pointers thrown at you (at least for inbuilt parsers, as you move on you can create your own and use them as you would any other),
should there be no argument given when you're expecting there to be one, or they give something entirely unrelated to it the command handler will manage this on its own and give the user a polite nudge saying how they should use the command.

There are parsers that don't mind not getting given a value, but they will either have defaults on them, or it is not required for the parser to function in how you use it.
Should the parser not have a default, or a default that isn't useful to you, you can also specify your own return value for a parser (be it in built, or your own) by calling.

```java
ArgumentParsers.alt(ArgumentParser<T> parser, T fallback)
```

First argument being the parser you want to give a different return value of and the second being the fallback argument of course being of the same type as the ArgumentParser returns.

There's a lot of options when it comes to processing arguments for your own command, you can even use multiple parsers of different return values, of course allowing for those
variations in possible responses in your code.

### Making your command usable
```java

...

@Override
public void onEnable() {
    
    final CommandRegistry commandRegistry = this.getCommandRegistry();

    commandRegistry.registerCommand(Command.builder()
             .names("boop", "boops")
             .executor(new MyCommand())
             .build());

}
```

**Overview**:

In our onEnable method we retrieve our CommandRegistry instance which is handily created for us by the API, after that
we can then create a new Command builder by calling `com.sheepybot.command.Command#builder`, this
is a useful method allowing you to configure your command for how it's to be used.

We specify our commands name (or trigger) this is what users can type to run our command,
and we also give an alias to give an alternate way to execute the command. Running the command with an alias
doesn't change the way a command will be processed and command aliases must be unique, giving your command an alias
already used by another command (within the bot not just your Module) will prevent it from being registered.

We also specify the actual executor for our new command so that the API knows what it's calling,
there are other methods in the builder, but these are the only ones we'll talk about now.

## The event system

### Listening to events
```java
import com.sheepybot.api.entities.event.EventListener;
import com.sheepybot.api.entities.event.EventHandler;

public class MyEventListener implements EventListener {

    @EventHandler
    public void onEvent(final GuildMessageReceivedEvent event) {
        System.out.println(String.format("%s sent a message in %s: %s", event.getGuild().getName(), event.getAuthor().getName(), event.getMessage().getDisplayContent()));
    }
    
}
```

**Overview**:

Just like any other class that will listen to events we must implement the EventListener class,
this is just to make sure we're giving the right thing when we register it and there's nothing to
inherit from the class itself.

In order to listen to an event we must first annotate a method with the @EventHandler annotation (the method name itself doesn't matter).
Once the event is fired it will be thrown around and will eventually land in our laps and we can proceed to deal with the information we were given 
by said event.

There are loads of events within the API all of which giving their own bit of information from users joining the guild
to people talking in voice channels, any bit of information we get there is likely an event for it.

The event system supports any event in JDA

### Listening to events
```java
import com.sheepybot.api.entities.module.EventRegistry;

...

@Override
public void onEnable() {
    ...
    final EventRegistry eventRegistry = this.getEventRegistry();

    eventRegistry.registerEvent(new MyEventListener());

}
```

**Overview**:

Now that we've created our event and added something to listen to it we now need a way of firing it,
Every module is given its own `EventRegistry` and similar to commands we can use it to register
our interest in an event so that when the time comes we're part of the cool kids club that get told about it.

We can also use it to call events as well (this is not limited to your own events), in this case we 
go to our event registry and call the `.register(EventListener)` method which takes our newly created `EventListener`
as its argument. After that, any time the event is fired your listener will get called.

### Final note
This guide will continue to grow and possibly end up being moved into their own sections
as the API and bot continue to grow themselves and demands change.

There's plenty of things we haven't covered but hopefully this gives some insight into how things
function, and the capability given to modules to become whatever they want to be.