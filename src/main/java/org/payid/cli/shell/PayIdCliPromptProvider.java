package org.payid.cli.shell;

import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStyle;
import org.springframework.shell.jline.PromptProvider;
import org.springframework.stereotype.Component;

/**
 * An implementation of {@link PromptProvider} that allows the Spring Shell prompt to be customized.
 */
@Component
public class PayIdCliPromptProvider implements PromptProvider {

  @Override
  public AttributedString getPrompt() {
    return new AttributedString("payid-cli:> ", AttributedStyle.DEFAULT.foreground(AttributedStyle.YELLOW));
  }
}
