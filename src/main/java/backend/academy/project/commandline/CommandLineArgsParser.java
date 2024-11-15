package backend.academy.project.commandline;

import com.beust.jcommander.JCommander;

public class CommandLineArgsParser {

    private CommandLineArgsParser() {}

    public static CommandLineArgs getArgs(String[] args) {
        CommandLineArgs jArgs = new CommandLineArgs();
        JCommander helloCmd = JCommander.newBuilder()
            .addObject(jArgs)
            .build();
        helloCmd.parse(args);
        return jArgs;
    }
}
