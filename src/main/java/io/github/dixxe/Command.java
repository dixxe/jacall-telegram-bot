package io.github.dixxe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/* Command handling class.
 * TODO: arguments support
 */
public class Command {
    private final String name;
    private final String description;
    public Command(String name, String description) {
        this.name = name;
        this.description = description;
    }

    protected String getInfo()  {
        return String.format("%s - %s\n", this.name, this.description);
    }

    public String getName() {
        return name;
    }

    // Static shortcut to parse arguments. I can't include them as property, because it will be inflexible.
    // Process commands in per-bot basis.
    public static List<String> proccesArguments(String messageWithCommand) {
        List<String> commandArgs = new ArrayList<>(Arrays.stream(messageWithCommand.split(" ")).toList());
        commandArgs.remove(0);
        return  commandArgs;
    }
}
