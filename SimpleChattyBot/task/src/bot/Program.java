package bot;

import java.util.Scanner;
import java.net.*;
import java.io.*;

public class Program {
  public static void main(String[] args) {
    /*
    SimpleBot bot = new SimpleBot(
      "Alice", 
      "1998",
      new ConsoleInput(),
      new ConsoleOutput()
    );
    bot.run();
    */

    Server server = new Server(80);
    server.run();

    Client client = new Client();
    client.run(
      "https://SimpleChattyBot--lebedkun.repl.co",
      80
    );
  }

  
}

public class Client {
  public void run(String serverName, int port) {
    try {
      Socket client = new Socket(
        serverName,
        port
      );

      Input input = new NetInput(client);
      Output output = new NetOutput(client);

      System.out.println(input.nextLine()); // greet
      
      output.outLine("Alexa"); // remindName
      System.out.println(input.nextLine());

      System.out.println(input.nextLine()); // guessAge
      output.outLine("3 5 7");
      System.out.println(input.nextLine());
    } catch (UnknownHostException e) {
      System.out.println(e);
    } catch (IOException e) {
      System.out.println(e);
    }
  }
}

public class Server {
  private static final int TIMEOUT = 10000;
  private ServerSocket server;
  private SimpleBot bot;

  public Server(int port) {
    try {
      this.server = new ServerSocket(port);
      this.server.setSoTimeout(TIMEOUT);

      this.bot = new SimpleBot(
        "Alice", 
        "1998",
        null,
        null
      );
    } catch (IOException e) {
      System.out.println(e);
    }
  }

  public void run() {
    while (true) {
      try {
        Socket client = this.server.accept();

        this.bot.setInput(
          new NetInput(client)
        ).setOutput(
          new NetOutput(client)
        ).run();

        client.close();
      } catch (SocketTimeoutException e) {
        System.out.println("Socket timeout");
      } catch (IOException e) {
        System.out.println("Invalid input");
      }
    }
  }
}

public class NetInput implements Input {
  private DataInputStream input;

  public NetInput(Socket client) throws IOException {
    this.input = new DataInputStream(client.getInputStream());
  }

  public String nextLine() throws IOException {
    String line = this.input.readUTF();
    return line;
  }
}

public class NetOutput implements Output {
  private DataOutputStream output;

  public NetOutput(Socket client) throws IOException {
    this.output = new DataOutputStream(client.getOutputStream());
  }

  public void outLine(String message) throws IOException {
    this.output.writeUTF(message + "\n");
  }

  public void outFormat(String format, Object... args) throws IOException {
    this.output.writeUTF(
        String.format(format, args)
      );
  }
}

public class ConsoleInput implements Input {
  private Scanner scanner = new Scanner(System.in);

  public String nextLine() throws IOException {
    return this.scanner.nextLine();
  };
}

public class ConsoleOutput implements Output {
  public void outLine(String message) throws IOException {
    System.out.println(message);
  };

  public void outFormat(String format, Object... args) throws IOException {
    System.out.printf(format, args);
  };
}

public interface Input {
  public String nextLine() throws IOException;
}

public interface Output {
  public void outLine(String message) throws IOException;
  public void outFormat(String format, Object... args) throws IOException;
}

public class Converter {
  public static int[] stringToInt(String[] rawNumbers) {
    int[] numbers = new int[rawNumbers.length];
    for (int i = 0; i < numbers.length; i++) {
      numbers[i] = Integer.parseInt(rawNumbers[i]);
    }
    return numbers;
  }
}

public class SimpleBot {
    private String name;
    private String birthYear;
    private Input input;
    private Output output;

    final static Scanner scanner = new Scanner(System.in); // Do not change this line

    public SimpleBot(String name, String birthYear, Input input, Output output) {
      this.name = name;
      this.birthYear = birthYear;
      this.input = input;
      this.output = output;
    }

    protected void greet() {
      try {
        String format = "Hello! My name is %s.\n";
        format += "I was created in %s.\n";
        format += "Please, remind me your name.\n"; 

        this.output.outFormat(
          format,
          this.name,
          this.birthYear
        );
      } catch (IOException e) {
        System.out.println(e);
      }
    }

    protected void remindName() {
        try {
          String name = this.input.nextLine();
          this.output.outFormat(
            "What a great name you have, %s!\n",
            name
          );
        } catch (IOException e) {
          System.out.println(e);
        }
    }

    protected void guessAge() {
        try {
          this.output.outLine(
            "Let me guess your age.\n" +
            "Say me remainders of dividing your age by 3, 5 and 7. Separate them with space"
          );

          String line = this.input.nextLine();
          int[] remainders = Converter.stringToInt(
            line.split(" ", 3)
          );
          
          int age = (remainders[0] * 70 + remainders[1] * 21 + remainders[2] * 15) % 105;
          this.output.outFormat("Your age is %d; that's a good time to start programming!", age);
        } catch (IOException e) {
          System.out.println(e);
        } 
    }

    /*
    protected void count() {
        this.output.outLine("Now I will prove to you that I can count to any number you want.");
        int num = this.input.nextInt();
        for (int i = 0; i <= num; i++) {
            this.output.outFormat("%d!\n", i);
        }
    }
    */

    /*
    protected void test() {
        this.output.outLine("Let's test your programming knowledge.");

        this.output.outLine("Which of the following operators is used to allocate memory to an array?");
        String answ1 = ".create";
        String answ2 = ".aloc";
        String answ3 = ".new";
        String answ4 = ".array";

        int num1 = 1;
        int num2 = 2;
        int num3 = 3;
        int num4 = 4;

        this.output.outLine(num1 + answ1 + "\n" + num2 + answ2 + "\n" + num3 + answ3 + "\n" + num4 + answ4);
        int number;


        do {
            number = this.input.nextInt();
            {
                this.output.outLine("Please, try again.");
            }


        } while (number != 3);

    }
    */

    protected void end() {
        try {
          this.output.outLine("Congratulations, have a nice day!"); // Do not change this text
        } catch (IOException e) {
          System.out.println(e);
        }
    }

    public void run() {
          this.greet(); // change it as you need
          this.remindName();
          this.guessAge();
          // this.count();
          // this.test();
          this.end();
    }

    public SimpleBot setInput(Input input) {
      this.input = input;
      return this;
    }

    public SimpleBot setOutput(Output output) {
      this.output = output;
      return this;
    }
}