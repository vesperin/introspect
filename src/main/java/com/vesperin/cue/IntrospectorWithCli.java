package com.vesperin.cue;

import com.github.rvesse.airline.Cli;
import com.github.rvesse.airline.builder.CliBuilder;
import com.github.rvesse.airline.parser.errors.ParseException;
import com.vesperin.cue.cmds.CallableCommand;
import com.vesperin.cue.cmds.ConceptAssignmentCommand;
import com.vesperin.cue.cmds.RepresentativeAnalysisCommand;
import com.vesperin.cue.cmds.TypicalityAnalysisCommand;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @author Huascar Sanchez
 */
public class IntrospectorWithCli implements Introspector {
  private IntrospectorWithCli(){}

  /**
   * @return a new Introspector object.
   */
  public static Introspector newIntrospector(){
    return new IntrospectorWithCli();
  }

  /**
   * Builds its own CLI.
   *
   * @param builder non-configured CLI builder.
   * @return a configured CLI builder.
   */
  private Cli<CallableCommand> buildCli(CliBuilder<CallableCommand> builder){
    return Objects.requireNonNull(builder).build();
  }

  private static <T extends CallableCommand> void execute(T cmd) {
    try {
      final ExecutorService service = Executors.newSingleThreadExecutor();
      final Future<Integer> result  = service.submit(cmd);
      int exitCode = result.get();
      System.out.println();
      System.out.println("Exiting with Code " + exitCode);
      System.exit(exitCode);
    } catch (Throwable e) {
      System.err.println("Command threw error: " + e.getMessage());
      e.printStackTrace(System.err);
    }
  }


  private static <T extends CallableCommand> void executeCli(Cli<T> cli, String[] args) {
    try {
      T cmd = cli.parse(args);
      execute(cmd);
    } catch (ParseException e) {
      System.err.println("Parser error: " + e.getMessage());
    } catch (Throwable e) {
      System.err.println("Unexpected error: " + e.getMessage());
      e.printStackTrace(System.err);
    }
  }

  public static void main(String[] args) {

    final IntrospectorWithCli   cue = new IntrospectorWithCli();

    final Cli<CallableCommand>  cueCli = cue.buildCli(Cli.<CallableCommand>builder("cue")
      .withDescription("Cue CLI")
      .withCommand(TypicalityAnalysisCommand.class)
      .withCommand(ConceptAssignmentCommand.class)
      .withCommand(RepresentativeAnalysisCommand.class)
    );

    executeCli(cueCli, args);
  }

}