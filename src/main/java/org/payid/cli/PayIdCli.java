package org.payid.cli;

import org.payid.cli.shell.PayIdCliConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

/**
 * The Java <tt>main</tt> class that is executed to bootstrap the Spring Shell CLI.
 */
@SpringBootApplication
@Import(PayIdCliConfig.class)
public class PayIdCli {

  public static void main(String[] args) {
    SpringApplication.run(PayIdCli.class, args);
  }
}
