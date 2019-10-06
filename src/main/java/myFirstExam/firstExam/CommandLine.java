package myFirstExam.firstExam;

//        dir — выводит список файлов в текущей директории
//        cd «путь» — перейти в директорию, путь к которой задан первым аргументом
//        pwd — вывести полный путь до текущей директории
//        cat «имя_файла» - выводит содержимое текстового файла «имя_файла»

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

public class CommandLine {
    private static final String MSG_COMMAND_NOT_FOUND = "Command not found";
    private static final String MSG_DELIM = "==========================================";

    private Map<String, Command> commands;

    private String consoleEncoding;

    private  CommandLine(String consoleEncoding) {
        commands = new TreeMap<>();
        Command cmd = new HelpCommand();
        commands.put(cmd.getName(), cmd);
        cmd = new DirCommand();
        commands.put(cmd.getName(), cmd);
        cmd = new ExitCommand();
        commands.put(cmd.getName(), cmd);
        cmd = new PwdCommand();
        commands.put(cmd.getName(), cmd);
        cmd = new CdCommand();
        commands.put(cmd.getName(), cmd);
        cmd = new CatCommand();
        commands.put(cmd.getName(), cmd);
        this.consoleEncoding = consoleEncoding;
    }

    private void execute() {
        Context c = new Context();
        c.currentDirectory = new File(".").getAbsoluteFile();
        boolean result = true;
        Scanner scanner = new Scanner(System.in, consoleEncoding);
        do {
            System.out.print("> ");
            String fullCommand = scanner.nextLine();
            ParsedCommand pc = new ParsedCommand(fullCommand);
            if (pc.command == null || "".equals(pc.command)) {
                continue;
            }
            Command cmd = commands.get(pc.command.toUpperCase());
            if (cmd == null) {
                System.out.println(MSG_COMMAND_NOT_FOUND);
                continue;
            }
            result = cmd.execute(c, pc.args);
        } while (result);
    }

    public static void main(String[] args) {
        System.out.println("Добро пожаловать в командную строку!");
        System.out.println("Для получения справки введите help");
        CommandLine cp = new CommandLine("Cp1251");
        cp.execute();
    }

    class ParsedCommand {
        String command;
        String[] args;

        private ParsedCommand(String line) {
            String[] parts = line.split(" ");
            command = parts[0];
            if (parts.length > 1) {
                args = new String[parts.length - 1];
                System.arraycopy(parts, 1, args, 0, args.length);
            }
        }
    }

    interface Command {
        boolean execute(Context context, String... args);

        void printHelp();

        String getName();

        String getDescription();
    }

   public static class Context {
        private File currentDirectory;

        void setCurrentDirectory(File currentDirectory) {
            this.currentDirectory = currentDirectory;
        }
    }

    class HelpCommand implements Command {

        @Override
        public boolean execute(Context context, String... args) {
            if (args == null) {
                System.out.println("Avaliable commands:\n" + MSG_DELIM);
                for (Command cmd : commands.values()) {
                    System.out.println(cmd.getName() + ": " + cmd.getDescription());
                }
                System.out.println(MSG_DELIM);
            } else {
                for (String cmd : args) {
                    System.out.println("Help for command " + cmd + ":\n" + MSG_DELIM);
                    Command command = commands.get(cmd.toUpperCase());
                    if (command == null) {
                        System.out.println(MSG_COMMAND_NOT_FOUND);
                    } else {
                        command.printHelp();
                    }
                    System.out.println(MSG_DELIM);
                }
            }
            return true;
        }

        @Override
        public void printHelp() {
            System.out.println(getDescription());
        }

        @Override
        public String getName() {
            return "HELP";
        }

        @Override
        public String getDescription() {
            return "Prints list of available commands";
        }
    }

    static class CdCommand implements Command {

        @Override
        public boolean execute(Context context, String... args) {
            if (args == null) {
                String currentDir = new File(".").getAbsolutePath();
                System.out.println(currentDir);
            } else {
                File newDir = new File(args[0]);
                if (newDir.isDirectory()) {
                    CdHelp(newDir);
                } else {
                    System.out.println("It's don't directory");
                }
            }
            return true;
        }

        private void CdHelp(File f) {
            Context context = new Context();
            context.setCurrentDirectory(f);

        }

        @Override
        public void printHelp() {
            System.out.println(getDescription());
        }

        @Override
        public String getName() {
            return "CD";
        }

        @Override
        public String getDescription() {
            return "Go to the directory on the way";
        }
    }

    static class DirCommand implements Command {

        @Override
        public void printHelp() {
            System.out.println(getDescription());
        }

        @Override
        public boolean execute(Context context, String... args) {
            if (args == null) {
                printDir(context.currentDirectory);
            }
            return true;
        }

        @Override
        public String getName() {
            return "DIR";
        }

        private void printDir(File dir) {
            File[] files = dir.listFiles();
            if (files != null) {
                for (File f : files) {
                    if (f.isFile()) {
                        System.out.println(f.getName());
                    }
                }
            }
        }

        @Override
        public String getDescription() {
            return "Prints files current directory";
        }
    }

    static class PwdCommand implements Command {

        @Override
        public boolean execute(Context context, String... args) {
            if (args == null) {
                printPwd(context.currentDirectory);
            }
            return true;
        }

        private void printPwd(File dir) {
            //String pwd = new File(".").getAbsolutePath();
            System.out.println(dir);
        }

        @Override
        public void printHelp() {
            System.out.println(getDescription());
        }

        @Override
        public String getName() {
            return "PWD";
        }

        @Override
        public String getDescription() {
            return "Full path for current directory";
        }
    }
static class CatCommand implements Command{

    @Override
    public boolean execute(Context context, String... args) {
        if (args == null) {
            String currentDir = new File(".").getAbsolutePath();
            System.out.println(currentDir);
        } else {
            File newFile = new File(args[0]);
            if (newFile.isFile()) {
                printCat(newFile);
            } else {
                System.out.println("It's don't file");
            }
        }
        return true;
    }
private void printCat(File file){
    try(FileReader reader = new FileReader(file))
    {
        int c;
        while((c=reader.read())!=-1){

            System.out.print((char)c);
        }
    }
    catch(IOException ex){

        System.out.println(ex.getMessage());
    }
}
    @Override
    public void printHelp() {
        System.out.println(getDescription());
    }

    @Override
    public String getName() {
        return "CAT";
    }

    @Override
    public String getDescription() {
        return "output text file content";
    }
}
    static class ExitCommand implements Command {
        @Override
        public boolean execute(Context context, String... args) {
            System.out.println("Finishing command processor... done.");
            return false;
        }

        @Override
        public void printHelp() {
            System.out.println(getDescription());
        }

        @Override
        public String getName() {
            return "EXIT";
        }

        @Override
        public String getDescription() {
            return "Exits from command processor";
        }
    }
}
