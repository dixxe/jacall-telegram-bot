package io.github.dixxe;

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
}
